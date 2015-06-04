package se.spaced.messages.protocol;

import se.fearless.common.uuid.UUID;

public interface SpacedItem {
	UUID getPk();
	ItemTemplate getItemTemplate();
}
