package se.spaced.server.persistence.dao.impl.inmemory;

import com.google.common.collect.Lists;
import se.spaced.server.model.ServerEntity;
import se.spaced.server.model.items.Inventory;
import se.spaced.server.model.items.InventoryType;
import se.spaced.server.model.items.PersistedInventory;
import se.spaced.server.model.items.ServerItem;
import se.spaced.server.persistence.dao.interfaces.InventoryDao;

import java.util.List;

public class InMemoryInventoryDao extends InMemoryDao<PersistedInventory> implements InventoryDao {

	@Override
	public PersistedInventory findInventory(ServerEntity player, InventoryType type) {
		for (PersistedInventory persistedInventory : data.values()) {
			if (persistedInventory.getOwner().equals(player)) {
				return persistedInventory;
			}
		}
		return null;
	}


	@Override
	public PersistedInventory findInventory(ServerItem serverItem) {
		for (PersistedInventory persistedInventory : data.values()) {
			if (persistedInventory.contains(serverItem)) {
				return persistedInventory;
			}
		}
		return null;
	}

	@Override
	public List<PersistedInventory> findByOwner(ServerEntity entity) {
		List<PersistedInventory> result = Lists.newArrayList();
		for (PersistedInventory persistedInventory : data.values()) {
			if (persistedInventory.getOwner().equals(entity)) {
				result.add(persistedInventory);
			}
		}
		return result;
	}

	@Override
	public PersistedInventory refresh(Inventory inventory) {
		return (PersistedInventory) inventory;
	}
}