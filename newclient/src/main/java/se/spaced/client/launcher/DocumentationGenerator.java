package se.spaced.client.launcher;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.util.Modules;
import se.krka.kahlua.integration.doc.ApiDocumentationExporter;
import se.krka.kahlua.integration.doc.DokuWikiPrinter;
import se.krka.kahlua.integration.expose.LuaJavaClassExposer;
import se.spaced.client.ardor.ui.SpacedGui;
import se.spaced.client.launcher.modules.DevResourceModule;
import se.spaced.client.launcher.modules.MockModule;
import se.spaced.client.launcher.modules.SpacedModule;
import se.spaced.client.launcher.modules.StartupModule;
import se.spaced.client.net.GameServer;
import se.spaced.client.settings.SettingsHandler;
import se.spaced.shared.util.guice.dependencytool.DependencyTool;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import static java.nio.charset.StandardCharsets.UTF_8;

public class DocumentationGenerator {
	public static void main(String[] args) throws IOException {
		new DocumentationGenerator().start(args);
	}

	public void start(String[] args) throws IOException {
		if (args.length < 1) {
			System.out.println("First argument should be filename");
			return;
		}
		String filename = args[0];
		File file = new File(filename);
		Writer writer = Files.newBufferedWriter(file.toPath(), UTF_8);

		runGenerator(writer);
		System.out.println("Saved documentation as " + file.getAbsolutePath());
	}

	public void runGenerator(Writer writer) {
		final Injector launchInjector = Guice.createInjector(new DevResourceModule(), new StartupModule());
		final SettingsHandler settingsHandler = launchInjector.getInstance(SettingsHandler.class);

		final Injector gameInjector = launchInjector.createChildInjector(
				Modules.override(
						new SpacedModule(settingsHandler.getRendererSettings(),
								new ArrayList<GameServer>(),
								new DevResourceModule()),
						settingsHandler.getGraphicsSettings().getWaterModule()).
						with(new MockModule()));

		SpacedGui spacedGui = gameInjector.getInstance(SpacedGui.class);

		try {
			new DependencyTool(gameInjector, getWriter(".dependency.txt"), getWriter(".dependency.dot")).process();
		} catch (IOException e) {
			e.printStackTrace();
		}

		LuaJavaClassExposer luaExposer = spacedGui.setupBindings();
		ApiDocumentationExporter exporter = new ApiDocumentationExporter(luaExposer.getClassDebugInformation());
		DokuWikiPrinter printer = new DokuWikiPrinter(writer, exporter);
		printer.process();
	}

	private PrintWriter getWriter(String suffix) throws IOException {
		return new PrintWriter(Files.newBufferedWriter(Paths.get(this.getClass().getSimpleName() + suffix), UTF_8));
	}
}