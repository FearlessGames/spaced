package se.spaced.client.sound;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import se.fearlessgames.common.io.StreamLocator;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;

public class SoundModule extends AbstractModule {

	@Override
	protected void configure() {
	}

	@Provides
	@Singleton
	public SoundSourceFactory getSoundSourceFactory(StreamLocator sl, ScheduledThreadPoolExecutor stpe, SoundBufferManager sbf) {
		return new SoundSourceFactory(sl, stpe, sbf);
	}

	@Provides
	@Singleton
	public ScheduledThreadPoolExecutor getScheduledThreadPoolExecutor() {
		ThreadFactory threadFactory = new ThreadFactoryBuilder()
				.setDaemon(true).setNameFormat("SoundStreamer-%s").build();
		return new ScheduledThreadPoolExecutor(2, threadFactory);
	}
}
