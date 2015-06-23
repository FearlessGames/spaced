package se.spaced.server.guice.modules;

import com.google.inject.AbstractModule;
import se.fearless.common.time.SystemTimeProvider;
import se.fearless.common.time.TimeProvider;

public class SystemTimeModule extends AbstractModule {
	@Override
	protected void configure() {
		bind(TimeProvider.class).to(SystemTimeProvider.class);
	}
}