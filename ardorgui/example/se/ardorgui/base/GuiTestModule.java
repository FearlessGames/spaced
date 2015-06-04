package se.ardorgui.base;

import com.ardor3d.framework.Scene;
import com.ardor3d.framework.Updater;
import com.ardor3d.scenegraph.Node;
import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import se.ardorgui.guice.BindGuiRoot;
import se.ardortech.BaseArdorMain;
import se.ardortech.Main;

public class GuiTestModule extends AbstractModule {
	private final Class<? extends GuiScene> exampleClass;
	private final Class<? extends GuiUpdater> updaterClass;

	public GuiTestModule(Class<? extends GuiScene> exampleClass) {
		this(exampleClass, null);
	}


	public GuiTestModule(Class<? extends GuiScene> exampleClass,
					 Class<? extends GuiUpdater> updaterClass) {
		this.exampleClass = exampleClass;
		this.updaterClass = updaterClass;
	}

	@Override
	protected void configure() {
		bind(Node.class).annotatedWith(BindGuiRoot.class).toInstance(new Node("guiRoot"));
		bind(Main.class).to(BaseArdorMain.class).in(Scopes.SINGLETON);

		bind(Updater.class).to(GuiUpdater.class).in(Scopes.SINGLETON);
		if (updaterClass != null) {
			bind(GuiUpdater.class).to(updaterClass).in(Scopes.SINGLETON);
		}
		bind(Scene.class).to(GuiScene.class).in(Scopes.SINGLETON);
		if(exampleClass != null) {
			bind(GuiScene.class).to(exampleClass).in(Scopes.SINGLETON);
		}
	}
}