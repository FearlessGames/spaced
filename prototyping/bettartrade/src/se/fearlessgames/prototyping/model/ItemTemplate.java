package se.fearlessgames.prototyping.model;

public class ItemTemplate {
	private final int basePrice;

	public ItemTemplate(int basePrice) {
		this.basePrice = basePrice;
	}

	public int getBasePrice() {
		return basePrice;
	}
}
