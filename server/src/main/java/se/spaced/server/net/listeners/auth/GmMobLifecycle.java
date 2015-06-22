package se.spaced.server.net.listeners.auth;

import com.google.inject.Inject;
import se.spaced.messages.protocol.s2c.S2CProtocol;
import se.spaced.server.model.Mob;
import se.spaced.server.model.entity.EntityService;
import se.spaced.server.model.spawn.MobLifecycle;
import se.spaced.server.model.spawn.SpawnService;
import se.spaced.server.net.broadcast.SmrtBroadcaster;

public class GmMobLifecycle implements MobLifecycle {
	private final SmrtBroadcaster<S2CProtocol> smrtBroadcaster;
	private final SpawnService spawnService;
	private final EntityService entityService;

	@Inject
	public GmMobLifecycle(
			SmrtBroadcaster<S2CProtocol> smrtBroadcaster, SpawnService spawnService, EntityService entityService) {
		this.smrtBroadcaster = smrtBroadcaster;
		this.spawnService = spawnService;
		this.entityService = entityService;
	}

	@Override
	public void removeEntity(Mob victim) {
		smrtBroadcaster.create().toAll().send().entity().entityDespawned(victim);
		spawnService.unregisterMob(this, victim);
		entityService.removeEntity(victim);
	}

	@Override
	public void notifyMobDeath(long now, Mob victim) {
		removeEntity(victim);
	}

	@Override
	public Mob doSpawn() {
		return null;
	}
}
