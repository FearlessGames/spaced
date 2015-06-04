package se.spaced.client.environment.settings;

import se.spaced.client.sound.music.SoundChannel;

public class SoundSetting implements Interpolable<SoundSetting> {
	private String soundFile;
	private SoundChannel soundChannel;
	private boolean stop;

	public SoundSetting(String soundFile, SoundChannel soundChannel, boolean stop) {
		this.soundFile = soundFile;
		this.soundChannel = soundChannel;
		this.stop = stop;
	}

	public boolean isStop() {
		return stop;
	}

	public String getSoundFile() {
		return soundFile;
	}

	public SoundChannel getSoundChannel() {
		return soundChannel;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof SoundSetting)) {
			return false;
		}

		SoundSetting that = (SoundSetting) o;

		if (soundChannel != that.soundChannel) {
			return false;
		}
		if (soundFile != null ? !soundFile.equals(that.soundFile) : that.soundFile != null) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		int result = soundFile != null ? soundFile.hashCode() : 0;
		result = 31 * result + (soundChannel != null ? soundChannel.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "SoundSetting{" +
				"soundFile='" + soundFile + '\'' +
				", soundChannel=" + soundChannel +
				", stop=" + stop +
				'}';
	}

	@Override
	public SoundSetting interpolate(SoundSetting other, float pos) {
		if (pos <= 0.5f) {
			return this;
		}
		return other;
	}

	
}
