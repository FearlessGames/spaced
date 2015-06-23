package se.spaced.client.ardor.ui.api;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import se.krka.kahlua.integration.annotations.LuaMethod;
import se.spaced.client.model.InventoryProvider;
import se.spaced.client.model.item.ClientInventory;
import se.spaced.client.model.item.ClientItem;
import se.spaced.client.model.player.PlayerEquipment;
import se.spaced.client.net.smrt.ServerConnection;
import se.spaced.messages.protocol.SpacedItem;
import se.spaced.shared.model.items.ContainerType;

@Singleton
public class ItemApi {
	private final ServerConnection serverConnection;
	private final InventoryProvider inventoryProvider;
	private final PlayerEquipment equipment;

	@Inject
	public ItemApi(ServerConnection serverConnection, InventoryProvider inventoryProvider, PlayerEquipment equipment) {
		this.serverConnection = serverConnection;
		this.inventoryProvider = inventoryProvider;
		this.equipment = equipment;
	}

	@LuaMethod(name = "UseItem", global = true)
	public void useItem(ClientItem item) {
		if (item != null) {
			serverConnection.getReceiver().items().useItem(item);
		}
	}

	@LuaMethod(name = "DeleteItem", global = true)
	public void deleteItem(ClientItem item) {
		if (item != null) {
			serverConnection.getReceiver().items().deleteItem(item);
		}
	}


	@LuaMethod(name = "GetInventory", global = true)
	public ClientInventory getInventory() {
		return inventoryProvider.getPlayerInventory();
	}

	@LuaMethod(name = "GetEquipmentSlotInfo", global = true)
	public ClientItem getEquipmentSlotInfo(ContainerType container) {
		return equipment.findEquippedItem(container);
	}

	@LuaMethod(name = "SwitchItemAtPositions", global = true)
	public void switchItemsAtPositions(ClientInventory inventory1, int pos1, ClientInventory inventory2, int pos2) {
		serverConnection.getReceiver().items().switchItemsAtPositions(inventory1, pos1, inventory2, pos2);
	}

	@LuaMethod(name = "SalvageItem", global = true)
	public void salvageItem(ClientItem item) {
		if (item != null) {
			serverConnection.getReceiver().items().salvageItem(Lists.newArrayList((SpacedItem) item));
		}
	}
}
