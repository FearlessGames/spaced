package se.spaced.server.model.spawn;

import se.spaced.server.mob.brains.MobBrain;
import se.spaced.server.model.Mob;

public interface SpawnListener {
	void entitySpawned(Mob mob, MobBrain brain);

	void entityDespawned(Mob mob);
}
