package se.spaced.client.view.entity;

import se.fearlessgames.common.util.uuid.UUID;

public interface EntityViewListener {
	void entityLeftClicked(UUID entityUuid);

	void entityRightClicked(UUID entityUuid);

	void nothingLeftClicked();

	void nothingRightClicked();

	void entityHovered(UUID entityUuid);

	void hoverReset();
}
