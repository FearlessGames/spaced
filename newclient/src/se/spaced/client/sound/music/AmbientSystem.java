package se.spaced.client.sound.music;

import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.spaced.client.environment.settings.SoundSetting;
import se.spaced.client.sound.SoundSource;
import se.spaced.client.sound.SoundSourceFactory;

import java.util.Map;

@Singleton
public class AmbientSystem {
	private final SoundSourceFactory sourceFactory;
	private final Map<SoundChannel, SoundSource> channels = Maps.newHashMap();
	private final Map<SoundChannel, String> names = Maps.newHashMap();
	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Inject
	public AmbientSystem(final SoundSourceFactory sourceFactory) {
		this.sourceFactory = sourceFactory;
	}

	public void playInChannel(final String name, final SoundChannel channel) {
		String soundName = names.get(channel);

		if (name.equals(soundName)) {
			return;
		}

		SoundSource playingSource = channels.get(channel);

		if (playingSource != null) {
			playingSource.stop();
			playingSource.delete();
		}

		SoundSource newSoundSource = sourceFactory.newStreamingSoundSource(name);
		if (newSoundSource != null) {
			newSoundSource.setRelative(true);
			if (channel == SoundChannel.LOOP1) {
				newSoundSource.setLooping(true);
				logger.debug("Starting streaming looping sound {}", name);
			} else {
				logger.debug("Starting streaming sound {}", name);
			}
			newSoundSource.play();
			channels.put(channel, newSoundSource);
			names.put(channel, name);
		}
	}

	public void stopChannel(final SoundChannel soundChannel) {
		SoundSource soundSource = channels.get(soundChannel);
		if (soundSource != null) {
			soundSource.stop();
			soundSource.delete();
			names.remove(soundChannel);
		}
	}

	public void setCurrentSettings(SoundSetting soundSetting) {
		if (soundSetting.isStop()) {
			stopChannel(soundSetting.getSoundChannel());
		} else {
			playInChannel(soundSetting.getSoundFile(), soundSetting.getSoundChannel());
		}
	}
}
