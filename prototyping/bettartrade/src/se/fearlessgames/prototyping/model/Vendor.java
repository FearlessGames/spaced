package se.fearlessgames.prototyping.model;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

public class Vendor implements Buyer, Seller {
	private final int desiredStockCount;
	private final BlockingQueue<Item> itemsInStock;
	private final OrderStock orderStock;
	private final Seller resupplier;
	private final ItemPriceService priceService;
	private final ItemTemplate itemTemplate;
	private long stockValue;

	private final Map<Integer, Order> expectedOrders = Maps.newHashMap();
	private long money;

	public Vendor(int desiredStockCount, Seller resupplier, ItemPriceService priceService, ItemTemplate itemTemplate) {
		this.desiredStockCount = desiredStockCount;
		this.resupplier = resupplier;
		this.priceService = priceService;
		this.itemTemplate = itemTemplate;

		orderStock = new OrderStock(priceService);

		itemsInStock = Queues.newArrayBlockingQueue(desiredStockCount * 2);
		stockValue = 0;
	}

	@Override
	public Offer requestOffer(int quantity, ItemTemplate itemTemplate, final Buyer buyer) {
		if (itemsInStock.isEmpty()) {
			return new EmptyOffer(buyer);
		}
		return orderStock.createOrderOffer(quantity, buyer, getStockSize(), desiredStockCount, getSellPrice());
	}

	@Override
	public Order acceptOffer(Offer offer, int payments) {
		Order order = orderStock.acceptOffer(offer);
		money += payments;
		stockValue -= payments;
		return order;
	}

	@Override
	public int getSellPrice() {
		return (int) (getAverageItemPrice() * 1.25);
	}

	private void handleReorder() {
		int totalItemQuantity = orderStock.getTotalItemQuantity();
		int numberOfOrderedItems = getNumberOfOrderedItems();
		int orderSize = desiredStockCount - itemsInStock.size() + totalItemQuantity - numberOfOrderedItems;
		if (orderSize > 0) {
			Offer offer = resupplier.requestOffer(orderSize, itemTemplate, this);
			if (offer.getQuantity() > 0) {
				if (offer.getTotalPrice() <= money) {
					money -= offer.getTotalPrice();
					stockValue += offer.getTotalPrice();
					Order resupplyOrder = resupplier.acceptOffer(offer, offer.getTotalPrice());
					expectedOrders.put(resupplyOrder.getOrderId(), resupplyOrder);
				}
			}
		}
	}

	private int getNumberOfOrderedItems() {
		int sum = 0;
		for (Order order : expectedOrders.values()) {
			sum += order.getQuantity();
		}
		return sum;
	}

	public void tick() {
		Order order = orderStock.getNextOrder();
		if (order != null) {
			handleOrder(order);
		}


		handleReorder();
	}

	private void handleOrder(Order order) {
		List<Item> items = Lists.newArrayList();

		itemsInStock.drainTo(items, order.getQuantity());

		order.deliver(items);

		if (items.size() < order.getQuantity()) {
			orderStock.insert(new Order(order.getOrderId(), order.getQuantity() - items.size(), order.getPrice(), order.getBuyer()));
		}
	}

	public int getStockSize() {
		return itemsInStock.size();
	}

	@Override
	public void receive(Order order, Collection<Item> items) {
		Order existingOrder = expectedOrders.remove(order.getOrderId());
		if (existingOrder != null) {
			int diff = existingOrder.getQuantity() - items.size();
			if (diff > 0) {
				expectedOrders.put(order.getOrderId(), new Order(order.getOrderId(), diff, order.getPrice(), order.getBuyer()));
			}
		}
		itemsInStock.addAll(items);
	}

	public Collection<Order> getExpectedOrders() {
		return expectedOrders.values();
	}

	public void giveMoney(long amount) {
		money += amount;
	}

	public long getMoney() {
		return money;
	}

	public long getAverageItemPrice() {
		if (itemsInStock.isEmpty()) {
			return itemTemplate.getBasePrice();
		}
		return stockValue / (getStockSize() + getNumberOfOrderedItems());
	}
}

