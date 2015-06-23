package se.spaced.client.launcher.modules;

import se.spaced.client.game.logic.implementations.chat.ChatLogicListener;
import se.spaced.client.model.listener.AbilityModelListener;
import se.spaced.client.model.listener.ClientEntityListener;
import se.spaced.client.model.listener.EquipmentListener;
import se.spaced.client.model.listener.LoginListener;
import se.spaced.client.model.listener.UserCharacterListener;
import se.spaced.client.model.player.PlayerTargetingListener;
import se.spaced.client.net.smrt.ServerConnectionListener;
import se.spaced.client.resources.zone.ZoneActivationListener;
import se.spaced.client.view.entity.EntityViewListener;
import se.spaced.shared.util.AbstractListenerDispatcherModule;

public class ListenerDispatcherModule extends AbstractListenerDispatcherModule {
	@Override
	protected void configure() {
		register(ChatLogicListener.class);
		register(AbilityModelListener.class);
		register(UserCharacterListener.class);
		register(ServerConnectionListener.class);
		register(LoginListener.class);
		register(PlayerTargetingListener.class);
		register(EntityViewListener.class);
		register(EquipmentListener.class);
		register(ClientEntityListener.class);
		register(ZoneActivationListener.class);
	}
}
