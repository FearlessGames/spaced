package se.spaced.client.environment;

import com.ardor3d.math.ColorRGBA;
import com.ardor3d.renderer.Camera;
import org.junit.Before;
import org.junit.Test;
import se.ardortech.math.SpacedVector3;
import se.fearlessgames.common.util.TimeProvider;
import se.mockachino.annotations.*;
import se.spaced.client.ardor.Water;
import se.spaced.client.environment.components.Fog;
import se.spaced.client.environment.components.SphereSky;
import se.spaced.client.environment.components.Sun;
import se.spaced.client.environment.settings.EnvSettings;
import se.spaced.client.environment.settings.EnvSettingsImpl;
import se.spaced.client.environment.settings.FogSetting;
import se.spaced.client.environment.settings.InterpolableProvider;
import se.spaced.client.environment.settings.SoundSetting;
import se.spaced.client.environment.settings.SunSetting;
import se.spaced.client.environment.time.GameTime;
import se.spaced.client.environment.time.GameTimeManager;
import se.spaced.client.model.ClientEntity;
import se.spaced.client.model.UserCharacter;
import se.spaced.client.resources.zone.ZoneActivationService;
import se.spaced.client.sound.music.AmbientSystem;
import se.spaced.client.sound.music.SoundChannel;
import se.spaced.shared.resources.zone.Zone;

import java.util.Arrays;
import java.util.HashSet;

import static se.mockachino.Mockachino.*;
import static se.mockachino.matchers.Matchers.*;

public class EnvironmentSystemTest {
	private EnvironmentSystem environmentSystem;
	@Mock
	private GameTimeManager gameTimeManager;
	@Mock
	private AmbientSystem ambientSystem;
	@Mock
	private Water water;
	@Mock
	private SphereSky sky;
	@Mock
	private TimeProvider timeProvider;
	@Mock
	private Fog fog;
	@Mock
	private Sun sun;
	@Mock
	Camera camera;
	@Mock
	InterpolableProvider<EnvSettings> settingsProvider;

	@Mock
	private UserCharacter userCharacter;

	@Mock
	private ZoneActivationService zoneActivationService;

	@Mock
	private Zone zone;
	@Mock
	private ZoneEnvironmentProvider zoneEnvironmentProvider;

	@Before
	public void setUp() {
		setupMocks(this);

		stubReturn(10.0).on(zone).getEnvironmentWeight(any(SpacedVector3.class));
		stubReturn(mock(ClientEntity.class)).on(userCharacter).getUserControlledEntity();
		stubReturn(new HashSet<Zone>(Arrays.asList(zone))).on(zoneActivationService).getNearbyZones(any(SpacedVector3.class),
				anyDouble());
		when(zoneActivationService.getRootZone()).thenReturn(zone);
		when(zoneEnvironmentProvider.getEnvironmentSettings(zone)).thenReturn(settingsProvider);
		environmentSystem = new EnvironmentSystem(gameTimeManager, sun, fog, timeProvider, sky,
				ambientSystem, userCharacter, zoneActivationService, zoneEnvironmentProvider);
	}

	@Test
	public void testChangeSettingsProvider() {
		GameTime gameTime = new GameTime(1L);
		stubReturn(gameTime).on(gameTimeManager).fromSystemTime(any(Long.class));
		ColorRGBA white = new ColorRGBA(1f, 1f, 1f, 1f);
		SunSetting sunSetting = new SunSetting(white, white, white);
		FogSetting fogSetting = new FogSetting(white, 1, 1, 1);
		SoundSetting soundSetting = new SoundSetting("soundFile", SoundChannel.LOOP1, false);
		EnvSettings setting = new EnvSettingsImpl(sunSetting, fogSetting, soundSetting);
		stubReturn(setting).on(settingsProvider).getSettings(gameTime);
		environmentSystem.update(camera, 0);
		verifyExactly(1).on(sun).setCurrentSettings(sunSetting);
		verifyExactly(1).on(fog).setCurrentSettings(fogSetting);
		verifyExactly(1).on(ambientSystem).setCurrentSettings(soundSetting);
	}
}
