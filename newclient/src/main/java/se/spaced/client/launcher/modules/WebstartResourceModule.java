package se.spaced.client.launcher.modules;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.thoughtworks.xstream.XStream;
import se.fearless.common.io.ClasspathIOLocator;
import se.fearless.common.io.FileLocator;
import se.fearless.common.io.IOLocator;
import se.fearless.common.io.MultiStreamLocator;
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
	public IOLocator getStreamLocator(
			@Named("resourceRootDir") File resourceRootDir,
			@Named("luaVarsDir") File luaVarsDir) {
		return new MultiStreamLocator(
				new FileLocator(luaVarsDir),
				new FileLocator(new File(spacedBase + "resources")),
				new FileLocator(new File(spacedBase + "resources" + "/textures/")),
				new ClasspathIOLocator()
		);

	}

	@Override
	@Provides
	@Singleton
	@Named("settings")
	public XmlIO getSettingsXStreamIO(XStream xStream) {
		return new XStreamIO(xStream, new FileLocator(new File(spacedBase)));
	}
}
