package se.spaced.server.model.entity;

import se.spaced.server.model.ServerEntity;

public interface EntityServiceListener {
	void entityAdded(ServerEntity entity);
	void entityRemoved(ServerEntity entity);
}
