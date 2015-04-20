package se.ardortech.example.curve;

import com.ardor3d.framework.NativeCanvas;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import se.ardortech.Main;
import se.ardortech.example.ArdorLauncher;

import java.net.URISyntaxException;
import java.util.List;

public class CurveLauncher extends ArdorLauncher {
	public static void main(String[] args) throws URISyntaxException {
		CurveLauncher launcher = new CurveLauncher();
		List<Module> modules = launcher.getArdorModules();
		modules.add(new CurveModule());
		final Injector injector = Guice.createInjector(modules);
		injector.getInstance(NativeCanvas.class).setTitle("CurveExample");
		injector.getInstance(Main.class).run();
	}
}