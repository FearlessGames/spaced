package se.spaced.server.model.spawn.area;

import se.spaced.server.persistence.dao.interfaces.Persistable;

public interface SpawnArea extends Persistable {

	/**
	 * Gets the next spawn point from this area
	 * The spawn point is made up of a position and a rotation
	 *
	 * @return the next spawn point
	 */
	SpawnPoint getNextSpawnPoint();

	void addSpawn();

	void removeSpawn();

	int getSpawnCount();
}
