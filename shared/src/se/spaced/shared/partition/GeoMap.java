package se.spaced.shared.partition;

import com.ardor3d.math.type.ReadOnlyVector3;

import java.util.List;

public interface GeoMap<T extends HasPosition> {
	void add(T obj);
	void remove(T obj);
	void update(T obj);
	boolean contains(T obj);

	List<T> findNear(ReadOnlyVector3 position, double maxDistance, boolean sorted);
	T findNearest(ReadOnlyVector3 position, double maxDistance);

	void clear();
}
