package se.spaced.client.launcher.modules;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.thoughtworks.xstream.XStream;
import se.fearlessgames.common.io.ClasspathStreamLocator;
import se.fearlessgames.common.io.FileStreamLocator;
import se.fearlessgames.common.io.MultiStreamLocator;
import se.fearlessgames.common.io.StreamLocator;
import se.spaced.client.resources.dae.BinColladaContentLoader;
import se.spaced.shared.model.xmo.ColladaContentLoader;
import se.spaced.shared.xml.XStreamIO;
import se.spaced.shared.xml.XmlIO;

import java.io.File;

public class WebstartResourceModule extends AbstractModule implements ResourceModule {
	private final String spacedBase;

	public WebstartResourceModule(String spacedBase) {
		this.spacedBase = spacedBase;
	}

	@Override
	protected void configure() {

	}

	@Override
	public Class<? extends ColladaContentLoader> getColladaContentLoaderClass() {
		return BinColladaContentLoader.class;
	}

	@Override
	@Provides
	@Singleton
	@Named("resourceRootDir")
	public File getResourceRootDir() {
		return new File(spacedBase + "resources");
	}

	@Override
	@Provides
	@Singleton
	@Named("luaVarsDir")
	public File getLuaVarsDir() {
		return new File(spacedBase + "vars");
	}

	@Override
	@Provides
	@Singleton
	public StreamLocator getStreamLocator(
			@Named("resourceRootDir") File resourceRootDir,
			@Named("luaVarsDir") File luaVarsDir) {
		return new MultiStreamLocator(
				new FileStreamLocator(luaVarsDir),
				new FileStreamLocator(new File(spacedBase + "resources")),
				new FileStreamLocator(new File(spacedBase + "resources" + "/textures/")),
				new ClasspathStreamLocator()
		);

	}

	@Override
	@Provides
	@Singleton
	@Named("settings")
	public XmlIO getSettingsXStreamIO(XStream xStream) {
		return new XStreamIO(xStream, new FileStreamLocator(new File(spacedBase)));
	}
}
