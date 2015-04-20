package se.spaced.server.guice.modules;

import com.google.inject.AbstractModule;
import se.fearlessgames.common.util.SystemTimeProvider;
import se.fearlessgames.common.util.TimeProvider;

public class SystemTimeModule extends AbstractModule {
	@Override
	protected void configure() {
		bind(TimeProvider.class).to(SystemTimeProvider.class);
	}
}