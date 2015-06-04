package se.ardorgui.base;

import com.ardor3d.framework.NativeCanvas;
import com.ardor3d.math.Vector2;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import se.ardorgui.ArdorGuiSettings;
import se.ardorgui.guice.ArdorGuiLuaModule;
import se.ardorgui.guice.ArdorGuiModule;
import se.ardorgui.lua.base.GuiTestArdorGuiModule;
import se.ardortech.Main;
import se.ardortech.example.ArdorLauncher;
import se.ardortech.math.AARectangle;

import java.awt.Insets;
import java.net.URISyntaxException;
import java.util.List;

public class GuiLauncher extends ArdorLauncher {
	public void start(Module guiModule) throws URISyntaxException {
		List<Module> modules = getArdorModules();
		modules.add(guiModule);
		modules.add(new ArdorGuiLuaModule());
		modules.add(new GuiTestArdorGuiModule());
		modules.add(new ArdorGuiModule(
        		new ArdorGuiSettings(
        				new Insets(5, 5, 5, 5),
        				"textures/gui/frame/Frame16px.png",
        				"textures/gui/frame/Frame16pxFilled.png",
        				"textures/gui/button/normal16.png",
        				"textures/gui/button/over16.png",
        				"textures/gui/button/down16.png",
        				"textures/gui/progressbar.png",
        				new AARectangle(new Vector2(0, 32f / 256f), new Vector2(1, 64f / 256f)),
        				"textures/gui/quad.png",
        				"textures/cursors/cursor.png",
        				22)));

		final Injector injector = Guice.createInjector(modules);
		injector.getInstance(NativeCanvas.class).setTitle("GuiExample");
		injector.getInstance(Main.class).run();
	}
}