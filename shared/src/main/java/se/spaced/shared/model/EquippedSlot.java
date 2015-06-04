package se.spaced.shared.model;

import se.fearless.common.uuid.UUID;
import se.spaced.shared.model.items.ContainerType;

public class EquippedSlot {
	private UUID itemId;
	private UUID templateId;
	private ContainerType slot;

	public EquippedSlot(UUID itemId, UUID templateId, ContainerType slot) {
		this.itemId = itemId;
		this.slot = slot;
		this.templateId = templateId;
	}

	public UUID getItemId() {
		return itemId;
	}

	public ContainerType getSlot() {
		return slot;
	}

	public UUID getTemplateId() {
		return templateId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((itemId == null) ? 0 : itemId.hashCode());
		result = prime * result + ((templateId == null) ? 0 : templateId.hashCode());
		result = prime * result + ((slot == null) ? 0 : slot.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj == null) {
			return false;
		}

		if (!getClass().isInstance(obj)) {
			return false;
		}

		EquippedSlot equippedSlot = (EquippedSlot) obj;

		if (!itemId.equals(equippedSlot.itemId)) {
			return false;
		}

		if (!templateId.equals(equippedSlot.templateId)) {
			return false;
		}

		if (!slot.equals(equippedSlot.slot)) {
			return false;
		}

		return true;

	}
}
