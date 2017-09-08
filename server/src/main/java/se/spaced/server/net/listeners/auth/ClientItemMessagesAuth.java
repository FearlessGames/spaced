package se.spaced.server.net.listeners.auth;

import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.fearless.common.uuid.UUID;
import se.spaced.messages.protocol.SpacedInventory;
import se.spaced.messages.protocol.SpacedItem;
import se.spaced.messages.protocol.c2s.ClientItemMessages;
import se.spaced.server.model.Player;
import se.spaced.server.model.ServerEntity;
import se.spaced.server.model.combat.EntityTargetService;
import se.spaced.server.model.crafting.SalvageService;
import se.spaced.server.model.items.Inventory;
import se.spaced.server.model.items.InventoryService;
import se.spaced.server.model.items.ItemService;
import se.spaced.server.model.items.ItemTemplateDataFactory;
import se.spaced.server.model.items.ServerItem;
import se.spaced.server.model.items.ServerItemTemplate;
import se.spaced.server.net.ClientConnection;

import java.util.ArrayList;
import java.util.Collection;

public class ClientItemMessagesAuth implements ClientItemMessages {
	private final Logger log = LoggerFactory.getLogger(getClass());
	private final ClientConnection clientConnection;
	private final ItemService itemService;
	private final EntityTargetService targetService;
	private final InventoryService inventoryService;
	private final SalvageService salvageService;

	public ClientItemMessagesAuth(ClientConnection clientConnection, ItemService itemService, EntityTargetService targetService,
											InventoryService inventoryService, SalvageService salvageService) {
		this.clientConnection = clientConnection;
		this.itemService = itemService;
		this.targetService = targetService;
		this.inventoryService = inventoryService;
		this.salvageService = salvageService;
	}

	@Override
	public void useItem(SpacedItem item) {
		ServerItem serverItem = (ServerItem) item;
		Player player = clientConnection.getPlayer();
		if (player == null) {
			throw new IllegalStateException("Tried to useItem when not ingame: " + clientConnection);
		}
		if (item == null) {
			log.warn("Trying to use null item. " + player);
			return;
		}
		if (itemService.isOwner(player, serverItem)) {
			ServerEntity target = targetService.getCurrentTarget(player);
			if (target == null) {
				target = player;
			}
			itemService.useItem(player, target, serverItem);
		}
	}

	@Override
	public void deleteItem(SpacedItem item) {
		ServerItem serverItem = (ServerItem) item;
		Player player = clientConnection.getPlayer();
		if (player == null) {
			throw new IllegalStateException("Tried to deleteItem when not ingame: " + clientConnection);
		}
		if (item == null) {
			log.warn("Trying to delete null item. " + player);
			return;
		}

		if (itemService.isOwner(player, serverItem)) {
			itemService.deleteItem(serverItem);
		}
	}

	@Override
	public void requestItemTemplateData(UUID pk) {
		ServerItemTemplate itemTemplate = itemService.getTemplateByPk(pk);
		clientConnection.getReceiver().item().itemTemplateDataResponse(ItemTemplateDataFactory.create(itemTemplate));
	}

	@Override
	public void switchItemsAtPositions(SpacedInventory inventory1, int pos1, SpacedInventory inventory2, int pos2) {
		Player player = clientConnection.getPlayer();
		Inventory serverInventory1 = (Inventory) inventory1;
		Inventory serverInventory2 = (Inventory) inventory2;
		if (!isOwner(player, serverInventory1) || !isOwner(player, serverInventory2)) {
			throw new IllegalStateException(String.format(
					"%s trying to access inventories not under that players control. %s %s",
					player.toString(),
					inventory1.toString(),
					inventory2.toString()));
		}
		inventoryService.move(serverInventory1, pos1, serverInventory2, pos2);
	}

	@Override
	public void salvageItem(Collection<? extends SpacedItem> items) {
		Player player = clientConnection.getPlayer();
		if (player == null) {
			throw new IllegalStateException("Tried to salvage an item when not in game");
		}
		if (items.isEmpty()) {
			throw new IllegalStateException(String.format("%s Tried to salvage without sending any items", player.getName()));
		}
		if (!isOwner(player, items)) {
			throw new IllegalStateException(String.format("%s trying to salvage an item not owned %s",
					player.getName(),
					items));
		}
		ArrayList<ServerItem> serverItems = Lists.newArrayList();
		for (SpacedItem item : items) {
			serverItems.add((ServerItem) item);
		}
		salvageService.salvage(player, serverItems);
	}

	private boolean isOwner(ServerEntity entity, Collection<? extends SpacedItem> items) {
		for (SpacedItem item : items) {
			if (!itemService.isOwner(entity, (ServerItem) item)) {
				return false;
			}
		}
		return true;
	}

	private boolean isOwner(ServerEntity entity, Inventory inventory) {
		return inventory.getOwner().equals(entity);
	}
}