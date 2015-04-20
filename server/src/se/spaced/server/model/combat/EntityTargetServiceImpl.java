package se.spaced.server.model.combat;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import se.spaced.server.model.ServerEntity;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
public class EntityTargetServiceImpl implements EntityTargetService {

	private final TargetUpdateListener listener;
	private final Map<ServerEntity, ServerEntity> targeting = new ConcurrentHashMap<ServerEntity, ServerEntity>();
	private final SetMultimap<ServerEntity, ServerEntity> entitiesTargeting =
			Multimaps.synchronizedSetMultimap(HashMultimap.<ServerEntity, ServerEntity>create());


	@Inject
	public EntityTargetServiceImpl(TargetUpdateListener listener) {
		this.listener = listener;
	}

	@Override
	public void setTarget(ServerEntity targetingEntity, ServerEntity newTarget) {
		if (newTarget.equals(targeting.get(targetingEntity))) {
			return;
		}
		ServerEntity oldTarget = targeting.remove(targetingEntity);
		if (oldTarget != null) {
			synchronized (entitiesTargeting) {
				entitiesTargeting.remove(oldTarget, targetingEntity);
			}
		}
		targeting.put(targetingEntity, newTarget);
		entitiesTargeting.put(newTarget, targetingEntity);
		if (!newTarget.equals(oldTarget)) {
			listener.targetChanged(targetingEntity, oldTarget, newTarget);
		}
	}


	@Override
	public void clearTarget(ServerEntity targetingEntity) {
		ServerEntity oldTarget = targeting.remove(targetingEntity);
		if (oldTarget != null) {
			entitiesTargeting.remove(oldTarget, targetingEntity);
			listener.targetCleared(targetingEntity, oldTarget);
		}
	}

	@Override
	public Set<ServerEntity> getEntitiesTargeting(ServerEntity target) {
		return Sets.newHashSet(entitiesTargeting.get(target));
	}

	@Override
	public ServerEntity getCurrentTarget(ServerEntity performer) {
		return targeting.get(performer);
	}

	@Override
	public void entityAdded(ServerEntity entity) {
	}

	@Override
	public void entityRemoved(ServerEntity entity) {
		clearTarget(entity);
		synchronized (entitiesTargeting) {
			for (ServerEntity serverEntity : getEntitiesTargeting(entity)) {
				clearTarget(serverEntity);
			}
		}
	}
}
