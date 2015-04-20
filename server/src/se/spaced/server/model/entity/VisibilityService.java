package se.spaced.server.model.entity;

import se.spaced.server.model.ServerEntity;
import se.spaced.shared.activecache.Job;

import java.util.Collection;

public interface VisibilityService extends EntityServiceListener {
	void updateEntityPosition(ServerEntity entity);

	void invokeForNearby(ServerEntity entity, Job<Collection<ServerEntity>> callback);

	void forceConnect(ServerEntity viewer, ServerEntity view);
}
