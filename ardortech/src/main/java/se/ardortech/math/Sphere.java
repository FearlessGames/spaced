package se.ardortech.math;

import com.ardor3d.math.Vector3;
import com.ardor3d.scenegraph.Spatial;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

//
@XStreamAlias("Sphere")
public class Sphere implements Shape3D {
	@XStreamAsAttribute
	private final SpacedVector3 center;
	@XStreamAsAttribute
	private final double radius;

	public Sphere(SpacedVector3 center, double radius) {
		this.center = center;
		this.radius = radius;
	}

	@Override
	public boolean isInside(SpacedVector3 point) {
		return SpacedVector3.distance(center, point) < radius;
	}

	@Override
	public boolean isInside(SpacedVector3 point, double margin) {
		return SpacedVector3.distance(center, point) < radius - margin;
	}

	@Override
	public Spatial getDebugShape() {
		return new com.ardor3d.scenegraph.shape.Sphere("", new Vector3(center.getX(), center.getY(), center.getZ()), 10, 10, radius);
	}

	@Override
	public double distanceToEdge(SpacedVector3 point) {
		double distanceToCenter = SpacedVector3.distance(center, point);
		return distanceToCenter - radius;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		Sphere sphere = (Sphere) o;

		if (Double.compare(sphere.radius, radius) != 0) {
			return false;
		}
		return center.equals(sphere.center);
	}

	@Override
	public int hashCode() {
		int result;
		long temp;
		result = center.hashCode();
		temp = radius != +0.0d ? Double.doubleToLongBits(radius) : 0L;
		result = 31 * result + (int) (temp ^ (temp >>> 32));
		return result;
	}
}