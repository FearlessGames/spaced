package se.spaced.messages.protocol.s2c;

import se.smrt.core.SmrtProtocol;
import se.spaced.messages.protocol.Entity;
import se.spaced.messages.protocol.ItemTemplate;
import se.spaced.messages.protocol.SpacedItem;
import se.spaced.shared.model.items.ContainerType;
import se.spaced.shared.model.items.EquipFailure;
import se.spaced.shared.model.items.UnequipFailure;

@SmrtProtocol
public interface ServerEquipmentMessages {
	void equippedItem(SpacedItem item, ContainerType type);

	void unequippedItem(SpacedItem item, ContainerType type);

	void entityEquippedItem(Entity entity, ItemTemplate item, ContainerType type);

	void entityUnequippedItem(Entity entity, ContainerType type);

	void failedToEquipItem(SpacedItem item, EquipFailure reason);

	void failedToUnequipItem(SpacedItem item, UnequipFailure reason);
}
