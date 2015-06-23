package se.spaced.client.environment.settings;

import com.ardor3d.math.ColorRGBA;
import se.spaced.client.sound.music.SoundChannel;

public class EmptyEnvSettings implements EnvSettings {
	private static final ColorRGBA BLACK = new ColorRGBA(0f, 0f, 0f, 0f);
	private static final SunSetting SUN = new SunSetting(BLACK, BLACK, BLACK);
	private static final SoundSetting SOUND = new SoundSetting("", SoundChannel.LOOP1, true);
	private static final FogSetting FOG = new FogSetting(BLACK, 0, 1, 1);

	public static final EmptyEnvSettings INSTANCE = new EmptyEnvSettings();

	private EmptyEnvSettings() {

	}
	
	@Override
	public SunSetting getSunSetting() {
		return SUN;
	}

	@Override
	public FogSetting getFogSetting() {
		return FOG;
	}

	@Override
	public SoundSetting getSoundSetting() {
		return SOUND;
	}

	@Override
	public EnvSettings interpolate(EnvSettings other, float pos) {
		return other;
	}
}
