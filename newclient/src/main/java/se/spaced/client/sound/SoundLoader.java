package se.spaced.client.sound;

import com.google.inject.Inject;
import se.spaced.shared.activecache.ActiveCache;
import se.spaced.shared.activecache.ActiveCacheImpl;
import se.spaced.shared.activecache.Job;
import se.spaced.shared.activecache.KeyRequestHandler;

public class SoundLoader {
	private final SoundBufferManager bufferManager;
	private final SoundSourceFactory sourceFactory;
	private final ActiveCache<String, SoundBuffer> cache = new ActiveCacheImpl<String, SoundBuffer>(new KeyRequestHandler<String>() {
		@Override
		public void requestKey(final String key) {
			final SoundBuffer buffer = bufferManager.newSoundBuffer(key);
			cache.setValue(key, buffer);
		}
	});

	@Inject
	public SoundLoader(SoundBufferManager bufferManager, SoundSourceFactory sourceFactory) {
		this.bufferManager = bufferManager;
		this.sourceFactory = sourceFactory;
	}

	public void runWhenReady(final String key, final Job<SoundSource> sourceJob) {
		cache.runWhenReady(key, new Job<SoundBuffer>() {
			@Override
			public void run(final SoundBuffer buffer) {
				final SoundSource source = sourceFactory.newSoundSource(buffer);
				sourceJob.run(source);
			}
		});
	}
}
