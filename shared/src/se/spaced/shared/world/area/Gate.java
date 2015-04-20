package se.spaced.shared.world.area;

import com.google.common.collect.ImmutableList;
import se.ardortech.math.SpacedRotation;
import se.ardortech.math.SpacedVector3;
import se.spaced.shared.world.AreaPoint;

public class Gate implements PointSequence {

	private final SpacedVector3 point1;
	private final SpacedVector3 point2;

	public Gate(SpacedVector3 point1, SpacedVector3 point2) {
		this.point1 = point1;
		this.point2 = point2;
	}

	public SpacedVector3 getPoint1() {
		return point1;
	}

	public SpacedVector3 getPoint2() {
		return point2;
	}

	public SpacedVector3 getMidPoint() {
		return point1.add(point2).scalarMultiply(0.5);
	}

	@Override
	public ImmutableList<AreaPoint> getAreaPoints() {
		return ImmutableList.of(new AreaPoint(point1, SpacedRotation.IDENTITY), new AreaPoint(point2, SpacedRotation.IDENTITY));
	}
}
