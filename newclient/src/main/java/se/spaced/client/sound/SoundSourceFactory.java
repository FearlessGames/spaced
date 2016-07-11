package se.spaced.client.sound;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.lwjgl.openal.AL10;
import se.fearless.common.io.IOLocator;
import se.spaced.client.sound.lwjgl.LwjglSoundSource;
import se.spaced.client.sound.lwjgl.LwjglStreamingSoundSource;

import java.util.concurrent.ScheduledExecutorService;

@Singleton
public class SoundSourceFactory {
	private final IOLocator ioLocator;
	private final ScheduledExecutorService executorService;
	private final SoundBufferManager bufferFactory;
	private final DirectBufferCache bufferCache = new DirectBufferCache();

	@Inject
	public SoundSourceFactory(IOLocator ioLocator, ScheduledExecutorService executorService, SoundBufferManager bufferFactory) {
		this.ioLocator = ioLocator;
		this.executorService = executorService;
		this.bufferFactory = bufferFactory;
	}

	public SoundSource newSoundSource(final SoundBuffer soundBuffer) {
		final SoundSource soundSource = newSoundSource();
		AL10.alSourcei(soundSource.getId(), AL10.AL_BUFFER, soundBuffer.getId());
		return soundSource;
	}

	public SoundSource newStreamingSoundSource(final String filepath) {
		final LwjglSoundSource soundSource = newSoundSource();

		return new LwjglStreamingSoundSource(soundSource, executorService, bufferFactory, ioLocator.getByteSource(filepath));
	}

	private LwjglSoundSource newSoundSource() {
		return new LwjglSoundSource(AL10.alGenSources(), bufferCache);
	}
}
