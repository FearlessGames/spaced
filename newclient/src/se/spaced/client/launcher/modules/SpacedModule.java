package se.spaced.client.launcher.modules;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import se.ardortech.render.module.LwjglModule;
import se.ardortech.render.module.RendererSettings;
import se.spaced.client.core.CoreModule;
import se.spaced.client.launcher.modules.smrt.NetworkModule;
import se.spaced.client.net.GameServer;
import se.spaced.client.sound.SoundModule;
import se.spaced.client.view.ViewModule;
import se.spaced.shared.model.xmo.ColladaContentLoader;
import se.spaced.shared.util.random.RandomProvider;
import se.spaced.shared.util.random.RealRandomProvider;

import java.util.List;

public class SpacedModule extends AbstractModule {
	private final RendererSettings rendererSettings;
	private final List<GameServer> gameServers;
	private final ResourceModule resourceModule;

	public SpacedModule(
			RendererSettings rendererSettings,
			List<GameServer> gameServers,
			ResourceModule resourceModule) {
		this.rendererSettings = rendererSettings;
		this.gameServers = gameServers;

		this.resourceModule = resourceModule;
	}


	@Override
	protected void configure() {

		install(new LwjglModule(rendererSettings));
		install(new CoreModule());
		install(new SpacedArdorModule());
		install(new NetworkModule(gameServers));
		install(new GameLogicModule());
		install(new SpacedGuiModule());
		install(new LuaModule());
		install(new ViewModule());
		install(new ZoneModule());
		install(new SoundModule());
		install(new ListenerDispatcherModule());
		install(new AnalyticsModule());
		install(new ToolsModule());

		bind(RandomProvider.class).to(RealRandomProvider.class).in(Scopes.SINGLETON);
		bind(ColladaContentLoader.class).to(resourceModule.getColladaContentLoaderClass()).in(Scopes.SINGLETON);
	}
}
