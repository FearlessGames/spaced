package se.spaced.server.model.combat;

import se.spaced.server.model.ServerEntity;
import se.spaced.server.model.entity.EntityServiceListener;

public interface EntityCombatService extends EntityServiceListener {
	boolean isInCombat(ServerEntity entity);

	void respawnWithHealth(ServerEntity entity, int health);

	Combat enterCombat(ServerEntity performer, ServerEntity target, long now, boolean entitiesAreHostile);

	int numberOfCombat();

	void removeFromCombat(ServerEntity entity);
}
