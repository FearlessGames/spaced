package se.fearlessgames.prototyping.trade.model;

import com.google.common.collect.Lists;
import se.fearlessgames.prototyping.trade.observers.ItemExchangeObserver;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ItemExchange {
	public static final int TIME_TO_DELIVERY = 5;
	private final Map<ItemTemplate, ItemStock> stock;
	private final ItemDeliveryService itemDeliveryService;
	private final String name;
	private final List<RestOrder> restOrders = Lists.newArrayList();
	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
	private ItemExchangeObserver itemExchangeObserver;

	public ItemExchange(Map<ItemTemplate, ItemStock> stock, ItemDeliveryService itemDeliveryService, String name) {
		this.stock = stock;
		this.itemDeliveryService = itemDeliveryService;
		this.name = name;
	}

	public void startHandleRestOrders() {
		scheduler.scheduleWithFixedDelay(new ChewRestOrders(), 10, 10, TimeUnit.SECONDS);
	}

	public String getName() {
		return name;
	}

	public class ChewRestOrders implements Runnable {
		@Override
		public void run() {
			if (itemExchangeObserver != null) {
				itemExchangeObserver.notifyPeriodicRestChew();
			}
			ItemExchange.this.handleRestOrders();
		}
	}

	public void setObserver(ItemExchangeObserver itemExchangeObserver) {
		this.itemExchangeObserver = itemExchangeObserver;
	}

	public void orderItems(ItemTemplate itemTemplate, int requestedAmount, Vendor vendor, boolean instantDelivery) {
		if (itemExchangeObserver != null) {
			itemExchangeObserver.notifyIncomingOrderFromVendor(itemTemplate, requestedAmount, vendor, instantDelivery);
		}
		handleRestOrders();
		deliverItemsToVendor(itemTemplate, requestedAmount, vendor, instantDelivery);
	}

	private void deliverItemsToVendor(ItemTemplate itemTemplate, int requestedAmount, Vendor vendor, boolean instantDelivery) {
		ItemStock itemStock = stock.get(itemTemplate);
		List<Item> currentStock = itemStock.getStock();
		int amountToDeliver = 0;
		if (requestedAmount > currentStock.size()) {
			int amountOfRestOrdersToCreate = requestedAmount - currentStock.size();
			amountToDeliver = currentStock.size();
			RestOrder ro = new RestOrder(itemTemplate, amountOfRestOrdersToCreate, vendor);
			if (itemExchangeObserver != null) {
				itemExchangeObserver.notifyAddingToRestOrderQueue(itemTemplate, amountOfRestOrdersToCreate, vendor);
			}
			restOrders.add(ro);
		} else {
			amountToDeliver = requestedAmount;
		}

		List<Item> bookedItems = Lists.newArrayList();
		for (int i = 0; i < amountToDeliver; i++) {
			bookedItems.add(currentStock.remove(i));
		}

		if (instantDelivery) {
			if (itemExchangeObserver != null) {
				itemExchangeObserver.notifyInstantDeliveryToVendor(bookedItems);
			}
			vendor.resupply(bookedItems);
		} else {
			if (itemExchangeObserver != null) {
				itemExchangeObserver.notifyNewScheduledDelivery(vendor, bookedItems);
			}
			scheduleDelivery(vendor, bookedItems);
		}
	}

	/**
	 * this will consume the rest queue. A new will be created for all items that were not in stock.
	 */
	private void handleRestOrders() {
		List<RestOrder> orderToProcess = new ArrayList<RestOrder>(restOrders);
		restOrders.clear();
		for (RestOrder restOrder : orderToProcess) {
			deliverItemsToVendor(restOrder.itemTemplate, restOrder.amount, restOrder.vendor, false);
		}

	}

	public static class RestOrder {
		private final ItemTemplate itemTemplate;
		private final int amount;
		private Vendor vendor;

		public RestOrder(ItemTemplate itemTemplate, int amount, Vendor vendor) {
			this.itemTemplate = itemTemplate;
			this.amount = amount;
			this.vendor = vendor;
		}

		public ItemTemplate getItemTemplate() {
			return itemTemplate;
		}

		public int getAmount() {
			return amount;
		}

		public Vendor getVendor() {
			return vendor;
		}
	}

	public boolean sellItemToExchange(Item item) {
		ItemStock items = stock.get(item.getItemTemplate());
		if (items.getStock().size() <= items.getWantedStock()) {
			items.getStock().add(item);
			itemExchangeObserver.notifyGotResuppliedWith(item);
			return true;
		} else {
			itemExchangeObserver.notifyDeniedResuppliedDueToMaxStockFor(item);
			return false;
		}
	}

	//Use distance between vendor and exchange to calculate Time to delivery?
	private void scheduleDelivery(Vendor vendor, List<Item> bookedItems) {
		itemDeliveryService.scheduleVendorResupply(new ItemDeliveryService.DeliveryToVendorFromExchange(vendor,
				bookedItems,
				TIME_TO_DELIVERY,
				itemExchangeObserver));
	}
}
