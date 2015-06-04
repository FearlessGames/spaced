package se.spaced.shared.partition;

import com.ardor3d.math.type.ReadOnlyVector3;

import java.util.Comparator;

public class HasPositionComparator implements Comparator<HasPosition> {
	private final ReadOnlyVector3 center;

	public HasPositionComparator(ReadOnlyVector3 center) {
		this.center = center;
	}

	@Override
	public int compare(HasPosition o1, HasPosition o2) {
		return (int) (center.distanceSquared(o1.getPosition()) - center.distanceSquared(o2.getPosition()));
	}
}
