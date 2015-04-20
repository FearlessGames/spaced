package se.spaced.messages.protocol;

import com.google.common.collect.ImmutableMultimap;
import se.fearlessgames.common.util.uuid.UUID;

public interface InventoryData {
	public UUID getPk();

	ImmutableMultimap<Integer, ? extends SpacedItem> getItemMap();
}
