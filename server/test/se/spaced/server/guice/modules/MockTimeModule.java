package se.spaced.server.guice.modules;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import se.fearlessgames.common.util.MockTimeProvider;
import se.fearlessgames.common.util.TimeProvider;

public class MockTimeModule extends AbstractModule {
	@Override
	protected void configure() {
		bind(TimeProvider.class).to(MockTimeProvider.class).in(Scopes.SINGLETON);
	}
}
