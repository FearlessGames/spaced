package se.fearlessgames.prototyping.model;

import java.util.Deque;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;

public class OrderStock {
	private AtomicInteger counter = new AtomicInteger();
	private Deque<Order> orderQueue = new LinkedList<Order>();
	private final ItemPriceService itemPriceService;


	public OrderStock(ItemPriceService itemPriceService) {
		this.itemPriceService = itemPriceService;
	}

	public Offer createOrderOffer(final int quantity, final Buyer buyer, int currentStock, int desiredStock, int basePrice) {

		final int price = itemPriceService.getPriceForItemBasedOnStock(desiredStock, currentStock, basePrice);

		return new Offer() {
			@Override
			public int getTotalPrice() {
				return price * quantity;
			}

			@Override
			public Buyer getBuyer() {
				return buyer;
			}

			@Override
			public int getQuantity() {
				return quantity;
			}

		};
	}

	public Order acceptOffer(Offer offer) {
		Order order = new Order(counter.incrementAndGet(), offer.getQuantity(), offer.getTotalPrice(), offer.getBuyer());
		orderQueue.add(order);
		return order;
	}

	public Order getNextOrder() {
		return orderQueue.poll();
	}

	public void insert(Order order) {
		orderQueue.push(order);
	}

	public int getTotalItemQuantity() {
		int qty = 0;
		for (Order order : orderQueue) {
			qty += order.getQuantity();
		}
		return qty;
	}

	public int getSize() {
		return orderQueue.size();
	}

}
