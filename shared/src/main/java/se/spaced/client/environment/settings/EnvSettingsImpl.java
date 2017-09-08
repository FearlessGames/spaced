package se.spaced.client.environment.settings;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("envsettings")
public class EnvSettingsImpl implements EnvSettings {
	private final SunSetting sunSetting;
	private final FogSetting fogSetting;
	private final SoundSetting soundSetting;

	public EnvSettingsImpl(SunSetting sunSetting, FogSetting fogSetting, SoundSetting soundSetting) {
		this.sunSetting = sunSetting;
		this.fogSetting = fogSetting;
		this.soundSetting = soundSetting;
	}

	@Override
	public SunSetting getSunSetting() {
		return sunSetting;
	}

	@Override
	public FogSetting getFogSetting() {
		return fogSetting;
	}

	@Override
	public SoundSetting getSoundSetting() {
		return soundSetting;
	}

	@Override
	public EnvSettings interpolate(EnvSettings other, float pos) {
		return new EnvSettingsImpl(
				sunSetting.interpolate(other.getSunSetting(), pos),
				fogSetting.interpolate(other.getFogSetting(), pos),
				soundSetting.interpolate(other.getSoundSetting(), pos));
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		EnvSettingsImpl envSettings = (EnvSettingsImpl) o;

		if (fogSetting != null ? !fogSetting.equals(envSettings.fogSetting) : envSettings.fogSetting != null) {
			return false;
		}
		if (soundSetting != null ? !soundSetting.equals(envSettings.soundSetting) : envSettings.soundSetting != null) {
			return false;
		}
		return sunSetting != null ? sunSetting.equals(envSettings.sunSetting) : envSettings.sunSetting == null;
	}

	@Override
	public int hashCode() {
		int result = sunSetting != null ? sunSetting.hashCode() : 0;
		result = 31 * result + (fogSetting != null ? fogSetting.hashCode() : 0);
		result = 31 * result + (soundSetting != null ? soundSetting.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "EnvSettings{" +
				"sunSetting=" + sunSetting +
				", fogSetting=" + fogSetting +
				", soundSetting=" + soundSetting +
				'}';
	}
}
