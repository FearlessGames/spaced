package se.spaced.shared.world.area;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import se.ardortech.math.SpacedVector3;

import java.util.Iterator;
import java.util.List;

public class Path implements Iterable<SpacedVector3>, Geometry {
	private final List<SpacedVector3> pathPoints;

	public Path() {
		pathPoints = Lists.newArrayList();
	}

	public Path(Iterable<SpacedVector3> pathPoints) {
		Preconditions.checkNotNull(pathPoints, "Pathpoints must not be null");
		this.pathPoints = Lists.newArrayList(pathPoints);
	}

	public boolean add(SpacedVector3 spacedVector3) {
		return pathPoints.add(spacedVector3);
	}

	public boolean isEmpty() {
		return pathPoints.isEmpty();
	}

	@Override
	public Iterator<SpacedVector3> iterator() {
		return pathPoints.iterator();
	}

	public List<SpacedVector3> getPathPoints() {
		return pathPoints;
	}
}
