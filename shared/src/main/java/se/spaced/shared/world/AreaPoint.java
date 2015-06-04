package se.spaced.shared.world;

import se.ardortech.math.SpacedRotation;
import se.ardortech.math.SpacedVector3;

public class AreaPoint {
	private final SpacedVector3 point;
	private final SpacedRotation rotation;

	public AreaPoint(SpacedVector3 point, SpacedRotation rotation) {
		this.point = point;
		this.rotation = rotation;
	}

	@Override
	public String toString() {
		return new StringBuilder(point.toString()).append(";").append(rotation).toString();
	}

	public SpacedVector3 getPoint() {
		return point;
	}

	public SpacedRotation getRotation() {
		return rotation;
	}
}
