package se.fearlessgames.prototyping.trade.observers;

import se.fearlessgames.prototyping.trade.model.Item;
import se.fearlessgames.prototyping.trade.model.ItemTemplate;

public interface SupplierObserver {
	void startedResupplying(ItemTemplate itemTemplate);

	void notifyCancelAllDeliveries();

	void notifyRunningDeliveryFromSupplier(Item item);
}
