package se.spaced.client.model.item;

import se.fearless.common.uuid.UUID;
import se.spaced.messages.protocol.ItemTemplate;

public class ItemTemplateProxy implements ItemTemplate {
	private final UUID pk;

	public ItemTemplateProxy(UUID uuid) {
		pk = uuid;
	}

	@Override
	public UUID getPk() {
		return pk;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof ItemTemplate)) {
			return false;
		}

		ItemTemplate that = (ItemTemplate) o;

		if (!pk.equals(that.getPk())) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		return pk.hashCode();
	}
}
