package se.spaced.messages.protocol.c2s;

import se.smrt.core.SmrtProtocol;
import se.spaced.messages.protocol.Entity;
import se.spaced.messages.protocol.SpacedItem;

import java.util.Collection;

@SmrtProtocol
public interface ClientVendorMessages {
	void requestVendorStock(Entity vendor);

	void playerBuysItemFromVendor(Entity vendor, SpacedItem item);

	void playerSellsItemsToVendor(Entity vendor, Collection<? extends SpacedItem> items);

	void endVendoring(Entity vendor);

	void startVendoring(Entity vendor);
}
