package se.spaced.server.model.spawn.area;

import java.util.Comparator;

class SpawnAreaComparator implements Comparator<SpawnArea> {
	@Override
	public int compare(SpawnArea o1, SpawnArea o2) {
		return o1.getSpawnCount() - o2.getSpawnCount();
	}
}
