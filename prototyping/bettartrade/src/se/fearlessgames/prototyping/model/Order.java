package se.fearlessgames.prototyping.model;

import java.util.Collection;

public class Order {
	private final int orderId;
	private final int quantity;
	private final int price;
	private final Buyer buyer;

	public Order(int orderId, int quantity, int price, Buyer buyer) {
		this.orderId = orderId;
		this.quantity = quantity;
		this.price = price;
		this.buyer = buyer;
	}


	public int getOrderId() {
		return orderId;
	}

	public int getQuantity() {
		return quantity;
	}

	public void deliver(Collection<Item> items) {
		buyer.receive(this, items);

	}

	public Buyer getBuyer() {
		return buyer;
	}

	public int getPrice() {
		return price;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		Order order = (Order) o;

		if (orderId != order.orderId) {
			return false;
		}
		if (price != order.price) {
			return false;
		}
		if (quantity != order.quantity) {
			return false;
		}
		if (buyer != null ? !buyer.equals(order.buyer) : order.buyer != null) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		int result = orderId;
		result = 31 * result + quantity;
		result = 31 * result + price;
		result = 31 * result + (buyer != null ? buyer.hashCode() : 0);
		return result;
	}
}
