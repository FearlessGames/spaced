package se.spaced.shared.world.area;

import se.ardortech.math.SpacedRotation;
import se.ardortech.math.SpacedVector3;

public class SinglePoint implements Geometry {
	private final SpacedVector3 point;
	private final SpacedRotation rotation;

	public SinglePoint(SpacedVector3 point, SpacedRotation rotation) {
		this.point = point;
		this.rotation = rotation;
	}

	public SpacedVector3 getPoint() {
		return point;
	}

	public SpacedRotation getRotation() {
		return rotation;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		SinglePoint that = (SinglePoint) o;

		if (point != null ? !point.equals(that.point) : that.point != null) {
			return false;
		}
		if (rotation != null ? !rotation.equals(that.rotation) : that.rotation != null) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		int result = point != null ? point.hashCode() : 0;
		result = 31 * result + (rotation != null ? rotation.hashCode() : 0);
		return result;
	}
}
