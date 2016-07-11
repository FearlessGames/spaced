package se.spaced.server.guice.modules;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import se.fearless.common.io.FileLocator;
import se.fearless.common.io.IOLocator;
import se.fearless.common.io.MultiStreamLocator;
import se.spaced.shared.resources.LoggingIOLocatorDecorator;

import java.io.File;

public class ResourcesModule extends AbstractModule {
	@Override
	protected void configure() {
	}

	@Provides
	@Singleton
	public IOLocator getStreamLocator() {
		return new MultiStreamLocator(
				new FileLocator(new File("src/main/resources")),
				new LoggingIOLocatorDecorator(new FileLocator(new File("../shared/src/main/resources"))
				)
		);
	}
}
