package sound.al;

import org.lwjgl.LWJGLException;
import org.lwjgl.openal.AL;
import se.fearlessgames.common.io.StreamLocator;
import se.fearlessgames.common.util.TimeProvider;
import sound.SoundEffect;
import sound.SoundFactory;

import java.nio.IntBuffer;
import java.util.concurrent.ScheduledExecutorService;

public class OpenALSoundFactory implements SoundFactory {
	private final StreamLocator streamLocator;
	private final ScheduledExecutorService executorService;
	private final OpenALSoundEffectBufferLoader soundEffectBufferLoader;

	public OpenALSoundFactory(StreamLocator streamLocator, ScheduledExecutorService executorService, TimeProvider timeProvider) {
		this.streamLocator = streamLocator;
		this.executorService = executorService;
		this.soundEffectBufferLoader = new OpenALSoundEffectBufferLoader(streamLocator, timeProvider);

		try {
			AL.create();
		} catch (LWJGLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public OpenALStreamedSoundEffect newStreamingSoundEffect(String filepath) {
		return new OpenALStreamedSoundEffect(filepath, streamLocator, executorService);
	}

	@Override
	public SoundEffect newDirectSoundEffect(String filepath) {
		IntBuffer buffer = soundEffectBufferLoader.getBufferForFile(filepath);
		return new OpenALDirectSoundEffect(buffer, filepath, soundEffectBufferLoader);
	}

}
