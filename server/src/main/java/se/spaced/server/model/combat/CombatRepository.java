package se.spaced.server.model.combat;

import se.spaced.server.model.ServerEntity;

public interface CombatRepository {
	Combat getCombat(ServerEntity entity);
	int numberOfCombat();
	void add(ServerEntity entity, Combat combat);
	void remove(ServerEntity entity);

	Iterable<Combat> getAllCombat();
}
