package se.spaced.client.view.entity;

import se.fearlessgames.common.util.uuid.UUID;
import se.spaced.client.core.states.Updatable;
import se.spaced.client.model.ClientEntity;
import se.spaced.client.model.Relation;
import se.spaced.client.model.player.TargetInfo;

public interface EntityView extends Updatable {
	void addEntity(ClientEntity entity, Relation relation);

	VisualEntity removeEntity(UUID uuid);

	VisualEntity getEntity(UUID uuid);

	void setTargetedEntity(TargetInfo targetInfo);

	void clearTargetedEntity();

	void setEntityAlive(UUID uuid, boolean alive);

	void setHoveredEntity(TargetInfo uuid);

	void clearHoveredEntity();

	void reset();
}
