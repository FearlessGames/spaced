package se.spaced.client.resources;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.SingleValueConverter;
import org.lwjgl.opengl.DisplayMode;
import se.ardortech.math.AABox;
import se.ardortech.math.Sphere;
import se.ardortech.render.module.RendererSettings;
import se.spaced.client.environment.settings.EnvSettingsImpl;
import se.spaced.client.environment.settings.EnvironmentSettings;
import se.spaced.client.environment.time.GameTime;
import se.spaced.client.model.animation.AnimationMapping;
import se.spaced.client.settings.AccountSettings;
import se.spaced.client.settings.GraphicsSettings;
import se.spaced.shared.model.AnimationState;
import se.spaced.shared.xml.ColorRGBAConverter;
import se.spaced.shared.xml.SharedXStreamRegistry;

public class ClientXStreamRegistry {
	private final SharedXStreamRegistry sharedXStreamRegistry = new SharedXStreamRegistry();
	private final SingleValueConverter gameTimeXStreamConverter;

	@Inject
	public ClientXStreamRegistry(@Named("GameTimeConverter") SingleValueConverter gameTimeXStreamConverter) {
		this.gameTimeXStreamConverter = gameTimeXStreamConverter;
	}

	public void registerDefaultsOn(XStream xStream) {
		sharedXStreamRegistry.registerDefaultsOn(xStream);

		xStream.registerConverter(new ColorRGBAConverter());
		xStream.processAnnotations(GraphicsSettings.class);
		xStream.processAnnotations(Sphere.class);
		xStream.processAnnotations(AABox.class);

		xStream.processAnnotations(GameTime.class);
		xStream.processAnnotations(EnvironmentSettings.class);
		xStream.processAnnotations(EnvSettingsImpl.class);


		xStream.processAnnotations(AnimationMapping.class);
		xStream.processAnnotations(AnimationState.class);

		xStream.registerConverter(gameTimeXStreamConverter);

		xStream.alias("displayMode", DisplayMode.class);

		xStream.alias("graphicsSettings", GraphicsSettings.class);
		xStream.alias("rendererSettings", RendererSettings.class);
		xStream.alias("accountSettings", AccountSettings.class);
	}
}
