package se.spaced.client.resources.zone;

import com.ardor3d.math.ColorRGBA;
import org.junit.Before;
import org.junit.Test;
import se.ardortech.math.Sphere;
import se.mockachino.annotations.*;
import se.spaced.client.environment.settings.EnvSettingsImpl;
import se.spaced.client.environment.settings.EnvironmentSettings;
import se.spaced.client.environment.settings.FogSetting;
import se.spaced.client.environment.settings.SoundSetting;
import se.spaced.client.environment.settings.SunSetting;
import se.spaced.client.environment.time.GameTime;
import se.spaced.client.environment.time.GameTimeManager;
import se.spaced.client.model.Prop;
import se.spaced.client.sound.music.SoundChannel;
import se.spaced.shared.resources.zone.Zone;
import se.spaced.shared.xml.XmlIO;
import se.spaced.shared.xml.XmlIOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static se.mockachino.Mockachino.*;
import static se.mockachino.matchers.Matchers.*;


public class ZoneXmlReaderImplTest {

	@Mock
	XmlIO xmlIO;
	@Mock
	GameTimeManager gameTimeManager;

	@Before
	public void setup() {
		setupMocks(this);
		zli = new ZoneXmlFileHandler(xmlIO);
	}

	ZoneXmlFileHandler zli;

	@Test
	public void testThatWeCanLoadBothInlineAndFileTemplatedZones() throws XmlIOException {
		Zone loadingZone = new Zone("dark forest", new Sphere(null, 0));
		loadingZone.getSubzoneFiles().add("smallgreenhill.zone");
		Prop p = new Prop("xmo1", null, null, null);


		Zone subZoneAfterLoadFromTemplate = new Zone("Loaded Zone", new Sphere(null, 0));
		subZoneAfterLoadFromTemplate.getProps().add(p);

		Zone subZoneInlined = new Zone("Inlined Zone", new Sphere(null, 0));
		subZoneInlined.getProps().add(p);

		loadingZone.getProps().add(p);
		loadingZone.getSubzones().add(subZoneInlined);

		stubReturn(loadingZone).on(xmlIO).load(Zone.class, "a");
		stubReturn(subZoneAfterLoadFromTemplate).on(xmlIO).load(Zone.class, "smallgreenhill.zone");
		Zone zone = zli.loadRootZone("a");

		assertTrue(zone.getSubzones().contains(subZoneAfterLoadFromTemplate));
		assertTrue(zone.getSubzones().contains(subZoneInlined));

	}

	@Test
	public void testThatWeCanLoadEnvironmentSettings() throws XmlIOException {
		ColorRGBA white = new ColorRGBA(1f, 1f, 1f, 1f);
		Zone loadingZone = new Zone("dark forest", new Sphere(null, 0));
		loadingZone.setEnvironmentDayCycleSettingsFile("file1");
		Zone subZone = new Zone("Sub forest", new Sphere(null, 0));
		loadingZone.getSubzones().add(subZone);
		EnvironmentSettings es = new EnvironmentSettings();
		EnvSettingsImpl setting = new EnvSettingsImpl(new SunSetting(white, white, white),
				new FogSetting(white, 1, 1, 1),
				new SoundSetting("soundFile", SoundChannel.LOOP1, true));
		es.getSettingsByGameTime().put(new GameTime(10L), setting);
		stubReturn(es).on(xmlIO).load(EnvironmentSettings.class, eq("file1"));
		stubReturn(loadingZone).on(xmlIO).load(Zone.class, "a");
		Zone zone = zli.loadRootZone("a");
		assertEquals("file1", zone.getEnvironmentDayCycleSettingsFile());
	}
}
