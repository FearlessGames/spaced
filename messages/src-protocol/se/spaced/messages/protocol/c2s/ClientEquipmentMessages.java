package se.spaced.messages.protocol.c2s;

import se.smrt.core.SmrtProtocol;
import se.spaced.messages.protocol.SpacedItem;
import se.spaced.shared.model.items.ContainerType;

@SmrtProtocol
public interface ClientEquipmentMessages {
	void equipItem(SpacedItem item, ContainerType type);

	void unequipItem(ContainerType type);
}
