package se.spaced.server.model.spawn;

import se.spaced.server.model.Mob;

public interface MobLifecycle {
	void removeEntity(Mob victim);

	void notifyMobDeath(long now, Mob victim);

	Mob doSpawn();
}
