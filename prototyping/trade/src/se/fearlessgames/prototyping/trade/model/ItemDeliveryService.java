package se.fearlessgames.prototyping.trade.model;

import se.fearlessgames.prototyping.trade.observers.ItemExchangeObserver;
import se.fearlessgames.prototyping.trade.observers.SupplierObserver;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * use future / callable to track success of resupply to throttle rate?
 */

public class ItemDeliveryService {


	private final ScheduledExecutorService scheduler;

	public ItemDeliveryService(int threads) {
		scheduler = Executors.newScheduledThreadPool(threads);
	}

	public CancableItemDelivery scheduleExchangeResupplyFromSupplier(DeliveryToExchangeFromSupplier deliveryToExchangeFromSupplier) {
		return new CancableItemDelivery(scheduler.scheduleWithFixedDelay(deliveryToExchangeFromSupplier,
				0,
				deliveryToExchangeFromSupplier.getResuplyRateInSeconds(),
				TimeUnit.SECONDS));
	}

	public void scheduleVendorResupply(DeliveryToVendorFromExchange deliveryToVendorFromExchange) {
		scheduler.schedule(deliveryToVendorFromExchange, deliveryToVendorFromExchange.getSecondsToDelivery(), TimeUnit.SECONDS);
	}

	public static class CancableItemDelivery {
		private final Future<?> future;

		public CancableItemDelivery(Future<?> future) {
			this.future = future;
		}

		public void cancel() {
			future.cancel(false);
		}
	}

	public static class DeliveryToVendorFromExchange implements Runnable {
		private final Vendor vendor;
		private final List<Item> bookedItems;
		private final int secondsToDelivery;
		private final ItemExchangeObserver itemExchangeObserver;

		public DeliveryToVendorFromExchange(Vendor vendor, List<Item> bookedItems, int secondsToDelivery, ItemExchangeObserver itemExchangeObserver) {
			this.vendor = vendor;
			this.bookedItems = bookedItems;
			this.secondsToDelivery = secondsToDelivery;
			this.itemExchangeObserver = itemExchangeObserver;
		}

		@Override
		public void run() {
			if (itemExchangeObserver != null) {
				itemExchangeObserver.notifyRunningDeliveryFromExchange(bookedItems);
			}
			vendor.resupply(bookedItems);
		}

		public int getSecondsToDelivery() {
			return secondsToDelivery;
		}
	}

	public static class DeliveryToExchangeFromSupplier implements Runnable {
		private final int resuplyRateInSeconds;
		private final ItemExchange itemExchange;
		private final ItemTemplate itemTemplate;
		private final SupplierObserver supplierObserver;

		public int getResuplyRateInSeconds() {
			return resuplyRateInSeconds;
		}

		public DeliveryToExchangeFromSupplier(int resuplyRateInSeconds, ItemExchange itemExchange, ItemTemplate itemTemplate, SupplierObserver supplierObserver) {
			this.resuplyRateInSeconds = resuplyRateInSeconds;
			this.itemExchange = itemExchange;
			this.itemTemplate = itemTemplate;
			this.supplierObserver = supplierObserver;
		}

		@Override
		public void run() {
			Item item = itemTemplate.createItem();
			if (supplierObserver != null) {
				supplierObserver.notifyRunningDeliveryFromSupplier(item);
			}
			itemExchange.sellItemToExchange(item);
		}
	}


}
