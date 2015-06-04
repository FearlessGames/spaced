package se.spaced.client.environment.settings;

public interface EnvSettings extends Interpolable<EnvSettings> {
	SunSetting getSunSetting();

	FogSetting getFogSetting();

	SoundSetting getSoundSetting();

}
