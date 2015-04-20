package se.fearlessgames.prototyping.trade.model;

import se.fearlessgames.common.util.uuid.UUID;

public class Item {
	private final UUID id;
	private final ItemTemplate itemTemplate;
	private final String name;
	private final int price;

	public Item(UUID id, ItemTemplate itemTemplate, String name, int price) {
		this.id = id;
		this.itemTemplate = itemTemplate;
		this.name = name;
		this.price = price;
	}

	public String getName() {
		return name;
	}

	public int getPrice() {
		return price;
	}

	public UUID getId() {
		return id;
	}

	public ItemTemplate getItemTemplate() {
		return itemTemplate;
	}

	@Override
	public String toString() {
		return name + "(" + price + ")";
	}
}
