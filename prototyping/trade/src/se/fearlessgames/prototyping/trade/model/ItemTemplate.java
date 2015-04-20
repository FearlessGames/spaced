package se.fearlessgames.prototyping.trade.model;

import se.fearlessgames.common.util.uuid.UUID;
import se.fearlessgames.common.util.uuid.UUIDFactory;

public class ItemTemplate {
	private final UUID id;
	private final UUIDFactory uuidFactory;
	private final String name;
	private final int price;

	public ItemTemplate(UUIDFactory uuidFactory, String name, int price) {
		this.uuidFactory = uuidFactory;
		this.id = uuidFactory.randomUUID();
		this.name = name;
		this.price = price;
	}

	public Item createItem() {
		return new Item(uuidFactory.randomUUID(), this, name, price);
	}

	public String getName() {
		return name;
	}
}
