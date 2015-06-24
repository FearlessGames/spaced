package se.spaced.client.launcher.modules;

import com.ardor3d.framework.NativeCanvas;
import com.ardor3d.image.Texture;
import com.ardor3d.scenegraph.Node;
import com.google.common.util.concurrent.AbstractFuture;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Names;
import se.ardorgui.ArdorGuiSettings;
import se.ardorgui.view.GuiViewFactory;
import se.ardortech.TextureLoadCallback;
import se.ardortech.TextureManager;
import se.fearless.common.util.TimeProvider;
import se.fearless.common.uuid.UUIDFactory;
import se.fearless.common.uuid.UUIDFactoryImpl;
import se.spaced.client.ardor.effect.EffectSystem;
import se.spaced.client.ardor.ui.SpacedGui;
import se.spaced.client.ardor.ui.SpacedGuiImpl;
import se.spaced.client.core.states.GameState;
import se.spaced.client.core.states.GameStateContext;
import se.spaced.client.game.logic.local.LocalChatLogic;
import se.spaced.client.game.logic.local.LocalLoginLogic;
import se.spaced.client.model.ClientEntity;
import se.spaced.client.model.ClientSpell;
import se.spaced.client.model.control.GroundImpactListener;
import se.spaced.client.net.messagelisteners.EntityCacheImpl;
import se.spaced.client.net.messagelisteners.SpellCacheImpl;
import se.spaced.client.net.ping.PingManager;
import se.spaced.client.net.remoteservices.ServerInfoWSC;
import se.spaced.client.net.smrt.ServerConnection;
import se.spaced.client.resources.zone.*;
import se.spaced.client.tools.spd.SpdView;
import se.spaced.client.view.PropSelectionListener;
import se.spaced.client.view.entity.EntityView;
import se.spaced.messages.protocol.Entity;
import se.spaced.messages.protocol.Spell;
import se.spaced.shared.activecache.ActiveCache;
import se.spaced.shared.model.xmo.XmoEntityFactory;
import se.spaced.shared.resources.XmoMaterialManager;
import se.spaced.shared.util.AbstractMockModule;
import se.spaced.shared.util.ListenerDispatcher;
import se.spaced.shared.world.terrain.AbstractHeightmapLoader;
import se.spaced.shared.world.terrain.HeightMap;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;

import static se.mockachino.Mockachino.mock;
import static se.mockachino.Mockachino.stubReturn;
import static se.mockachino.matchers.Matchers.any;

public final class MockModule extends AbstractMockModule {
	@Override
	protected void configure() {
		bind(UUIDFactory.class).to(UUIDFactoryImpl.class);
		bind(SpacedGui.class).to(SpacedGuiImpl.class);
		bindMock(LocalChatLogic.class);
		bindMock(LocalLoginLogic.class);
		bindMock(ServerConnection.class);
		bindMock(PingManager.class);
		bindMock(ArdorGuiSettings.class);
		bindMock(ServerInfoWSC.class);
		bind(Node.class).annotatedWith(Names.named("entityNode")).toInstance(mock(Node.class));
		bind(Node.class).annotatedWith(Names.named("propsNode")).toInstance(mock(Node.class));
		bind(Node.class).annotatedWith(Names.named("rootNode")).toInstance(mock(Node.class));
		bindMock(XmoEntityFactory.class);
		bindMock(ZoneActivationService.class);
		bindMock(ZoneValidator.class);
		bindMock(TimeProvider.class);
		bindMock(AbstractHeightmapLoader.class);
		bind(GuiViewFactory.class).to(MockGuiViewFactory.class);
		bindMock(XmoMaterialManager.class);
		bindMock(NativeCanvas.class);
		bindMock(SpdView.class);
		bindMock(ZoneDebugShapeService.class);
		bindMock(ScenegraphService.class);
		bindMock(ZoneXmlWriter.class);
		bindMock(RootZoneService.class);
		bindMock(EffectSystem.class);
		bindMock(EntityView.class);
		bindMock(GroundImpactListener.class);
		bindMock(ScheduledExecutorService.class);
		bindMock(HeightMap.class);
		bindMock(GameStateContext.class);
		bindMock(GameState.class, Names.named("selectCharacterState"));
		bindMock(GameState.class, Names.named("createCharacterState"));
	}

	@Singleton
	@Provides
	public ActiveCache<Spell, ClientSpell> getSpellCache() {
		return new SpellCacheImpl(null);
	}

	@Singleton
	@Provides
	public ActiveCache<Entity, ClientEntity> getEntityCache() {
		return new EntityCacheImpl(null);
	}

	@Provides
	@Singleton
	public ListenerDispatcher<PropSelectionListener> getPropViewLD() {
		return ListenerDispatcher.create(PropSelectionListener.class);
	}

	@Provides
	@Singleton
	public TextureManager getTextureManager() {
		TextureManager tm = mock(TextureManager.class);
		Future<Texture> future = new AbstractFuture<Texture>() {
			@Override
			public Texture get() throws InterruptedException, ExecutionException {
				return new MockTexture();
			}
		};

		stubReturn(future).on(tm).loadTexture(any(String.class), any(TextureLoadCallback.class));

		return tm;
	}
}
