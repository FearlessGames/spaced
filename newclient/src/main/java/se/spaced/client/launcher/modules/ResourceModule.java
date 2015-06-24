package se.spaced.client.launcher.modules;

import com.google.inject.Module;
import com.thoughtworks.xstream.XStream;
import se.fearless.common.io.StreamLocator;
import se.spaced.shared.model.xmo.ColladaContentLoader;
import se.spaced.shared.xml.XmlIO;

import java.io.File;

public interface ResourceModule extends Module {

	XmlIO getSettingsXStreamIO(XStream xStream);

	File getLuaVarsDir();

	StreamLocator getStreamLocator(File resourceRootDir, File luaVarsDir);

	File getResourceRootDir();

	Class<? extends ColladaContentLoader> getColladaContentLoaderClass();
}
