package se.ardorgui.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.google.inject.Singleton;
import se.ardorgui.ArdorGuiSettings;
import se.ardorgui.input.GuiFocusManager;
import se.ardorgui.view.ArdorGuiViewFactory;
import se.ardorgui.view.GuiViewFactory;
import se.ardortech.meshgenerator.MeshFactory;

public class ArdorGuiModule extends AbstractModule {
	private final ArdorGuiSettings settings;

	public ArdorGuiModule(ArdorGuiSettings settings) {
		this.settings = settings;
	}

	@Override
	protected void configure() {
		bind(ArdorGuiSettings.class).toInstance(settings);
		bind(GuiFocusManager.class).in(Singleton.class);
		bind(MeshFactory.class);
		bind(GuiViewFactory.class).to(ArdorGuiViewFactory.class).in(Scopes.SINGLETON);
	}
}