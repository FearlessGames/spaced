package se.fearlessgames.prototyping.model;

import com.google.common.collect.Lists;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

public class Supplier {
	private final int maxStock;
	private long manufactureTime;
	private long deliveryInterval;

	private final Queue<Item> createdItemsInStock;
	private Exchange exchange;
	private final ItemTemplate itemTemplate;

	public Supplier(int maxStock, ItemTemplate itemTemplate) {
		this.maxStock = maxStock;
		this.itemTemplate = itemTemplate;
		createdItemsInStock = new ArrayBlockingQueue<Item>(maxStock);
	}

	public void tick() {
		createdItemsInStock.offer(new Item(itemTemplate));

		if (!createdItemsInStock.isEmpty() && exchange != null) {
			Item item = createdItemsInStock.poll();
			exchange.addItems(Lists.newArrayList(item));
		}

	}

	public int getLocalStockSize() {
		return createdItemsInStock.size();
	}

	public void registerExchange(Exchange exchange) {
		this.exchange = exchange;
	}
}
