package se.fearlessgames.prototyping.trade.model;

import com.google.common.collect.Lists;
import se.fearlessgames.prototyping.trade.observers.SupplierObserver;

import java.util.List;
import java.util.Map;

public class Supplier {
	private final ItemExchange itemExchange;
	private Map<ItemTemplate, Integer> resupplyMap;
	private final ItemDeliveryService itemDeliveryService;
	private List<ItemDeliveryService.CancableItemDelivery> ongoingDeliveries = Lists.newArrayList();
	private SupplierObserver supplierObserver;
	private String name;

	public Supplier(ItemExchange itemExchange, Map<ItemTemplate, Integer> resupplyMap, ItemDeliveryService itemDeliveryService, String name) {
		this.itemExchange = itemExchange;
		this.resupplyMap = resupplyMap;
		this.itemDeliveryService = itemDeliveryService;
		this.name = name;
	}

	public void setObserver(SupplierObserver supplierObserver) {
		this.supplierObserver = supplierObserver;
	}

	/**
	 * optimize scheduling by interval
	 */
	public void startResuply() {
		for (ItemTemplate itemTemplate : resupplyMap.keySet()) {
			ItemDeliveryService.DeliveryToExchangeFromSupplier deliveryToExchangeFromSupplier = new ItemDeliveryService.DeliveryToExchangeFromSupplier(resupplyMap.get(
					itemTemplate), itemExchange, itemTemplate, supplierObserver);
			ItemDeliveryService.CancableItemDelivery itemDelivery = itemDeliveryService.scheduleExchangeResupplyFromSupplier(deliveryToExchangeFromSupplier);
			ongoingDeliveries.add(itemDelivery);
			if (supplierObserver != null) {
				supplierObserver.startedResupplying(itemTemplate);
			}
		}
	}


	public void kill() {
		for (ItemDeliveryService.CancableItemDelivery ongoingDelivery : ongoingDeliveries) {
			ongoingDelivery.cancel();
		}
		ongoingDeliveries.clear();
		supplierObserver.notifyCancelAllDeliveries();
	}

	public String getName() {
		return name;
	}
}
