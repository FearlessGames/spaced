package se.spaced.messages.protocol;

import se.fearlessgames.common.util.uuid.UUID;

public interface SpacedItem {
	UUID getPk();
	ItemTemplate getItemTemplate();
}
