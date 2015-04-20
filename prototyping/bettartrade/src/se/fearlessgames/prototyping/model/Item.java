package se.fearlessgames.prototyping.model;

public class Item {
	private final ItemTemplate itemTemplate;

	public Item(ItemTemplate itemTemplate) {
		this.itemTemplate = itemTemplate;
	}

	public int getBasePrice() {
		return itemTemplate.getBasePrice();
	}
}
