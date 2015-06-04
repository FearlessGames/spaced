package se.spaced.client.environment.settings;

import se.spaced.client.environment.time.GameTime;

import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

public class KeyFrameSettingsProvider implements InterpolableProvider<EnvSettings> {

	private final KeyFrameSettingsInterpolator<SunSetting> sun;
	private final KeyFrameSettingsInterpolator<FogSetting> fog;
	private final KeyFrameSettingsInterpolator<SoundSetting> sound;

	public KeyFrameSettingsProvider(GameTime cycleTime, NavigableMap<GameTime, EnvSettings> keyframes) {

		NavigableMap<GameTime, SunSetting> sunFrames = new TreeMap<GameTime, SunSetting>();
		NavigableMap<GameTime, FogSetting> fogFrames = new TreeMap<GameTime, FogSetting>();
		NavigableMap<GameTime, SoundSetting> soundFrames = new TreeMap<GameTime, SoundSetting>();
		for (Map.Entry<GameTime, EnvSettings> entry : keyframes.entrySet()) {
			GameTime key = entry.getKey();
			EnvSettings value = entry.getValue();
			if (value.getFogSetting() != null) {
				fogFrames.put(key, value.getFogSetting());
			}
			if (value.getSunSetting() != null) {
				sunFrames.put(key, value.getSunSetting());
			}
			if (value.getSoundSetting() != null) {
				soundFrames.put(key, value.getSoundSetting());
			}
		}
		sun = new KeyFrameSettingsInterpolator<SunSetting>("sun", cycleTime, sunFrames);
		fog = new KeyFrameSettingsInterpolator<FogSetting>("fog", cycleTime, fogFrames);
		sound = new KeyFrameSettingsInterpolator<SoundSetting>("sound", cycleTime, soundFrames);
	}

	@Override
	public EnvSettings getSettings(GameTime t) {
		SunSetting sunSetting = sun.getSettings(t);
		FogSetting fogSetting = fog.getSettings(t);
		SoundSetting soundSetting = sound.getSettings(t);
		return new EnvSettingsImpl(sunSetting, fogSetting, soundSetting);
	}

}
