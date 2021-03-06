package se.spaced.client.model.item;

import se.fearless.common.uuid.UUID;
import se.spaced.messages.protocol.ItemTemplate;
import se.spaced.messages.protocol.SpacedItem;

public class ClientItemProxy implements SpacedItem {
	private final UUID pk;
	private final ItemTemplate itemTemplate;

	public ClientItemProxy(UUID pk, ItemTemplate itemTemplate) {
		this.pk = pk;
		this.itemTemplate = itemTemplate;
	}

	@Override
	public UUID getPk() {
		return pk;
	}

	@Override
	public ItemTemplate getItemTemplate() {
		return itemTemplate;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof SpacedItem)) {
			return false;
		}

		SpacedItem that = (SpacedItem) o;

		return pk.equals(that.getPk());
	}

	@Override
	public int hashCode() {
		return pk.hashCode();
	}
}
