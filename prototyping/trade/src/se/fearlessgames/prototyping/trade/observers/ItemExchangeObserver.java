package se.fearlessgames.prototyping.trade.observers;

import se.fearlessgames.prototyping.trade.model.Item;
import se.fearlessgames.prototyping.trade.model.ItemTemplate;
import se.fearlessgames.prototyping.trade.model.Vendor;

import java.util.List;

public interface ItemExchangeObserver {
	void notifyIncomingOrderFromVendor(ItemTemplate itemTemplate, int requestedAmount, Vendor vendor, boolean instantDelivery);

	void notifyAddingToRestOrderQueue(ItemTemplate itemTemplate, int amountOfRestOrdersToCreate, Vendor vendor);

	void notifyInstantDeliveryToVendor(List<Item> bookedItems);

	void notifyNewScheduledDelivery(Vendor vendor, List<Item> bookedItems);

	void notifyPeriodicRestChew();

	void notifyGotResuppliedWith(Item item);

	void notifyDeniedResuppliedDueToMaxStockFor(Item item);

	void notifyRunningDeliveryFromExchange(List<Item> bookedItems);
}
