package se.spaced.server.model.items;

import se.spaced.server.model.ServerEntity;
import se.spaced.shared.model.items.ContainerType;

public interface EquipmentService {

	void equipItem(ServerEntity entity, ContainerType type, ServerItem item);

	void unequipItem(ServerEntity entity, ContainerType type);

	ServerItem getEquippedItem(ServerEntity entity, ContainerType type);

	void applyAuras(ServerEntity entity);

	EquippedItems getEquippedItems(ServerEntity entity);

	void createEquippedItemsForEntity(ServerEntity entity);
}
