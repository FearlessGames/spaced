package se.fearlessgames.prototyping.trade.observers;

import se.fearlessgames.prototyping.trade.model.Item;
import se.fearlessgames.prototyping.trade.model.ItemTemplate;

import java.util.List;

public interface VendorStockObserver {
	public void notifyPurchase(Item item);

	public void notifyResupply(List<Item> items);

	public void notifyOrderedItems(ItemTemplate itemTemplate, int amount);
}
