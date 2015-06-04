package se.spaced.messages.protocol;

import com.google.common.collect.ImmutableMultimap;
import se.fearless.common.uuid.UUID;

public interface InventoryData {
	public UUID getPk();

	ImmutableMultimap<Integer, ? extends SpacedItem> getItemMap();
}
