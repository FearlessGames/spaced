package se.spaced.server.model.items;

import se.fearless.common.uuid.UUID;
import se.spaced.messages.protocol.InventoryData;
import se.spaced.messages.protocol.SpacedInventory;
import se.spaced.server.model.ServerEntity;

public interface Inventory extends SpacedInventory, InventoryData {
	int getNrOfSlots();

	ServerEntity getOwner();

	InventoryType getType();

	boolean isFull();

	boolean contains(ServerItem item);

	ServerItem getItem(UUID itemPk);

	@Override
	UUID getPk();
}
