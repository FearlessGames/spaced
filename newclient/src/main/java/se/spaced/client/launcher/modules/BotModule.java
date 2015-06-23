package se.spaced.client.launcher.modules;

import com.ardor3d.scenegraph.Node;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import se.ardortech.Main;
import se.fearlessgames.common.lifetime.LifetimeManager;
import se.fearlessgames.common.lifetime.LifetimeManagerImpl;
import se.fearlessgames.common.lua.LuaVm;
import se.fearlessgames.common.util.uuid.UUIDFactory;
import se.fearlessgames.common.util.uuid.UUIDFactoryImpl;
import se.spaced.client.ardor.effect.EffectSystem;
import se.spaced.client.bot.BotGameLoop;
import se.spaced.client.core.states.GameState;
import se.spaced.client.core.states.GameStateContext;
import se.spaced.client.core.states.LoadingState;
import se.spaced.client.core.states.WorldGameStateMarkup;
import se.spaced.client.sound.SoundListener;
import se.spaced.client.view.AbilityView;
import se.spaced.client.view.cursor.CursorView;
import se.spaced.client.view.entity.EntityView;
import se.spaced.shared.events.EventHandler;
import se.spaced.shared.model.xmo.XmoEntityFactory;
import se.spaced.shared.util.cache.CacheManager;

import static se.mockachino.Mockachino.*;

public final class BotModule extends AbstractModule {

	@Override
	public void configure() {
		bind(UUIDFactory.class).to(UUIDFactoryImpl.class);
		bind(EventHandler.class).toInstance(mock(EventHandler.class));
		bind(GameStateContext.class).toInstance(mock(GameStateContext.class));
		bind(WorldGameStateMarkup.class).toInstance(mock(WorldGameStateMarkup.class));
		bind(AbilityView.class).toInstance(mock(AbilityView.class));
		bind(EntityView.class).toInstance(mock(EntityView.class));
		bind(SoundListener.class).toInstance(mock(SoundListener.class));
		bind(CursorView.class).toInstance(mock(CursorView.class));
		bind(EffectSystem.class).toInstance(mock(EffectSystem.class));
		bind(LuaVm.class).annotatedWith(Names.named("gui")).toInstance(mock(LuaVm.class));
		bind(LuaVm.class).annotatedWith(Names.named("effects")).toInstance(mock(LuaVm.class));
		bind(Node.class).annotatedWith(Names.named("entityNode")).toInstance(mock(Node.class));
		bind(GameState.class).annotatedWith(Names.named("loadingState")).toInstance(mock(LoadingState.class));
		//bind(RootZoneService.class).toInstance(mock(RootZoneService.class));
		bind(LifetimeManager.class).to(LifetimeManagerImpl.class).in(Scopes.SINGLETON);
		bind(LoadingState.class).toInstance(mock(LoadingState.class));
		bind(XmoEntityFactory.class).toInstance(mock(XmoEntityFactory.class));
		bind(Main.class).to(BotGameLoop.class);

		install(new StartupModule());
		install(new ZoneModule());

		install(new ListenerDispatcherModule());
	}

	@Provides
	@Singleton
	@Named("propsNode")
	public Node getPropsNode() {
		final Node node = new Node("propsNode");
		return node;
	}

	@Provides
	@Singleton
	@Named("rootNode")
	public Node getRootNode() {
		final Node node = new Node("rootNode");
		return node;
	}

	@Provides
	@Singleton
	@Named("xmoCachedManager")
	public CacheManager getXmoCacheManager() {
		return new CacheManager();
	}



}
