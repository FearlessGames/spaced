package se.spaced.client.model.listener;

import se.spaced.messages.protocol.SpacedItem;
import se.spaced.shared.model.PositionalData;
import se.spaced.shared.model.items.ContainerType;

public interface UserCharacterListener {
	void combatStateUpdated(boolean entered);

	void resurrectionRequested();

	void positionalDataUpdated(PositionalData positionalData);

	void equip(SpacedItem clientItem, ContainerType containerType);
	void unequip(ContainerType containerType);

}
