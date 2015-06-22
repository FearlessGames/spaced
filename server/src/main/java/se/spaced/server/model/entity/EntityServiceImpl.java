package se.spaced.server.model.entity;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.apache.mina.util.ConcurrentHashSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.fearlessgames.common.util.uuid.UUID;
import se.fearlessgames.common.util.uuid.UUIDFactory;
import se.spaced.messages.protocol.s2c.S2CProtocol;
import se.spaced.server.model.ServerEntity;
import se.spaced.shared.util.BijectiveMap;
import se.spaced.shared.util.ListenerDispatcher;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Singleton
public class EntityServiceImpl implements EntityService {
	private final Logger log = LoggerFactory.getLogger(getClass());
	private final Map<UUID, ServerEntity> entities;
	private final BijectiveMap<ServerEntity, S2CProtocol> smrtReceivers;
	private final UUIDFactory uuidFactory;
	private final ListenerDispatcher<EntityServiceListener> serviceListener;
	private final Lock addRemoveLock = new ReentrantLock();

	@Inject
	public EntityServiceImpl(
			UUIDFactory uuidFactory, ListenerDispatcher<EntityServiceListener> listenerDispatcher) {
		this.uuidFactory = uuidFactory;
		this.serviceListener = listenerDispatcher;
		entities = new ConcurrentHashMap<UUID, ServerEntity>();
		smrtReceivers = new BijectiveMap<ServerEntity, S2CProtocol>();
	}

	@Override
	public void addEntity(ServerEntity entity, S2CProtocol smrtReceiver) {
		addRemoveLock.lock();
		try {
			addSmrtReceiver(entity, smrtReceiver);
			addToWorld(entity);
		} finally {
			addRemoveLock.unlock();
		}
	}


	private void addSmrtReceiver(ServerEntity entity, S2CProtocol smrtReceiver) {
		if (smrtReceiver == null) {
			log.warn("smrtReceiver is null for {}", entity);
		}
		smrtReceivers.connect(entity, smrtReceiver);
	}

	private void addToWorld(ServerEntity entity) {
		log.debug("Added entity: {} ", entity);
		entities.put(entity.getPk(), entity);
		serviceListener.trigger().entityAdded(entity);
	}

	@Override
	public void removeEntity(ServerEntity entity) {
		addRemoveLock.lock();
		try {
			entities.remove(entity.getPk());
			smrtReceivers.removeX(entity);
			serviceListener.triggerReversed().entityRemoved(entity);
		} finally {
			addRemoveLock.unlock();
		}
	}

	@Override
	public ServerEntity getEntity(UUID uuid) {
		return entities.get(uuid);
	}

	@Override
	public S2CProtocol getSmrtReceiver(ServerEntity entity) {
		return smrtReceivers.getY(entity);
	}

	@Override
	public boolean isLoggedIn(ServerEntity entity) {
		return entities.containsValue(entity);
	}

	@Override
	public Collection<ServerEntity> getAllEntities(ServerEntity excluded) {
		if (excluded == null) {
			return Collections.unmodifiableCollection(entities.values());
		} else {
			Collection<ServerEntity> values = new ConcurrentHashSet<ServerEntity>(entities.values());
			values.remove(excluded);
			return values;
		}
	}

	@Override
	public ServerEntity findEntityByName(String name) {
		for (ServerEntity entity : entities.values()) {
			if (name.equals(entity.getName())) {
				return entity;
			}
		}
		return null;
	}

	@Override
	public UUID getNewEntityId() {
		return uuidFactory.randomUUID();
	}
}
