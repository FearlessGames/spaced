package se.spaced.client.environment;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import org.junit.Test;
import se.spaced.client.environment.settings.EmptyEnvSettings;
import se.spaced.client.environment.settings.EnvSettings;
import se.spaced.client.environment.settings.EnvironmentSettings;
import se.spaced.client.environment.settings.InterpolableProvider;
import se.spaced.client.environment.time.GameTime;
import se.spaced.client.environment.time.HourMinuteGameTimeManager;
import se.spaced.client.resources.zone.RootZoneService;
import se.spaced.shared.resources.zone.Zone;
import se.spaced.shared.world.TimeSystemInfo;
import se.spaced.shared.xml.XmlIO;

import java.util.Map;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static se.mockachino.Mockachino.*;

public class ZoneEnvironmentProviderTest {
	@Test
	public void loadSimple() throws Exception {
		Zone zone = mock(Zone.class);
		String envSettingsFileName = "envSettingsFile";
		when(zone.getEnvironmentDayCycleSettingsFile()).thenReturn(envSettingsFileName);
		XmlIO xmlIO = mock(XmlIO.class);
		EnvironmentSettings environmentSettings = mock(EnvironmentSettings.class);
		Map<GameTime, EnvSettings> settingsMap = ImmutableMap.of(new GameTime(100), (EnvSettings) EmptyEnvSettings.INSTANCE);
		when(environmentSettings.getSettingsByGameTime()).thenReturn(settingsMap);
		when(xmlIO.load(EnvironmentSettings.class, envSettingsFileName)).thenReturn(environmentSettings);

		ZoneEnvironmentProvider zoneEnvironmentProvider = new ZoneEnvironmentProvider(xmlIO, new HourMinuteGameTimeManager(new TimeSystemInfo(10, 10, 10, 1.0)), mock(RootZoneService.class));
		zoneEnvironmentProvider.onReload(zone);

		InterpolableProvider<EnvSettings> provider = zoneEnvironmentProvider.getEnvironmentSettings(zone);
		assertNotNull(provider);

		provider = zoneEnvironmentProvider.getEnvironmentSettings(mock(Zone.class));
		assertNull(provider);
	}

	@Test
	public void loadWithSubzones() throws Exception {
		Zone zone = mock(Zone.class);
		Zone subZone = mock(Zone.class);
		when(zone.getSubzones()).thenReturn(Lists.newArrayList(subZone));
		String envSettingsFileName = "envSettingsFile";
		when(zone.getEnvironmentDayCycleSettingsFile()).thenReturn(envSettingsFileName);
		when(subZone.getEnvironmentDayCycleSettingsFile()).thenReturn(envSettingsFileName);

		XmlIO xmlIO = mock(XmlIO.class);
		EnvironmentSettings environmentSettings = mock(EnvironmentSettings.class);
		Map<GameTime, EnvSettings> settingsMap = ImmutableMap.of(new GameTime(100), (EnvSettings) EmptyEnvSettings.INSTANCE);
		when(environmentSettings.getSettingsByGameTime()).thenReturn(settingsMap);
		when(xmlIO.load(EnvironmentSettings.class, envSettingsFileName)).thenReturn(environmentSettings);

		ZoneEnvironmentProvider zoneEnvironmentProvider = new ZoneEnvironmentProvider(xmlIO, new HourMinuteGameTimeManager(new TimeSystemInfo(10, 10, 10, 1.0)), mock(RootZoneService.class));
		zoneEnvironmentProvider.onReload(zone);

		InterpolableProvider<EnvSettings> provider = zoneEnvironmentProvider.getEnvironmentSettings(subZone);
		assertNotNull(provider);
	}
}
