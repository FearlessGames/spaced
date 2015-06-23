package se.spaced.client.launcher.modules;

import com.ardor3d.framework.DisplaySettings;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.Singleton;
import com.google.inject.name.Names;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.SingleValueConverter;
import com.thoughtworks.xstream.io.xml.DomDriver;
import se.ardortech.render.module.RendererSettings;
import se.fearlessgames.common.io.StreamLocator;
import se.fearlessgames.common.lifetime.LifetimeManager;
import se.fearlessgames.common.lifetime.LifetimeManagerImpl;
import se.spaced.client.environment.time.GameTimeManager;
import se.spaced.client.environment.time.GameTimeXStreamConverter;
import se.spaced.client.environment.time.HourMinuteGameTimeManager;
import se.spaced.client.resources.ClientXStreamRegistry;
import se.spaced.client.settings.AccountSettings;
import se.spaced.client.settings.SettingsHandler;
import se.spaced.shared.world.TimeSystemInfo;
import se.spaced.shared.xml.XStreamIO;
import se.spaced.shared.xml.XmlIO;

public final class StartupModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(LifetimeManager.class).to(LifetimeManagerImpl.class).in(Scopes.SINGLETON);
		bind(SingleValueConverter.class).annotatedWith(Names.named("GameTimeConverter")).to(GameTimeXStreamConverter.class).in(
				Scopes.SINGLETON);
	}


	@Provides
	@Singleton
	public XmlIO getXmlIo(XStream xStream, StreamLocator streamLocator) {
		return new XStreamIO(xStream, streamLocator);
	}


	@Provides
	public AccountSettings getAccountSettings(SettingsHandler settings) {
		return settings.getAccountSettings();
	}

	@Provides
	public RendererSettings getRendererSettings(SettingsHandler settings) {
		return settings.getRendererSettings();
	}

	@Provides
	@Singleton
	public GameTimeManager getGameTimeManager() {
		return new HourMinuteGameTimeManager(new TimeSystemInfo(24, 60, 60, 1));
	}

	@Provides
	@Singleton
	public XStream getXStream(ClientXStreamRegistry clientXStreamRegistry) {
		XStream xStream = new XStream(new DomDriver());
		clientXStreamRegistry.registerDefaultsOn(xStream);

		return xStream;
	}

	@Provides
	@Singleton
	public DisplaySettings getDisplaySettings(RendererSettings sds) {
		return new DisplaySettings(
				sds.getWidth(), sds.getHeight(), sds.getColorDepth(), sds.getFrequency(), sds.getAlphaBits(),
				sds.getDepthBits(), sds.getStencilBits(), sds.getSamples(), sds.isFullScreen(), sds.isStereo()
		);
	}
}
