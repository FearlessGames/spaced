package se.spaced.shared.model.items;

import se.spaced.shared.model.xmo.AttachmentPointIdentifier;

/**
 * A container is anything that can hold an item, so your chest can be seen as a container of size 1
 * that only can hold chest items.
 *
 * You can also choose to view these as the slots on a paper doll model. 
 */
public enum ContainerType {
	MAIN_HAND(AttachmentPointIdentifier.RIGHT_HAND),
	OFF_HAND(AttachmentPointIdentifier.LEFT_HAND),
	HEAD(AttachmentPointIdentifier.HEAD),
	CHEST(AttachmentPointIdentifier.SKIN_CHEST),
	LEGS(AttachmentPointIdentifier.SKIN_LEGS),
	FEET(AttachmentPointIdentifier.SKIN_FEET),
	BACK(AttachmentPointIdentifier.BACK),
	HAND(AttachmentPointIdentifier.BACK),
	UTILITY(AttachmentPointIdentifier.VEHICLE),
	BAG_SLOT(AttachmentPointIdentifier.BACK),
	LEFT_WRIST(AttachmentPointIdentifier.LEFT_WRIST),
	RIGHT_WRIST(AttachmentPointIdentifier.RIGHT_WRIST);

	private final AttachmentPointIdentifier attachmentPoint;

	ContainerType(AttachmentPointIdentifier attachmentPoint) {
		this.attachmentPoint = attachmentPoint;
	}

	public AttachmentPointIdentifier getAttachmentPoint() {
		return attachmentPoint;
	}
}
