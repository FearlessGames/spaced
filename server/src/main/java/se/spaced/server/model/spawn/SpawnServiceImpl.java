package se.spaced.server.model.spawn;

import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.fearless.common.time.TimeProvider;
import se.spaced.messages.protocol.Entity;
import se.spaced.messages.protocol.s2c.S2CEmptyReceiver;
import se.spaced.messages.protocol.s2c.S2CProtocol;
import se.spaced.server.mob.brains.MobBrain;
import se.spaced.server.model.Mob;
import se.spaced.server.model.ServerEntity;
import se.spaced.server.net.broadcast.SmrtBroadcaster;
import se.spaced.server.persistence.util.transactions.AutoTransaction;
import se.spaced.shared.util.ListenerDispatcher;

import java.util.Map;

@Singleton
public class SpawnServiceImpl extends S2CEmptyReceiver implements SpawnService {
	private final Logger log = LoggerFactory.getLogger(SpawnServiceImpl.class);

	private final Map<ServerEntity, MobLifecycle> entityOwners;

	private final TimeProvider timeProvider;
	private final ListenerDispatcher<SpawnListener> listenerDispatcher;

	@Inject
	public SpawnServiceImpl(
			SmrtBroadcaster<S2CProtocol> smrtBroadcaster,
			TimeProvider timeProvider,
			ListenerDispatcher<SpawnListener> listenerDispatcher) {
		this.timeProvider = timeProvider;
		this.listenerDispatcher = listenerDispatcher;
		smrtBroadcaster.addSpy(this);
		entityOwners = Maps.newHashMap();
	}

	@Override
	public MobLifecycle getOwner(ServerEntity mob) {
		return entityOwners.get(mob);
	}

	@Override
	@AutoTransaction
	public void registerMob(MobLifecycle owner, Mob entity, MobBrain brain) {
		MobLifecycle oldOwner = entityOwners.remove(entity);
		if (oldOwner == null) {
			entityOwners.put(entity, owner);
		} else {
			log.error(entity + " was created twice, first by " + oldOwner + " and then by " + owner);
		}
		listenerDispatcher.trigger().entitySpawned(entity, brain);
	}

	@Override
	@AutoTransaction
	public void unregisterMob(MobLifecycle owner, Mob entity) {
		MobLifecycle oldOwner = entityOwners.remove(entity);
		if (oldOwner == null) {
			log.error(entity + " had no owner");
		} else if (!oldOwner.equals(owner)) {
			log.error(entity + " was owned by " + oldOwner + " but removed by " + owner);
		}
		listenerDispatcher.trigger().entityDespawned(entity);
	}

	@Override
	public S2CProtocol getReceiver() {
		return this;
	}

	@Override
	public void entityWasKilled(Entity attacker, Entity target) {
		ServerEntity victim = (ServerEntity) target;
		MobLifecycle owner = entityOwners.get(victim);

		if (owner != null) {
			Mob mobVictim = (Mob) victim;
			owner.notifyMobDeath(timeProvider.now(), mobVictim);
		}
	}
}

