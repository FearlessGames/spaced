package se.spaced.client.ardor.ui.api;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.krka.kahlua.integration.annotations.LuaMethod;
import se.spaced.client.model.UserCharacter;
import se.spaced.client.sound.SoundLoader;
import se.spaced.client.sound.SoundSource;
import se.spaced.shared.activecache.Job;


@Singleton
public class SoundApi {
	private final Logger log = LoggerFactory.getLogger(getClass());
	private final SoundLoader soundLoader;
	private final UserCharacter userCharacter;

	@Inject
	public SoundApi(SoundLoader soundLoader, UserCharacter userCharacter) {
		this.soundLoader = soundLoader;
		this.userCharacter = userCharacter;
	}

	@LuaMethod(global = true, name = "PlaySound")
	public void playSound(final String filePath, final double gain) {

		soundLoader.runWhenReady(filePath, new Job<SoundSource>() {
			@Override
			public void run(final SoundSource soundSource) {
				log.debug("{} has loaded and will be played", filePath);
				soundSource.setGain((float) gain);
				soundSource.setLooping(false);
				soundSource.setPitch((float) 1.0);
				soundSource.setPosition(userCharacter.getPosition());
				soundSource.play();
			}
		});
	}
}
