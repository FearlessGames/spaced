package se.spaced.shared.partition;

import com.ardor3d.math.type.ReadOnlyVector3;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class DumbGeoMap<T extends HasPosition> implements GeoMap<T> {

	private final Collection<T> container;

	public DumbGeoMap(Collection<T> container) {
		this.container = container;
	}

	@Override
	public void add(T obj) {
		container.add(obj);
	}

	@Override
	public void remove(T obj) {
		container.remove(obj);
	}

	@Override
	public void update(T obj) {
	}

	@Override
	public boolean contains(T obj) {
		return container.contains(obj);
	}

	@Override
	public List<T> findNear(ReadOnlyVector3 position, double maxDistance, boolean sorted) {
		ArrayList<T> result = new ArrayList<T>();
		for (T allObject : container) {
			if (allObject.getPosition().distance(position) <= maxDistance) {
				result.add(allObject);
			}
		}
		if (sorted) {
			Collections.sort(result, new HasPositionComparator(position));
		}
		return result;
	}

	@Override
	public T findNearest(ReadOnlyVector3 position, double maxDistance) {
		T best = null;
		double bestDist = Double.MAX_VALUE;
		for (T allObject : container) {
			double dist = allObject.getPosition().distance(position);
			if (dist <= maxDistance && dist < bestDist) {
				best = allObject;
				bestDist = dist;
			}
		}
		return best;
	}

	@Override
	public void clear() {
		container.clear();
	}
}
