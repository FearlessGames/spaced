package se.fearlessgames.prototyping.trade.model;

import com.google.common.collect.Maps;
import se.fearlessgames.common.util.uuid.UUID;
import se.fearlessgames.prototyping.trade.observers.VendorStockObserver;

import java.util.List;
import java.util.Map;

public class Vendor {
	private final Map<UUID, Item> stock = Maps.newHashMap();
	private final Map<ItemTemplate, Integer> wantedStock;
	private VendorStockObserver vendorStockObserver;

	private final ItemExchange itemExchange;
	private String name;

	public Vendor(ItemExchange itemExchange, Map<ItemTemplate, Integer> wantedStock, String name) {
		this.itemExchange = itemExchange;
		this.wantedStock = wantedStock;
		this.name = name;
	}

	public void setObserver(VendorStockObserver vendorStockObserver) {
		this.vendorStockObserver = vendorStockObserver;
	}

	public void resuplyToInitialState() {
		for (ItemTemplate itemTemplate : wantedStock.keySet()) {
			Integer requestedAmount = wantedStock.get(itemTemplate);
			if (vendorStockObserver != null) {
				vendorStockObserver.notifyOrderedItems(itemTemplate, requestedAmount);
			}
			itemExchange.orderItems(itemTemplate, requestedAmount, this, true);
		}
	}

	public Item purchase(UUID id) {
		Item item = stock.remove(id);
		itemExchange.orderItems(item.getItemTemplate(), 1, this, false);
		if (vendorStockObserver != null) {
			vendorStockObserver.notifyOrderedItems(item.getItemTemplate(), 1);
			vendorStockObserver.notifyPurchase(item);
		}
		return item;
	}

	public Item purchaseRandom() {
		UUID uuidToPurchase = null;
		for (UUID uuid : stock.keySet()) {
			uuidToPurchase = uuid;
			break;
		}
		return purchase(uuidToPurchase);
	}


	public void resupply(List<Item> items) {
		for (Item resuppliedItem : items) {
			stock.put(resuppliedItem.getId(), resuppliedItem);
		}
		if (vendorStockObserver != null) {
			vendorStockObserver.notifyResupply(items);
		}
	}

	public String getName() {
		return name;
	}
}
