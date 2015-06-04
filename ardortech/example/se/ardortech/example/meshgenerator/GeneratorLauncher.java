package se.ardortech.example.meshgenerator;

import com.ardor3d.framework.NativeCanvas;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import se.ardortech.Main;
import se.ardortech.example.ArdorLauncher;

import java.net.URISyntaxException;
import java.util.List;

public class GeneratorLauncher extends ArdorLauncher {
	public static void main(String[] args) throws URISyntaxException {
		GeneratorLauncher launcher = new GeneratorLauncher();
		List<Module> modules = launcher.getArdorModules();
		modules.add(new GeneratorModule());
		final Injector injector = Guice.createInjector(modules);
		injector.getInstance(NativeCanvas.class).setTitle("GeneratorExample");
		injector.getInstance(Main.class).run();
	}
}