package se.spaced.client.launcher;

import com.ardor3d.image.util.AWTImageLoader;
import com.ardor3d.util.resource.ResourceLocator;
import com.ardor3d.util.resource.ResourceLocatorTool;
import com.google.inject.Guice;
import com.google.inject.Injector;
import se.ardortech.Main;
import se.ardortech.SpacedResourceLocator;
import se.fearless.common.io.StreamLocator;
import se.fearless.common.log.Slf4jJulBridge;
import se.spaced.client.launcher.modules.ResourceModule;
import se.spaced.client.launcher.modules.SpacedModule;
import se.spaced.client.launcher.modules.StartupModule;
import se.spaced.client.model.PlayerTargeting;
import se.spaced.client.net.GameServer;
import se.spaced.client.presenter.*;
import se.spaced.client.settings.SettingsHandler;
import se.spaced.client.settings.ui.AvailableDisplayModesSupplier;
import se.spaced.client.settings.ui.RenderPropertiesPresenter;
import se.spaced.client.settings.ui.RenderPropertiesViewImpl;
import se.spaced.client.view.ardor.ArdorChat;

import java.util.List;

public class ClientStarter {
	final List<GameServer> gameServers;
	final ResourceModule resourceModule;

	static {
		Slf4jJulBridge.init();
	}

	public ClientStarter(List<GameServer> gameServers, ResourceModule resourceModule) {
		this.gameServers = gameServers;
		this.resourceModule = resourceModule;
	}

	public void start(StartCallback startCallback) {

		Injector startupInjector = Guice.createInjector(resourceModule, new StartupModule());

		final SettingsHandler settingsHandler = startupInjector.getInstance(SettingsHandler.class);

		showRenderSettingsDialogIfMissing(settingsHandler);


		// TODO: Don't use this useless loader when we bother writing our own async one
		AWTImageLoader.registerLoader();

		ResourceLocator srl = new SpacedResourceLocator(startupInjector.getInstance(StreamLocator.class));
		ResourceLocatorTool.addResourceLocator(ResourceLocatorTool.TYPE_TEXTURE, srl);

		final Injector gameInjector = startupInjector.createChildInjector(
				new SpacedModule(settingsHandler.getRendererSettings(), gameServers, resourceModule),
				settingsHandler.getGraphicsSettings().getWaterModule());

		// TODO: Is this the right place to init?
		gameInjector.getInstance(ArdorChat.class);
		gameInjector.getInstance(AbilityPresenter.class);
		gameInjector.getInstance(LoginPresenter.class);
		gameInjector.getInstance(UserCharacterPresenter.class);
		gameInjector.getInstance(TargetingPresenter.class);
		gameInjector.getInstance(VisualEntityPresenter.class);
		gameInjector.getInstance(AbilityPresenter.class);
		gameInjector.getInstance(PlayerTargeting.class);

		Main game = gameInjector.getInstance(Main.class);

		if (startCallback != null) {
			startCallback.done();
		}

		game.run();
	}

	private void showRenderSettingsDialogIfMissing(SettingsHandler settingsHandler) {
		if (settingsHandler.isMissingRenderSettings()) {
			RenderPropertiesPresenter renderPropertiesPresenter = new RenderPropertiesPresenter(new RenderPropertiesViewImpl(),
					new AvailableDisplayModesSupplier());
			renderPropertiesPresenter.setCurrentSettings(settingsHandler.getRendererSettings());
			renderPropertiesPresenter.showDialog();
			settingsHandler.save();
		}
	}

	public interface StartCallback {
		void done();
	}
}