package se.spaced.client.launcher.modules;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import se.spaced.client.tools.areacreator.AreaCreatorPresenter;
import se.spaced.client.tools.areacreator.impl.AreaCreatorPresenterImpl;

public class ToolsModule extends AbstractModule {
	@Override
	protected void configure() {
		bind(AreaCreatorPresenter.class).to(AreaCreatorPresenterImpl.class).in(Scopes.SINGLETON);
	}
}
