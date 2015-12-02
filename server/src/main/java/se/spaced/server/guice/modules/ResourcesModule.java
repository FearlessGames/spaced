package se.spaced.server.guice.modules;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import se.fearless.common.io.FileStreamLocator;
import se.fearless.common.io.MultiStreamLocator;
import se.fearless.common.io.StreamLocator;

import java.io.File;

public class ResourcesModule extends AbstractModule {
	@Override
	protected void configure() {
	}

	@Provides
	@Singleton
	public StreamLocator getStreamLocator() {
		return new MultiStreamLocator(
				new FileStreamLocator(new File("src/main/resources")),
				new FileStreamLocator(new File("../shared/src/main/resources"))
		);
	}
}
