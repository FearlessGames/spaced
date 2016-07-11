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
import se.spaced.client.resources.dae.CachingColladaContentLoader;
import se.spaced.shared.model.xmo.ColladaContentLoader;
import se.spaced.shared.xml.XStreamIO;
import se.spaced.shared.xml.XmlIO;

import java.io.File;

public class DevResourceModule extends AbstractModule implements ResourceModule {

	@Override
	protected void configure() {
	}


	@Override
	@Provides
	@Singleton
	@Named("resourceRootDir")
	public File getResourceRootDir() {
		return new File("src/main/resources");
	}


	@Override
	@Provides
	@Singleton
	@Named("luaVarsDir")
	public File getLuaVarsDir() {
		return new File("vars");
	}


	@Override
	@Provides
	@Singleton
	public IOLocator getStreamLocator(
			@Named("resourceRootDir") File resourceRootDir,
			@Named("luaVarsDir") File luaVarsDir) {

		return new MultiStreamLocator(
				new FileLocator(luaVarsDir),
				new FileLocator(new File(System.getProperty("user.dir") + "/resources")),
				new FileLocator(resourceRootDir),
				new FileLocator(new File(resourceRootDir, "/textures")),
				new FileLocator(new File("../shared/src/main/resources")),
				new FileLocator(new File("../ArdorGui/src/main/resources")),
				new ClasspathIOLocator());

	}

	@Override
	@Provides
	@Singleton
	@Named("settings")
	public XmlIO getSettingsXStreamIO(XStream xStream) {
		return new XStreamIO(xStream, new FileLocator(new File(System.getProperty("user.dir"))));
	}

	@Override
	public Class<? extends ColladaContentLoader> getColladaContentLoaderClass() {
		return CachingColladaContentLoader.class;
	}
}
