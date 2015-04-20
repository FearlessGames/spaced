package se.spaced.server.model.entity;

import se.fearlessgames.common.util.uuid.UUID;
import se.spaced.messages.protocol.s2c.S2CProtocol;
import se.spaced.server.model.ServerEntity;

import java.util.Collection;

public interface EntityService {
	ServerEntity getEntity(UUID uuid);

	/**
	 * Wrapper around addSmrtReceiver and addToWorld
	 * @param entity
	 * @param smrtReceiver
	 */
	void addEntity(ServerEntity entity, S2CProtocol smrtReceiver);

	void removeEntity(ServerEntity entity);

	Collection<ServerEntity> getAllEntities(ServerEntity excluded);

	UUID getNewEntityId();

	ServerEntity findEntityByName(String name);

	S2CProtocol getSmrtReceiver(ServerEntity entity);

	boolean isLoggedIn(ServerEntity entity);
}
