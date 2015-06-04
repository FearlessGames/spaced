package se.ardortech.example;

import com.ardor3d.image.util.AWTImageLoader;
import com.ardor3d.util.resource.ResourceLocatorTool;
import com.ardor3d.util.resource.SimpleResourceLocator;
import com.google.common.collect.Lists;
import com.google.inject.Module;
import se.ardortech.properties.PropertiesDialogHelper;
import se.ardortech.properties.PropertiesGameSettings;
import se.ardortech.render.module.LwjglModule;
import se.ardortech.render.module.RendererSettings;

import java.net.URISyntaxException;
import java.util.List;

public class ArdorLauncher {
	public List<Module> getArdorModules() throws URISyntaxException {
		final PropertiesGameSettings prefs = PropertiesDialogHelper.getAttributes(new PropertiesGameSettings(
				"ardorSettings.properties",
				null));

		RendererSettings rendererSettings = new RendererSettings();
		rendererSettings.setHeight(prefs.getHeight());
		rendererSettings.setWidth(prefs.getWidth());
		rendererSettings.setColorDepth(prefs.getDepth());
		rendererSettings.setFrequency(prefs.getFrequency());
		rendererSettings.setAlphaBits(prefs.getAlphaBits());
		rendererSettings.setDepthBits(prefs.getDepthBits());
		rendererSettings.setStencilBits(prefs.getStencilBits());
		rendererSettings.setSamples(prefs.getSamples());
		rendererSettings.setWindowMode(prefs.getWindowMode());
		rendererSettings.setStereo(false);

		AWTImageLoader.registerLoader();
		ResourceLocatorTool.addResourceLocator(ResourceLocatorTool.TYPE_TEXTURE,
				new SimpleResourceLocator(ArdorLauncher.class.getClassLoader().getResource("textures/")));

		return Lists.newArrayList((Module) new LwjglModule(rendererSettings));
	}
}