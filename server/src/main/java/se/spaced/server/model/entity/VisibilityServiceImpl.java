package se.spaced.server.model.entity;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.SetMultimap;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.ardortech.math.SpacedVector3;
import se.spaced.server.model.ServerEntity;
import se.spaced.shared.activecache.Job;
import se.spaced.shared.util.ListenerDispatcher;

import java.util.Collection;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Singleton
public class VisibilityServiceImpl implements VisibilityService {
	private final Logger log = LoggerFactory.getLogger(getClass());

	private static final double OUT_OF_RANGE_SQ = 250 * 250;
	private static final double IN_RANGE_SQ = 230 * 230;

	private final Multimap<ServerEntity, ServerEntity> canSee = createMultimap();
	private final EntityService entityService;
	private final ListenerDispatcher<AppearanceService> appearanceListeners;
	private final Lock lock = new ReentrantLock();

	private static Multimap<ServerEntity, ServerEntity> createMultimap() {
		SetMultimap<ServerEntity, ServerEntity> multimap = HashMultimap.create();
		return multimap;
		//return Multimaps.synchronizedSetMultimap(multimap);
	}

	@Inject
	public VisibilityServiceImpl(EntityService entityService, ListenerDispatcher<AppearanceService> appearanceListeners) {
		this.entityService = entityService;
		this.appearanceListeners = appearanceListeners;
	}

	@Override
	public void updateEntityPosition(final ServerEntity entity) {
		internalUpdateEntityPosition(entity);
	}

	private void internalUpdateEntityPosition(ServerEntity entity) {
		SpacedVector3 myPos = entity.getPosition();
		lock.lock();
		try {
			Collection<ServerEntity> entityCollection = Lists.newArrayList(canSee.get(entity));
			for (ServerEntity other : entityCollection) {
				if (SpacedVector3.distanceSq(myPos, other.getPosition()) > OUT_OF_RANGE_SQ) {
					removeConnection(entity, other);
				}
			}

			Collection<ServerEntity> allEntities = entityService.getAllEntities(null);
			for (ServerEntity other : allEntities) {
				if (!entity.equals(other) && !entityCollection.contains(other)) {
					if (SpacedVector3.distanceSq(myPos, other.getPosition()) <= IN_RANGE_SQ) {
						addConnection(entity, other);
					}
				}
			}
		} finally {
			lock.unlock();
		}
	}

	@Override
	public void invokeForNearby(final ServerEntity entity, final Job<Collection<ServerEntity>> callback) {
		lock.lock();
		try {
			Collection<ServerEntity> entityCollection = canSee.get(entity);
			callback.run(entityCollection);
		} finally {
			lock.unlock();
		}
	}

	@Override
	public void forceConnect(ServerEntity viewer, ServerEntity view) {
		lock.lock();
		try {
			Collection<ServerEntity> viewList = canSee.get(viewer);
			if (viewList == null) {
				log.error("Expected " + viewer + " in VisibilityService", new RuntimeException());
				return;
			}
			if (!viewer.equals(view) && !viewList.contains(view)) {
				addConnection(viewer, view);
			}
		} finally {
			lock.unlock();
		}
	}

	private void addConnection(ServerEntity entity, ServerEntity other) {
		log.debug("Adding connection between {} and {}", entity, other);	
		entityCanSee(entity, other);
		entityCanSee(other, entity);
	}

	private void entityCanSee(ServerEntity entity, ServerEntity other) {
		boolean added = canSee.put(entity, other);
		if (added) {
			appearanceListeners.trigger().notifyAppeared(entity, other);
		} else {
			throw new RuntimeException(String.format("Entity could already see %s -> %s", entity.getName(), other.getName()));
		}
	}


	private void removeConnection(ServerEntity entity, ServerEntity other) {
		entityCantSee(entity, other);
		entityCantSee(other, entity);
	}

	private void entityCantSee(ServerEntity entity, ServerEntity other) {
		canSee.remove(entity, other);
		appearanceListeners.trigger().notifyDisappeared(entity, other);
	}

	@Override
	public void entityAdded(final ServerEntity entity) {
		internalUpdateEntityPosition(entity);
	}

	@Override
	public void entityRemoved(final ServerEntity removedEntity) {
		lock.lock();
		try {
			Collection<ServerEntity> entityCollection = canSee.get(removedEntity);
			if (entityCollection != null) {
				for (ServerEntity entity : entityCollection) {
					entityCantSee(entity, removedEntity);
				}
			}
			canSee.removeAll(removedEntity);
		} finally {
			lock.unlock();
		}
	}
}
