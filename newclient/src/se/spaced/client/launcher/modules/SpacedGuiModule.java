package se.spaced.client.launcher.modules;

import com.ardor3d.extension.ui.UIHud;
import com.ardor3d.renderer.state.CullState;
import com.ardor3d.renderer.state.RenderState;
import com.ardor3d.renderer.state.ZBufferState;
import com.ardor3d.scenegraph.Node;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.Singleton;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import se.ardorgui.guice.BindGuiRoot;
import se.ardorgui.lua.bindings.LuaKeyBindings;
import se.krka.kahlua.integration.expose.IterableExposer;
import se.spaced.client.ardor.InputManager;
import se.spaced.client.ardor.ui.api.AccountApi;
import se.spaced.client.ardor.ui.api.ChatApi;
import se.spaced.client.ardor.ui.api.ConnectionApi;
import se.spaced.client.ardor.ui.api.FontApi;
import se.spaced.client.ardor.ui.api.GmApi;
import se.spaced.client.ardor.ui.api.ItemApi;
import se.spaced.client.ardor.ui.api.PerformanceApi;
import se.spaced.client.ardor.ui.api.PlayerActionsApi;
import se.spaced.client.ardor.ui.api.PromoApi;
import se.spaced.client.ardor.ui.api.SavedVarsApi;
import se.spaced.client.ardor.ui.api.SoundApi;
import se.spaced.client.ardor.ui.api.SpellApi;
import se.spaced.client.ardor.ui.api.SystemApi;
import se.spaced.client.ardor.ui.api.TargetApi;
import se.spaced.client.ardor.ui.api.TradeApi;
import se.spaced.client.ardor.ui.api.UnitInfoApi;
import se.spaced.client.ardor.ui.api.VendorApi;
import se.spaced.client.ardor.ui.api.WorldInfoApi;
import se.spaced.client.model.control.CharacterControlLuaHandler;
import se.spaced.client.model.control.CharacterControlProvider;
import se.spaced.shared.events.EventHandler;
import se.spaced.shared.events.LuaEventHandler;

public final class SpacedGuiModule extends AbstractModule {
	@Override
	protected void configure() {
		bind(EventHandler.class).to(LuaEventHandler.class).in(Scopes.SINGLETON);

		Multibinder<Object> multibinder = Multibinder.newSetBinder(binder(),
				Object.class,
				Names.named("defaultGuiFunctions"));
		multibinder.addBinding().to(PlayerActionsApi.class);
		multibinder.addBinding().to(UnitInfoApi.class);
		multibinder.addBinding().to(LuaKeyBindings.class);
		multibinder.addBinding().to(CharacterControlProvider.class);
		multibinder.addBinding().to(CharacterControlLuaHandler.class);
		multibinder.addBinding().to(ConnectionApi.class);
		multibinder.addBinding().to(WorldInfoApi.class);
		multibinder.addBinding().to(ChatApi.class);
		multibinder.addBinding().to(GmApi.class);
		multibinder.addBinding().to(SpellApi.class);
		multibinder.addBinding().to(TargetApi.class);
		multibinder.addBinding().to(LuaEventHandler.class);
		multibinder.addBinding().to(PerformanceApi.class);
		multibinder.addBinding().to(ItemApi.class);
		multibinder.addBinding().to(TradeApi.class);
		multibinder.addBinding().to(VendorApi.class);
		multibinder.addBinding().to(AccountApi.class);
		multibinder.addBinding().to(SoundApi.class);
		multibinder.addBinding().to(SavedVarsApi.class);
		multibinder.addBinding().to(FontApi.class);
		multibinder.addBinding().to(PromoApi.class);
		multibinder.addBinding().to(SystemApi.class);
		multibinder.addBinding().to(IterableExposer.class);
	}

	@Provides
	@Singleton
	@BindGuiRoot
	public Node getGuiRootNode(@Named("rootNode") Node root) {
		Node node = new Node("guiRoot");
		CullState cullState = new CullState();
		cullState.setCullFace(CullState.Face.None);
		node.setRenderState(cullState);

		final RenderState guiZBuffer = new ZBufferState();
		guiZBuffer.setEnabled(false);
		node.setRenderState(guiZBuffer);

		root.attachChild(node);

		return node;
	}

	@Provides
	@Singleton
	public UIHud getUiHud() {
		return new UIHud();
	}

	@Provides
	@Singleton
	public InputManager getInputManager() {
		return new InputManager();
	}
}