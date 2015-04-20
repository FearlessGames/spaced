package se.spaced.messages.protocol.c2s;

import se.fearlessgames.common.util.uuid.UUID;
import se.smrt.core.SmrtProtocol;
import se.spaced.messages.protocol.SpacedInventory;
import se.spaced.messages.protocol.SpacedItem;

import java.util.Collection;

@SmrtProtocol
public interface ClientItemMessages {
	void useItem(SpacedItem item);

	void deleteItem(SpacedItem item);

	void requestItemTemplateData(UUID pk);

	void switchItemsAtPositions(SpacedInventory inventory1, int pos1, SpacedInventory inventory2, int pos2);

	void salvageItem(Collection<? extends SpacedItem> item);
}
