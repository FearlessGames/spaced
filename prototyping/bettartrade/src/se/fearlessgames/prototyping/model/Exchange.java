package se.fearlessgames.prototyping.model;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.fearlessgames.common.collections.Collections3;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Exchange implements Seller {
	private final Logger log = LoggerFactory.getLogger(getClass());
	private final int desiredStockCount;
	private final BlockingQueue<Item> itemsInStock;
	private final OrderStock orderStock;
	private long money;
	private final ItemTemplate itemTemplate;

	public Exchange(int desiredStockCount, ItemPriceService priceService, ItemTemplate itemTemplate) {
		this.desiredStockCount = desiredStockCount;
		this.itemTemplate = itemTemplate;
		itemsInStock = new ArrayBlockingQueue<Item>(desiredStockCount * 2);
		orderStock = new OrderStock(priceService);
	}

	public int getNumberOfOutstandingOrders() {
		return orderStock.getSize();
	}

	public long addItems(Collection<Item> items) {
		log.info("Received new items {}", items.size());
		if (itemsInStock.remainingCapacity() == 0) {
			return 0;
		}

		try {
			long sum = Collections3.sum(items, new Function<Item, Integer>() {
				@Override
				public Integer apply(Item item) {
					return item.getBasePrice();
				}
			});
			long balance = money - sum;
			if (balance < 0) {
				log.info("Exchange can't afford {} items @ {}. {}", items.size(), sum, money);
				return 0;
			}
			itemsInStock.addAll(items);
			money = balance;
			return sum;
		} catch (Exception e) {
			log.error("Failed to addItems", e);
			return 0;
		}
	}

	public int getNumberOfItemsInStock() {
		return itemsInStock.size();
	}

	public void tick() {
		Order order = orderStock.getNextOrder();
		if (order == null) {
			return;
		}

		List<Item> items = Lists.newArrayList();

		itemsInStock.drainTo(items, order.getQuantity());

		if (!items.isEmpty()) {
			order.deliver(items);
		}

		if (items.size() < order.getQuantity()) {
			orderStock.insert(new Order(order.getOrderId(), order.getQuantity() - items.size(), order.getPrice(), order.getBuyer()));
		}
	}

	public int getNumberOfItemsInBacklog() {
		return orderStock.getTotalItemQuantity();
	}

	@Override
	public Offer requestOffer(int quantity, ItemTemplate itemTemplate, Buyer buyer) {
		return orderStock.createOrderOffer(quantity, buyer, getNumberOfItemsInStock(), desiredStockCount, itemTemplate.getBasePrice());
	}

	@Override
	public Order acceptOffer(Offer offer, int payments) {
		money += payments;
		return orderStock.acceptOffer(offer);
	}

	@Override
	public int getSellPrice() {
		return itemTemplate.getBasePrice();
	}

	public void giveMoney(int amount) {
		money += amount;
	}

	public long getMoney() {
		return money;
	}
}
