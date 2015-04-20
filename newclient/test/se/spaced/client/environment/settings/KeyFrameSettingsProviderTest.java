package se.spaced.client.environment.settings;

import org.junit.Before;
import org.junit.Test;
import se.mockachino.annotations.*;
import se.spaced.client.environment.time.GameTime;

import java.util.NavigableMap;
import java.util.TreeMap;

import static org.junit.Assert.assertEquals;
import static se.mockachino.Mockachino.*;

public class KeyFrameSettingsProviderTest {

	@Mock
	GameTime gameTime;
	@Mock
	EnvSettingsImpl envSetting;
	@Mock
	SunSetting sunSetting;
	@Mock
	FogSetting fogSetting;
	@Mock
	SoundSetting soundSetting;


	@Before
	public void setUp() {
		setupMocks(this);
	}

	@Test
	public void testGetSettings() throws Exception {
		NavigableMap<GameTime, EnvSettings> envSettingsNavigableMap = new TreeMap<GameTime, EnvSettings>();
		envSettingsNavigableMap.put(new GameTime(1L), envSetting);
		stubReturn(sunSetting).on(envSetting).getSunSetting();
		stubReturn(fogSetting).on(envSetting).getFogSetting();
		stubReturn(soundSetting).on(envSetting).getSoundSetting();
		KeyFrameSettingsProvider keyFrameSettingsProvider = new KeyFrameSettingsProvider(gameTime,
				envSettingsNavigableMap);
		assertEquals(sunSetting, keyFrameSettingsProvider.getSettings(new GameTime(0)).getSunSetting());
		assertEquals(fogSetting, keyFrameSettingsProvider.getSettings(new GameTime(0)).getFogSetting());
		assertEquals(soundSetting, keyFrameSettingsProvider.getSettings(new GameTime(0)).getSoundSetting());
	}
}
