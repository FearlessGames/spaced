package se.ardortech.example.box;

import com.ardor3d.framework.NativeCanvas;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import se.ardortech.Main;
import se.ardortech.example.ArdorLauncher;

import java.net.URISyntaxException;
import java.util.List;

public class BoxLauncher extends ArdorLauncher {
	public static void main(String[] args) throws URISyntaxException {
		BoxLauncher launcher = new BoxLauncher();
		List<Module> modules = launcher.getArdorModules();
		modules.add(new BoxModule());
		final Injector injector = Guice.createInjector(modules);
		injector.getInstance(NativeCanvas.class).setTitle("BoxExample");
		injector.getInstance(Main.class).run();
	}
}