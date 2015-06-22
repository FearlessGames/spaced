package se.spaced.server.persistence.dao.interfaces;

import se.spaced.server.model.ServerEntity;
import se.spaced.server.model.items.Inventory;
import se.spaced.server.model.items.InventoryType;
import se.spaced.server.model.items.PersistedInventory;
import se.spaced.server.model.items.ServerItem;

import java.util.List;

public interface InventoryDao extends Dao<PersistedInventory> {

	PersistedInventory findInventory(ServerEntity player, InventoryType type);

	PersistedInventory findInventory(ServerItem serverItem);

	List<PersistedInventory> findByOwner(ServerEntity entity);

	PersistedInventory refresh(Inventory inventory);
}
