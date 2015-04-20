package se.spaced.client.ardor.ui.api;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import se.fearlessgames.common.util.SystemTimeProvider;
import se.fearlessgames.common.util.uuid.UUIDFactory;
import se.fearlessgames.common.util.uuid.UUIDFactoryImpl;
import se.krka.kahlua.integration.annotations.LuaMethod;
import se.spaced.client.model.item.ClientItem;
import se.spaced.client.model.player.PlayerActions;
import se.spaced.client.statistics.Analytics;
import se.spaced.client.statistics.Trackables;
import se.spaced.messages.protocol.AuraTemplate;
import se.spaced.messages.protocol.ItemTemplateData;
import se.spaced.shared.model.AppearanceData;
import se.spaced.shared.model.Money;
import se.spaced.shared.model.items.ContainerType;
import se.spaced.shared.model.items.ItemType;

import java.util.HashSet;
import java.util.Random;

@Singleton
public class PlayerActionsApi {
	private final PlayerActions playerActions;
	private final Analytics analytics;

	@Inject
	public PlayerActionsApi(PlayerActions playerActions, Analytics analytics) {
		this.playerActions = playerActions;
		this.analytics = analytics;
	}

	@LuaMethod(global = true, name = "AcceptRess")
	public void acceptRess() {
		playerActions.resurrect();
	}

	@LuaMethod(global = true, name = "Dance")
	public void dance() {
		analytics.track(Trackables.PlayerActionsEvents.DANCE);
		playerActions.dance();
	}

	@LuaMethod(global = true, name = "Sit")
	public void sit() {
		playerActions.sit();
	}

	@LuaMethod(global = true, name = "Sleep")
	public void sleep() {
		playerActions.sleep();
	}

	@LuaMethod(global = true, name = "Equip")
	public void equip(ContainerType container, String xmoFile) {
		UUIDFactory uuidFactory = new UUIDFactoryImpl(new SystemTimeProvider(), new Random());
		ClientItem clientItem = new ClientItem(uuidFactory.randomUUID(),
				new ItemTemplateData(uuidFactory.randomUUID(), "name", new AppearanceData(xmoFile, ""),
						new HashSet<ItemType>(), new HashSet<AuraTemplate>(), Money.ZERO, null));
		playerActions.equip(clientItem, container);
	}

	@LuaMethod(global = true, name = "Equip")
	public void equip(ClientItem item, ContainerType container) {
		playerActions.equip(item, container);
	}

	@LuaMethod(global = true, name = "Equip")
	public void equip(ClientItem item) {
		playerActions.equip(item);
	}

	@LuaMethod(global = true, name = "Unequip")
	public void unequip(ContainerType container) {
		playerActions.unequip(container);
	}

	@LuaMethod(global = true, name = "Unstuck")
	public void unstuck() {
		analytics.track(Trackables.PlayerActionsEvents.UNSTUCK);
		playerActions.unstuck();
	}
}
