package se.spaced.shared.partition;

import com.ardor3d.math.type.ReadOnlyVector3;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class PartitionedGeoMap<T extends HasPosition> implements GeoMap<T> {
	// TODO: add real concurrent sets
	private final ConcurrentHashMap<ZoneKey, Zone<T>> data = new ConcurrentHashMap<ZoneKey, Zone<T>>();
	private final ConcurrentHashMap<T, Zone<T>> allObjects = new ConcurrentHashMap<T, Zone<T>>();
	private final double cubeLength;

	public PartitionedGeoMap(double cubeLength) {
		this.cubeLength = cubeLength;
	}

	private int getIndex(double val) {
		// TODO: what if val is negative?
		return (int) (val / cubeLength);
	}

	private ZoneKey getZoneKey(int x, int y, int z) {
		return new ZoneKey(x, y, z);
	}

	private ZoneKey getZoneKey(ReadOnlyVector3 position) {
		int x = getIndex(position.getX());
		int y = getIndex(position.getY());
		int z = getIndex(position.getZ());
		return getZoneKey(x, y, z);
	}

	private Zone<T> getZone(int x, int y, int z) {
		ZoneKey key = getZoneKey(x, y, z);
		return getZone(key);
	}

	private Zone<T> getZone(ReadOnlyVector3 position) {
		ZoneKey zone = getZoneKey(position);
		return getZone(zone);
	}

	private Zone<T> getZone(ZoneKey key) {
		Zone<T> newZone = new Zone<T>();
		Zone<T> oldZone = data.putIfAbsent(key, newZone);
		if (oldZone == null) {
			return newZone;
		}
		return oldZone;
	}

	@Override
	public void add(T obj) {
		synchronized (obj) {
			remove(obj);
			ReadOnlyVector3 position = obj.getPosition();
			Zone zone = getZone(position);
			zone.objects.put(obj, Boolean.TRUE);
			allObjects.put(obj, zone);
		}
	}

	@Override
	public void remove(T obj) {
		synchronized (obj) {
			Zone zone = allObjects.remove(obj);
			if (zone != null) {
				zone.objects.remove(obj);
			}
		}
	}

	@Override
	public void update(T obj) {
		synchronized (obj) {
			Zone oldZone = allObjects.get(obj);
			Zone actualZone = getZone(getZoneKey(obj.getPosition()));
			if (oldZone != actualZone) {
				remove(obj);
				add(obj);
			}
		}
	}

	@Override
	public boolean contains(T obj) {
		return allObjects.containsKey(obj);
	}

	@Override
	public List<T> findNear(ReadOnlyVector3 position, double maxDistance, boolean sorted) {
		ArrayList<T> result = new ArrayList<T>();
		ZoneKey key = getZoneKey(position);

		// TODO: use actual position to clip off more indices
		int width = (int) (1 + Math.ceil(maxDistance / cubeLength));
		for (int x = -width; x <= width; x++) {
			for (int y = -width; y <= width; y++) {
				for (int z = -width; z <= width; z++) {
					ZoneKey key2 = getZoneKey(key.getX() + x, key.getY() + y, key.getZ() + z);
					Zone<T> zone = getZone(key2);
					Set<T> objects = zone.objects.keySet();
					for (T object : objects) {
						if (object.getPosition().distance(position) <= maxDistance) {
							result.add(object);
						}
					}
				}
			}
		}

		if (sorted) {
			Collections.sort(result, new HasPositionComparator(position));
		}
		return result;
	}

	@Override
	public T findNearest(ReadOnlyVector3 position, double maxDistance) {
		ZoneKey key = getZoneKey(position);
		Zone<T> zone = getZone(key);

		T best = null;
		double bestDist = Double.MAX_VALUE;
		for (T object : zone.objects.keySet()) {
			double dist = object.getPosition().distance(position);
			if (dist <= maxDistance && dist <= bestDist) {
				best = object;
				bestDist = dist;
			}
		}

		// TODO: scan surrounding zones until they are further away than bestDist
		return best;
	}

	@Override
	public void clear() {
		this.allObjects.clear();
		this.data.clear();
	}
}
