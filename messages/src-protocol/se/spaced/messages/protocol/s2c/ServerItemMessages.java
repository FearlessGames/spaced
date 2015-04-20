package se.spaced.messages.protocol.s2c;

import se.smrt.core.SmrtProtocol;
import se.spaced.messages.protocol.InventoryData;
import se.spaced.messages.protocol.ItemTemplateData;
import se.spaced.messages.protocol.SpacedInventory;
import se.spaced.messages.protocol.SpacedItem;

import java.util.List;

@SmrtProtocol
public interface ServerItemMessages {
	void sendInventory(InventoryData inventory);

	void itemTemplateDataResponse(ItemTemplateData data);

	void itemAdded(SpacedInventory inventory, int position, SpacedItem item);

	void itemRemoved(SpacedInventory inventory, int position, SpacedItem item);

	void itemsSwapped(SpacedInventory inventory1, int pos1, SpacedInventory inventory2, int pos2);

	void itemsMoved(SpacedInventory inventory1, int pos1, List<? extends SpacedItem> movedItems, SpacedInventory inventory2, int pos2);
}
