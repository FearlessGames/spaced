package se.spaced.client.ardor.ui.api;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.krka.kahlua.integration.annotations.LuaMethod;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class SavedVarsApi {
	private final Logger log = LoggerFactory.getLogger(getClass());

	private final File rootDirectory;

	@Inject
	public SavedVarsApi(@Named("luaVarsDir") File rootDirectory) {
		this.rootDirectory = rootDirectory;
	}

	@LuaMethod(name = "SaveVarsFile", global = true)
	public void saveVarsFile(String path, String data) {
		if (!path.endsWith(".lua")) {
			path = path + ".lua";
		}
		File file = new File(rootDirectory, path);
		new File(file.getParent()).mkdirs();
		try {
			PrintWriter writer = new PrintWriter(file);
			writer.print(data);
			writer.flush();
			writer.close();
		} catch (FileNotFoundException e) {
			log.error("Failed to open file: " + file, e);
		}
	}
}
