package se.spaced.server.guice.modules;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import se.fearless.common.time.MockTimeProvider;
import se.fearless.common.time.TimeProvider;

public class MockTimeModule extends AbstractModule {
	@Override
	protected void configure() {
		bind(TimeProvider.class).to(MockTimeProvider.class).in(Scopes.SINGLETON);
	}
}
