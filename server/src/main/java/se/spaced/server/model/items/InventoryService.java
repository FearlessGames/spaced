package se.spaced.server.model.items;

import se.spaced.server.model.ServerEntity;

public interface InventoryService {
	PersistedInventory getInventory(ServerEntity player, InventoryType type);

	PersistedInventory createInventory(ServerEntity entity, int capacity, InventoryType type);

	void add(Inventory inventory, ServerItem item) throws InventoryFullException;


	void move(Inventory inventory1, int pos1, Inventory inventory2, int pos2);

	void move(Inventory inventory1, int pos1, int quantity, Inventory inventory2, int pos2);

	void removeIfExists(ServerItem serverItem);


}
