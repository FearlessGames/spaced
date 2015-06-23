package se.spaced.client.environment;

import com.ardor3d.math.ColorRGBA;
import com.ardor3d.math.type.ReadOnlyColorRGBA;
import org.junit.Before;
import org.junit.Test;
import se.ardortech.math.SpacedVector3;
import se.ardortech.math.Sphere;
import se.spaced.client.environment.settings.EnvSettings;
import se.spaced.client.environment.settings.EnvSettingsImpl;
import se.spaced.client.environment.settings.FogSetting;
import se.spaced.client.environment.settings.SoundSetting;
import se.spaced.client.environment.settings.StaticEnvironmentSettingsProvider;
import se.spaced.client.environment.settings.SunSetting;
import se.spaced.client.environment.time.GameTime;
import se.spaced.client.resources.zone.ZoneActivationListener;
import se.spaced.client.resources.zone.ZoneActivationService;
import se.spaced.client.resources.zone.ZoneActivationServiceImpl;
import se.spaced.client.resources.zone.ZoneChangedListener;
import se.spaced.client.sound.music.SoundChannel;
import se.spaced.shared.resources.zone.Zone;

import static org.junit.Assert.assertEquals;
import static se.mockachino.Mockachino.*;

public class ZoneInterpolationStrategyTest {
	private static final float EPSILON = 0.001f;

	private ZoneActivationService activation;
	private ZoneInterpolationStrategy strategy;
	private ZoneEnvironmentProvider zoneEnvironmentProvider;

	ColorRGBA white = new ColorRGBA(1, 1, 1, 1);
	ColorRGBA gray = new ColorRGBA(0.5f, 0.5f, 0.5f, 1);
	ColorRGBA black = new ColorRGBA(0, 0, 0, 1);
	ColorRGBA lightgray = new ColorRGBA(2.0f / 3, 2.0f / 3, 2.0f / 3, 1);

	EnvSettings whiteSetting = build(white);
	EnvSettingsImpl graySetting = build(gray);
	EnvSettings blackSetting = build(black);
	EnvSettings lightgraySetting = build(lightgray);

	@Before
	public void setup() {
		activation = new ZoneActivationServiceImpl(mock(ZoneActivationListener.class), mock(ZoneChangedListener.class));
		zoneEnvironmentProvider = mock(ZoneEnvironmentProvider.class);
		strategy = new ZoneInterpolationStrategy(activation, zoneEnvironmentProvider);
	}

	@Test
	public void testNoInnerFade() {
		Zone child = new Zone("child", new Sphere(SpacedVector3.ZERO, 50), null, null, null, 10.0, 1, null);
		when(zoneEnvironmentProvider.getEnvironmentSettings(child)).thenReturn(new StaticEnvironmentSettingsProvider(whiteSetting));
		Zone root = new Zone("root",
				new Sphere(SpacedVector3.ZERO, 100),
				null,
				null,
				null,
				10.0,
				1,
				null);
		root.addSubZone(child);
		child.setParentZone(root);

		when(zoneEnvironmentProvider.getEnvironmentSettings(root)).thenReturn(new StaticEnvironmentSettingsProvider(blackSetting));


		activation.setRootZone(root, SpacedVector3.ZERO, 1000);

		EnvSettings settings = strategy.getSettings(new GameTime(0), SpacedVector3.ZERO);
		assertEquals(graySetting.toString() + "\n", settings.toString() + "\n");
	}


	@Test
	public void testFullInnerFade() {
		Zone child = new Zone("child", new Sphere(SpacedVector3.ZERO, 50), null, null, null, 10.0, 1, 20.0);
		when(zoneEnvironmentProvider.getEnvironmentSettings(child)).thenReturn(new StaticEnvironmentSettingsProvider(whiteSetting));
		Zone root = new Zone("root",
				new Sphere(SpacedVector3.ZERO, 100),
				null,
				null,
				null,
				10.0,
				1,
				null);
		root.addSubZone(child);
		child.setParentZone(root);

		when(zoneEnvironmentProvider.getEnvironmentSettings(root)).thenReturn(new StaticEnvironmentSettingsProvider(blackSetting));

		activation.setRootZone(root, SpacedVector3.ZERO, 1000);

		EnvSettings settings = strategy.getSettings(new GameTime(0), SpacedVector3.ZERO);
		assertEnvSettings(whiteSetting, settings);
	}

	@Test
	public void testPartialInnerFade() {
		Zone child = new Zone("child", new Sphere(SpacedVector3.ZERO, 50), null, null, null, 20.0, 1, 20.0);
		when(zoneEnvironmentProvider.getEnvironmentSettings(child)).thenReturn(new StaticEnvironmentSettingsProvider(whiteSetting));
		Zone root = new Zone("root",
				new Sphere(SpacedVector3.ZERO, 100),
				null,
				null,
				null,
				10.0,
				1,
				null);
		when(zoneEnvironmentProvider.getEnvironmentSettings(root)).thenReturn(new StaticEnvironmentSettingsProvider(blackSetting));
		root.addSubZone(child);
		child.setParentZone(root);

		activation.setRootZone(root, SpacedVector3.ZERO, 1000);

		EnvSettings settings = strategy.getSettings(new GameTime(0), new SpacedVector3(40, 0, 0));

		assertEnvSettings(lightgraySetting, settings);

	}

	private void assertEnvSettings(EnvSettings settings1, EnvSettings settings2) {
		assertColor(settings1.getSunSetting().getDiffuseColor(), settings2.getSunSetting().getDiffuseColor());
		assertColor(settings1.getSunSetting().getAmbientColor(), settings2.getSunSetting().getAmbientColor());
		assertColor(settings1.getSunSetting().getEmissiveColor(), settings2.getSunSetting().getEmissiveColor());

		assertColor(settings1.getFogSetting().getColor(), settings2.getFogSetting().getColor());
		assertEquals(settings1.getFogSetting().getDensity(), settings2.getFogSetting().getDensity(), EPSILON);
		assertEquals(settings1.getFogSetting().getEnd(), settings2.getFogSetting().getEnd(), EPSILON);
		assertEquals(settings1.getFogSetting().getStart(), settings2.getFogSetting().getStart(), EPSILON);

		assertEquals(settings1.getSoundSetting(), settings2.getSoundSetting());
	}


	private void assertColor(ReadOnlyColorRGBA a, ReadOnlyColorRGBA b) {
		assertEquals(a.getAlpha(), b.getAlpha(), EPSILON);
		assertEquals(a.getBlue(), b.getBlue(), EPSILON);
		assertEquals(a.getGreen(), b.getGreen(), EPSILON);
		assertEquals(a.getRed(), b.getRed(), EPSILON);
	}

	private static EnvSettingsImpl build(ColorRGBA color) {
		SunSetting sun = new SunSetting(color, color, color);
		FogSetting fog = new FogSetting(color, 0, 1, 1);
		return new EnvSettingsImpl(sun, fog, new SoundSetting("", SoundChannel.LOOP1, true));
	}

}
