package se.ardortech.math;

import com.ardor3d.math.MathUtils;
import com.ardor3d.math.ValidatingTransform;
import com.ardor3d.math.Vector3;
import com.ardor3d.math.type.ReadOnlyVector3;

public class VectorMath {
	private VectorMath() {
	}

	public static SpacedVector3 getDirection(SpacedVector3 currentPosition, SpacedVector3 target) {
		return target.subtract(currentPosition).normalize();
	}


	public static double moveTowardsLocal(
			Vector3 currentPosition, ReadOnlyVector3 target, double speed, double timeStep) {
		if (speed <= 0) {
			return 0;
		}

		double distance = currentPosition.distance(target);
		if (distance <= 0) {
			return timeStep;
		}
		double wantedDistance = speed * timeStep;
		double distanceLeft = wantedDistance - distance;
		if (distanceLeft > 0) {
			wantedDistance = distance;
		} else {
			distanceLeft = 0;
		}
		currentPosition.lerpLocal(target, wantedDistance / distance);
		return distanceLeft / speed;
	}

	public static SpacedVector3 moveTowards(SpacedVector3 currentPosition, SpacedVector3 target, double speed, double timeStep) {
		if (speed <= 0) {
			return currentPosition;
		}

		double distance = SpacedVector3.distance(currentPosition, target);
		if (distance <= 0) {
			return target;
		}
		double wantedDistance = Math.min(speed * timeStep, distance);
		return lerp(currentPosition, target, wantedDistance / distance);
	}


	public static SpacedVector3 lerp(SpacedVector3 v0, SpacedVector3 v1, double scalar) {
		double x = (1.0 - scalar) * v0.getX() + scalar * v1.getX();
		double y = (1.0 - scalar) * v0.getY() + scalar * v1.getY();
		double z = (1.0 - scalar) * v0.getZ() + scalar * v1.getZ();
		return new SpacedVector3(x, y, z);
	}

	public static SpacedVector3 getNormal(SpacedVector3 p0, SpacedVector3 p1, SpacedVector3 p2) {
		return SpacedVector3.crossProduct(p2.subtract(p0), p1.subtract(p0)).normalize();
	}


	public static SpacedRotation slerp(SpacedRotation from, SpacedRotation to, double amount) {
		// check for weighting at either extreme
		if (amount == 0.0) {
			return from;
		} else if (Double.compare(amount, 1.0) == 0) {
			return to;
		}

		// Check for equality and skip operation.
		if (from.equals(to)) {
			return from;
		}

		double dotP = SpacedRotation.dot(from, to);

		if (dotP < 0.0) {
			// Negate the second quaternion and the result of the dot product
			to = to.scalarMultiply(-1.0);
			dotP = -dotP;
		}

		// Set the first and second scale for the interpolation
		double scale0 = 1 - amount;
		double scale1 = amount;

		// Check if the angle between the 2 quaternions was big enough to
		// warrant such calculations
		if ((1 - dotP) > 0.1) {// Get the angle between the 2 quaternions,
			// and then store the sin() of that angle
			final double theta = Math.acos(dotP);
			final double invSinTheta = 1f / MathUtils.sin(theta);

			// Calculate the scale for q1 and q2, according to the angle and
			// it's sine value
			scale0 = MathUtils.sin((scale0) * theta) * invSinTheta;
			scale1 = MathUtils.sin((scale1 * theta)) * invSinTheta;
		}

		// Calculate the x, y, z and w values for the quaternion by using a
		// special form of linear interpolation for quaternions.
		final double x = (scale0 * from.getX()) + (scale1 * to.getX());
		final double y = (scale0 * from.getY()) + (scale1 * to.getY());
		final double z = (scale0 * from.getZ()) + (scale1 * to.getZ());
		final double w = (scale0 * from.getW()) + (scale1 * to.getW());

		// Return the interpolated quaternion
		return new SpacedRotation(x, y, z, w);
	}

	/**
	 * Returns a rotation equal the rotation required to point the z-axis at 'direction' and the y-axis to
	 * 'up'.
	 *
	 * @param direction where to 'look' at
	 * @param up		  a vector indicating the local up direction.
	 */
	public static SpacedRotation lookAt(SpacedVector3 direction, SpacedVector3 up) {

		SpacedVector3 zAxis = direction.normalize();
		SpacedVector3 xAxis = SpacedVector3.crossProduct(up.normalize(), zAxis);
		SpacedVector3 yAxis = SpacedVector3.crossProduct(zAxis, xAxis);

		SpacedRotation rotation = SpacedRotation.fromAxes(xAxis, yAxis, zAxis);

		ValidatingTransform transform = new ValidatingTransform();
		try {
			transform.setRotation(Rotations.fromSpaced(rotation));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rotation;

	}


	/**
	 * Calculates the orthogonal projection of a vector onto a plane
	 * @param vector the vector to project on the plane
	 * @param planePoint1 point 1 in the plane
	 * @param planePoint2 point 2 in the plane
	 * @param planePoint3 point 3 in the plane
	 * @return the orthogonal projection of the vector onto the plane defined by the three points
	 */
	public static SpacedVector3 projectOntoPlane(
			SpacedVector3 vector,
			SpacedVector3 planePoint1,
			SpacedVector3 planePoint2,
			SpacedVector3 planePoint3) {
		SpacedVector3 normal = getNormal(planePoint1, planePoint2, planePoint3);
		return vector.subtract(normal.scalarMultiply(SpacedVector3.dotProduct(normal, vector)));
	}

	public static SpacedVector3 findIntersection(SpacedVector3 p1, SpacedVector3 line1Point2, SpacedVector3 p2, SpacedVector3 line2Point2) {
		if (SpacedVector3.distanceSq(p1, p2) < 0.1) {
			return p1;
		}
		SpacedVector3 v1 = p1.subtract(line1Point2);
		SpacedVector3 v2 = p2.subtract(line2Point2);

		double a = -SpacedVector3.crossProduct(p2.subtract(p1), v2).length() / SpacedVector3.crossProduct(v1,
				v2).length();
		return p1.add(v1.scalarMultiply(a));
	}

	public static InViewPoint standingIn(final SpacedVector3 viewPoint) {
		return new InViewPoint() {
			@Override
			public SpatialQuery is(final SpacedVector3 point) {
				return new SpatialQuery() {
					@Override
					public boolean leftOf(SpacedVector3 reference) {
						// This is the y part of the cross product of the vectors going from
						// viewPoint to reference and viewPoint to point
						double v1z = viewPoint.getZ() - reference.getZ();
						double v1x = viewPoint.getX() - reference.getX();
						double v2x = viewPoint.getX() - point.getX();
						double v2z = viewPoint.getZ() - point.getZ();
						return Math.signum(v1z * v2x - v1x * v2z) > 0;
					}
				};
			}

			@Override
			public SpacedVector3 getLeftMost(SpacedVector3 point1, SpacedVector3 point2) {
				return is(point1).leftOf(point2) ? point1 : point2;
			}
		};
	}

	public interface InViewPoint {
		SpatialQuery is(SpacedVector3 point);
		SpacedVector3 getLeftMost(SpacedVector3 point1, SpacedVector3 point2);
	}

	public interface SpatialQuery {
		boolean leftOf(SpacedVector3 reference);
	}
}
