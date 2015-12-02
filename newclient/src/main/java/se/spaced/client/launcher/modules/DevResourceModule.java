package se.spaced.client.launcher.modules;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.thoughtworks.xstream.XStream;
import se.fearless.common.io.ClasspathStreamLocator;
import se.fearless.common.io.FileStreamLocator;
import se.fearless.common.io.MultiStreamLocator;
import se.fearless.common.io.StreamLocator;
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
	public StreamLocator getStreamLocator(
			@Named("resourceRootDir") File resourceRootDir,
			@Named("luaVarsDir") File luaVarsDir) {

		return new MultiStreamLocator(
				new FileStreamLocator(luaVarsDir),
				new FileStreamLocator(new File(System.getProperty("user.dir") + "/resources")),
				new FileStreamLocator(resourceRootDir),
				new FileStreamLocator(new File(resourceRootDir, "/textures")),
				new FileStreamLocator(new File("../shared/src/main/resources")),
				new FileStreamLocator(new File("../ArdorGui/src/main/resources")),
				new ClasspathStreamLocator());

	}

	@Override
	@Provides
	@Singleton
	@Named("settings")
	public XmlIO getSettingsXStreamIO(XStream xStream) {
		return new XStreamIO(xStream, new FileStreamLocator(new File(System.getProperty("user.dir"))));
	}

	@Override
	public Class<? extends ColladaContentLoader> getColladaContentLoaderClass() {
		return CachingColladaContentLoader.class;
	}
}
