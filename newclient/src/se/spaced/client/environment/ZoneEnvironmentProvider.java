package se.spaced.client.environment;

import com.google.common.collect.Maps;
import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.spaced.client.environment.settings.EnvSettings;
import se.spaced.client.environment.settings.EnvironmentSettings;
import se.spaced.client.environment.settings.InterpolableProvider;
import se.spaced.client.environment.settings.KeyFrameSettingsProvider;
import se.spaced.client.environment.time.GameTime;
import se.spaced.client.environment.time.GameTimeManager;
import se.spaced.client.resources.zone.RootZoneService;
import se.spaced.client.resources.zone.RootZoneServiceListener;
import se.spaced.shared.resources.zone.Zone;
import se.spaced.shared.xml.XmlIO;
import se.spaced.shared.xml.XmlIOException;

import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

public class ZoneEnvironmentProvider implements RootZoneServiceListener {
	private final Logger log = LoggerFactory.getLogger(getClass());

	private final XmlIO xmlIO;
	private final GameTimeManager gameTimeManager;
	private final Map<Zone, InterpolableProvider<EnvSettings>> environmentSettingsByZone = Maps.newHashMap();

	@Inject
	public ZoneEnvironmentProvider(XmlIO xmlIO, GameTimeManager gameTimeManager, RootZoneService rootZoneService) {
		this.xmlIO = xmlIO;
		this.gameTimeManager = gameTimeManager;
		rootZoneService.addListener(this);
	}

	public InterpolableProvider<EnvSettings> getEnvironmentSettings(Zone zone) {
		return environmentSettingsByZone.get(zone);
	}

	@Override
	public void onReload(Zone rootZone) {
		loadEnviroment(rootZone);
	}

	private void loadEnviroment(Zone zone) {
		String settingsFile = zone.getEnvironmentDayCycleSettingsFile();
		if (settingsFile != null) {
			EnvironmentSettings envSettings = loadSettings(settingsFile);
			if (envSettings != null) {
				NavigableMap<GameTime, EnvSettings> settings = new TreeMap<GameTime, EnvSettings>(envSettings.getSettingsByGameTime());
				InterpolableProvider<EnvSettings> settingsProvider = new KeyFrameSettingsProvider(
						gameTimeManager.getCycleTime(),
						settings);
				environmentSettingsByZone.put(zone, settingsProvider);
			}
		}
		for (Zone subZone : zone.getSubzones()) {
			loadEnviroment(subZone);
		}
	}

	private EnvironmentSettings loadSettings(String settingsFile) {
		try {
			return xmlIO.load(EnvironmentSettings.class, settingsFile);
		} catch (XmlIOException e) {
			log.warn("Failed to load environment settings file" + settingsFile);
		}
		return new EnvironmentSettings();
	}

}
