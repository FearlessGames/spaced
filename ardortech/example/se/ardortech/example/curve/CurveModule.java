package se.ardortech.example.curve;

import com.ardor3d.framework.Scene;
import com.ardor3d.framework.Updater;
import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import se.ardortech.BaseArdorMain;
import se.ardortech.Main;
import se.ardortech.example.BaseExampleScene;

public class CurveModule extends AbstractModule {
	@Override
	protected void configure() {
		bind(Main.class).to(BaseArdorMain.class).in(Scopes.SINGLETON);
		bind(Scene.class).to(BaseExampleScene.class).in(Scopes.SINGLETON);
		bind(BaseExampleScene.class).to(CurveScene.class).in(Scopes.SINGLETON);
		bind(Updater.class).to(CurveUpdater.class).in(Scopes.SINGLETON);
	}
}