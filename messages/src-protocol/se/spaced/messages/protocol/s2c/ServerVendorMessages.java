package se.spaced.messages.protocol.s2c;

import se.smrt.core.SmrtProtocol;
import se.spaced.messages.protocol.Entity;
import se.spaced.messages.protocol.SpacedItem;

import java.util.List;

@SmrtProtocol
public interface ServerVendorMessages {
	void vendorStockItems(Entity vendor, List<? extends SpacedItem> items);

	void cannotAfford(String currency, long amount);

	void vendorOutOfRange(Entity vendor);

	void vendorAddedItem(SpacedItem newItem);

	void boughtItem(SpacedItem item);

	void itemWasBought(SpacedItem item);

	void vendorDespawned(Entity vendor);

	void inventoryFull();
}
