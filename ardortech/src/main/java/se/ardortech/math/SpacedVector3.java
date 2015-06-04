package se.ardortech.math;

import com.ardor3d.math.Vector3;
import com.ardor3d.math.type.ReadOnlyVector3;
import org.apache.commons.math.MathRuntimeException;
import org.apache.commons.math.util.MathUtils;
import se.krka.kahlua.integration.annotations.LuaMethod;

import java.io.Serializable;

/**
 * This class implements vectors in a three-dimensional space.
 * <p>Instance of this class are guaranteed to be immutable.</p>
 */
public class SpacedVector3 implements ReadOnlyVector3, Serializable {

	private static final long serialVersionUID = 1883907360611470345L;

	/**
	 * Null vector (coordinates: 0, 0, 0).
	 */
	public static final SpacedVector3 ZERO = new SpacedVector3(0, 0, 0);

	/**
	 * First canonical vector (coordinates: 1, 0, 0).
	 */
	public static final SpacedVector3 PLUS_I = new SpacedVector3(1, 0, 0);

	/**
	 * Opposite of the first canonical vector (coordinates: -1, 0, 0).
	 */
	public static final SpacedVector3 MINUS_I = new SpacedVector3(-1, 0, 0);

	/**
	 * Second canonical vector (coordinates: 0, 1, 0).
	 */
	public static final SpacedVector3 PLUS_J = new SpacedVector3(0, 1, 0);

	/**
	 * Opposite of the second canonical vector (coordinates: 0, -1, 0).
	 */
	public static final SpacedVector3 MINUS_J = new SpacedVector3(0, -1, 0);

	/**
	 * Third canonical vector (coordinates: 0, 0, 1).
	 */
	public static final SpacedVector3 PLUS_K = new SpacedVector3(0, 0, 1);

	/**
	 * Opposite of the third canonical vector (coordinates: 0, 0, -1).
	 */
	public static final SpacedVector3 MINUS_K = new SpacedVector3(0, 0, -1);

	// CHECKSTYLE: stop ConstantName
	/**
	 * A vector with all coordinates set to NaN.
	 */
	public static final SpacedVector3 NaN = new SpacedVector3(Double.NaN, Double.NaN, Double.NaN);
	// CHECKSTYLE: resume ConstantName

	/**
	 * A vector with all coordinates set to positive infinity.
	 */
	public static final SpacedVector3 POSITIVE_INFINITY =
			new SpacedVector3(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);

	/**
	 * A vector with all coordinates set to negative infinity.
	 */
	public static final SpacedVector3 NEGATIVE_INFINITY =
			new SpacedVector3(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY);

	/**
	 * Abscissa.
	 */
	private final double x;

	/**
	 * Ordinate.
	 */
	private final double y;

	/**
	 * Height.
	 */
	private final double z;

	/**
	 * Simple constructor.
	 * Build a vector from its coordinates
	 *
	 * @param x abscissa
	 * @param y ordinate
	 * @param z height
	 * @see #getX()
	 * @see #getY()
	 * @see #getZ()
	 */
	public SpacedVector3(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	/**
	 * Simple constructor.
	 * Build a vector from its azimuthal coordinates
	 *
	 * @param alpha azimuth (&alpha;) around Z
	 *              (0 is +X, &pi;/2 is +Y, &pi; is -X and 3&pi;/2 is -Y)
	 * @param delta elevation (&delta;) above (XY) plane, from -&pi;/2 to +&pi;/2
	 * @see #getAlpha()
	 * @see #getDelta()
	 */
	public SpacedVector3(double alpha, double delta) {
		double cosDelta = Math.cos(delta);
		this.x = Math.cos(alpha) * cosDelta;
		this.y = Math.sin(alpha) * cosDelta;
		this.z = Math.sin(delta);
	}

	/**
	 * Multiplicative constructor
	 * Build a vector from another one and a scale factor.
	 * The vector built will be a * u
	 *
	 * @param a scale factor
	 * @param u base (unscaled) vector
	 */
	public SpacedVector3(double a, SpacedVector3 u) {
		this.x = a * u.x;
		this.y = a * u.y;
		this.z = a * u.z;
	}

	/**
	 * Linear constructor
	 * Build a vector from two other ones and corresponding scale factors.
	 * The vector built will be a1 * u1 + a2 * u2
	 *
	 * @param a1 first scale factor
	 * @param u1 first base (unscaled) vector
	 * @param a2 second scale factor
	 * @param u2 second base (unscaled) vector
	 */
	public SpacedVector3(double a1, SpacedVector3 u1, double a2, SpacedVector3 u2) {
		this.x = a1 * u1.x + a2 * u2.x;
		this.y = a1 * u1.y + a2 * u2.y;
		this.z = a1 * u1.z + a2 * u2.z;
	}

	/**
	 * Linear constructor
	 * Build a vector from three other ones and corresponding scale factors.
	 * The vector built will be a1 * u1 + a2 * u2 + a3 * u3
	 *
	 * @param a1 first scale factor
	 * @param u1 first base (unscaled) vector
	 * @param a2 second scale factor
	 * @param u2 second base (unscaled) vector
	 * @param a3 third scale factor
	 * @param u3 third base (unscaled) vector
	 */
	public SpacedVector3(
			double a1, SpacedVector3 u1, double a2, SpacedVector3 u2,
			double a3, SpacedVector3 u3) {
		this.x = a1 * u1.x + a2 * u2.x + a3 * u3.x;
		this.y = a1 * u1.y + a2 * u2.y + a3 * u3.y;
		this.z = a1 * u1.z + a2 * u2.z + a3 * u3.z;
	}

	/**
	 * Linear constructor
	 * Build a vector from four other ones and corresponding scale factors.
	 * The vector built will be a1 * u1 + a2 * u2 + a3 * u3 + a4 * u4
	 *
	 * @param a1 first scale factor
	 * @param u1 first base (unscaled) vector
	 * @param a2 second scale factor
	 * @param u2 second base (unscaled) vector
	 * @param a3 third scale factor
	 * @param u3 third base (unscaled) vector
	 * @param a4 fourth scale factor
	 * @param u4 fourth base (unscaled) vector
	 */
	public SpacedVector3(
			double a1, SpacedVector3 u1, double a2, SpacedVector3 u2,
			double a3, SpacedVector3 u3, double a4, SpacedVector3 u4) {
		this.x = a1 * u1.x + a2 * u2.x + a3 * u3.x + a4 * u4.x;
		this.y = a1 * u1.y + a2 * u2.y + a3 * u3.y + a4 * u4.y;
		this.z = a1 * u1.z + a2 * u2.z + a3 * u3.z + a4 * u4.z;
	}

	/**
	 * Get the abscissa of the vector.
	 *
	 * @return abscissa of the vector
	 * @see #SpacedVector3(double, double, double)
	 */
	@LuaMethod(name = "GetX")
	public double getX() {
		return x;
	}

	/**
	 * Get the ordinate of the vector.
	 *
	 * @return ordinate of the vector
	 * @see #SpacedVector3(double, double, double)
	 */
	@LuaMethod(name = "GetY")
	public double getY() {
		return y;
	}

	/**
	 * Get the height of the vector.
	 *
	 * @return height of the vector
	 * @see #SpacedVector3(double, double, double)
	 */
	@LuaMethod(name = "GetZ")
	public double getZ() {
		return z;
	}

	/**
	 * Get the L<sub>1</sub> norm for the vector.
	 *
	 * @return L<sub>1</sub> norm for the vector
	 */
	public double getNorm1() {
		return Math.abs(x) + Math.abs(y) + Math.abs(z);
	}

	/**
	 * Get the L<sub>2</sub> norm for the vector.
	 *
	 * @return euclidian norm for the vector
	 */
	public double getNorm() {
		return Math.sqrt(x * x + y * y + z * z);
	}

	/**
	 * Get the square of the norm for the vector.
	 *
	 * @return square of the euclidian norm for the vector
	 */
	public double getNormSq() {
		return x * x + y * y + z * z;
	}

	/**
	 * Get the L<sub>&infin;</sub> norm for the vector.
	 *
	 * @return L<sub>&infin;</sub> norm for the vector
	 */
	public double getNormInf() {
		return Math.max(Math.max(Math.abs(x), Math.abs(y)), Math.abs(z));
	}

	/**
	 * Get the azimuth of the vector.
	 *
	 * @return azimuth (&alpha;) of the vector, between -&pi; and +&pi;
	 * @see #SpacedVector3(double, double)
	 */
	public double getAlpha() {
		return Math.atan2(y, x);
	}

	/**
	 * Get the elevation of the vector.
	 *
	 * @return elevation (&delta;) of the vector, between -&pi;/2 and +&pi;/2
	 * @see #SpacedVector3(double, double)
	 */
	public double getDelta() {
		return Math.asin(z / getNorm());
	}

	/**
	 * Add a vector to the instance.
	 *
	 * @param v vector to add
	 * @return a new vector
	 */
	public SpacedVector3 add(SpacedVector3 v) {
		return new SpacedVector3(x + v.x, y + v.y, z + v.z);
	}

	/**
	 * Add a scaled vector to the instance.
	 *
	 * @param factor scale factor to apply to v before adding it
	 * @param v		vector to add
	 * @return a new vector
	 */
	public SpacedVector3 add(double factor, SpacedVector3 v) {
		return new SpacedVector3(x + factor * v.x, y + factor * v.y, z + factor * v.z);
	}

	/**
	 * Subtract a vector from the instance.
	 *
	 * @param v vector to subtract
	 * @return a new vector
	 */
	public SpacedVector3 subtract(SpacedVector3 v) {
		return new SpacedVector3(x - v.x, y - v.y, z - v.z);
	}

	/**
	 * Subtract a scaled vector from the instance.
	 *
	 * @param factor scale factor to apply to v before subtracting it
	 * @param v		vector to subtract
	 * @return a new vector
	 */
	public SpacedVector3 subtract(double factor, SpacedVector3 v) {
		return new SpacedVector3(x - factor * v.x, y - factor * v.y, z - factor * v.z);
	}

	/**
	 * Get a normalized vector aligned with the instance or this if the norm is 0
	 *
	 * @return a new normalized vector
	 */
	public SpacedVector3 normalize() {
		double s = getNorm();
		if (s == 0) {
			return this;
		}
		return scalarMultiply(1 / s);
	}

	/**
	 * Get a vector orthogonal to the instance.
	 * <p>There are an infinite number of normalized vectors orthogonal
	 * to the instance. This method picks up one of them almost
	 * arbitrarily. It is useful when one needs to compute a reference
	 * frame with one of the axes in a predefined direction. The
	 * following example shows how to build a frame having the k axis
	 * aligned with the known vector u :
	 * <pre><code>
	 *   SpacedVector3 k = u.normalize();
	 *   SpacedVector3 i = k.orthogonal();
	 *   SpacedVector3 j = SpacedVector3.crossProduct(k, i);
	 * </code></pre></p>
	 *
	 * @return a new normalized vector orthogonal to the instance
	 * @throws ArithmeticException if the norm of the instance is null
	 */
	public SpacedVector3 orthogonal() {

		double threshold = 0.6 * getNorm();
		if (threshold == 0) {
			// TODO: Change?
			throw MathRuntimeException.createArithmeticException("zero norm");
		}

		if ((x >= -threshold) && (x <= threshold)) {
			double inverse = 1 / Math.sqrt(y * y + z * z);
			return new SpacedVector3(0, inverse * z, -inverse * y);
		} else if ((y >= -threshold) && (y <= threshold)) {
			double inverse = 1 / Math.sqrt(x * x + z * z);
			return new SpacedVector3(-inverse * z, 0, inverse * x);
		}
		double inverse = 1 / Math.sqrt(x * x + y * y);
		return new SpacedVector3(inverse * y, -inverse * x, 0);

	}

	/**
	 * Compute the angular separation between two vectors.
	 * <p>This method computes the angular separation between two
	 * vectors using the dot product for well separated vectors and the
	 * cross product for almost aligned vectors. This allows to have a
	 * good accuracy in all cases, even for vectors very close to each
	 * other.</p>
	 *
	 * @param v1 first vector
	 * @param v2 second vector
	 * @return angular separation between v1 and v2
	 * @throws ArithmeticException if either vector has a null norm
	 */
	public static double angle(SpacedVector3 v1, SpacedVector3 v2) {

		double normProduct = v1.getNorm() * v2.getNorm();
		if (normProduct == 0) {
			throw MathRuntimeException.createArithmeticException("zero norm");
		}

		double dot = dotProduct(v1, v2);
		double threshold = normProduct * 0.9999;
		if ((dot < -threshold) || (dot > threshold)) {
			// the vectors are almost aligned, compute using the sine
			SpacedVector3 v3 = crossProduct(v1, v2);
			if (dot >= 0) {
				return Math.asin(v3.getNorm() / normProduct);
			}
			return Math.PI - Math.asin(v3.getNorm() / normProduct);
		}

		// the vectors are sufficiently separated to use the cosine
		return Math.acos(dot / normProduct);

	}

	/**
	 * Get the opposite of the instance.
	 *
	 * @return a new vector which is opposite to the instance
	 */
	public SpacedVector3 negate() {
		return new SpacedVector3(-x, -y, -z);
	}

	/**
	 * Multiply the instance by a scalar
	 *
	 * @param a scalar
	 * @return a new vector
	 */
	public SpacedVector3 scalarMultiply(double a) {
		return new SpacedVector3(a * x, a * y, a * z);
	}

	/**
	 * Returns true if any coordinate of this vector is NaN; false otherwise
	 *
	 * @return true if any coordinate of this vector is NaN; false otherwise
	 */
	public boolean isNaN() {
		return Double.isNaN(x) || Double.isNaN(y) || Double.isNaN(z);
	}

	/**
	 * Returns true if any coordinate of this vector is infinite and none are NaN;
	 * false otherwise
	 *
	 * @return true if any coordinate of this vector is infinite and none are NaN;
	 *         false otherwise
	 */
	public boolean isInfinite() {
		return !isNaN() && (Double.isInfinite(x) || Double.isInfinite(y) || Double.isInfinite(z));
	}

	/**
	 * Test for the equality of two 3D vectors.
	 * <p>
	 * If all coordinates of two 3D vectors are exactly the same, and none are
	 * <code>Double.NaN</code>, the two 3D vectors are considered to be equal.
	 * </p>
	 * <p>
	 * <code>NaN</code> coordinates are considered to affect globally the vector
	 * and be equals to each other - i.e, if either (or all) coordinates of the
	 * 3D vector are equal to <code>Double.NaN</code>, the 3D vector is equal to
	 * {@link #NaN}.
	 * </p>
	 *
	 * @param other Object to test for equality to this
	 * @return true if two 3D vector objects are equal, false if
	 *         object is null, not an instance of SpacedVector3, or
	 *         not equal to this SpacedVector3 instance
	 */
	@Override
	public boolean equals(Object other) {

		if (this == other) {
			return true;
		}

		if (other instanceof SpacedVector3) {
			final SpacedVector3 rhs = (SpacedVector3) other;
			if (rhs.isNaN()) {
				return this.isNaN();
			}

			return (MathUtils.equals(x,rhs.x) && MathUtils.equals(y,rhs.y) && MathUtils.equals(z, rhs.z));
		}
		return false;
	}

	/**
	 * Get a hashCode for the 3D vector.
	 * <p>
	 * All NaN values have the same hash code.</p>
	 *
	 * @return a hash code value for this object
	 */
	@Override
	public int hashCode() {
		if (isNaN()) {
			return 8;
		}
		// TODO: remove dependency?
		return 31 * (23 * MathUtils.hash(x) + 19 * MathUtils.hash(y) + MathUtils.hash(z));
	}

	/**
	 * Compute the dot-product of two vectors.
	 *
	 * @param v1 first vector
	 * @param v2 second vector
	 * @return the dot product v1.v2
	 */
	public static double dotProduct(SpacedVector3 v1, SpacedVector3 v2) {
		return v1.x * v2.x + v1.y * v2.y + v1.z * v2.z;
	}

	/**
	 * Compute the cross-product of two vectors.
	 *
	 * @param v1 first vector
	 * @param v2 second vector
	 * @return the cross product v1 ^ v2 as a new Vector
	 */
	public static SpacedVector3 crossProduct(SpacedVector3 v1, SpacedVector3 v2) {
		return new SpacedVector3(v1.y * v2.z - v1.z * v2.y,
				v1.z * v2.x - v1.x * v2.z,
				v1.x * v2.y - v1.y * v2.x);
	}

	/**
	 * Compute the distance between two vectors according to the L<sub>1</sub> norm.
	 * <p>Calling this method is equivalent to calling:
	 * <code>v1.subtract(v2).getNorm1()</code> except that no intermediate
	 * vector is built</p>
	 *
	 * @param v1 first vector
	 * @param v2 second vector
	 * @return the distance between v1 and v2 according to the L<sub>1</sub> norm
	 */
	public static double distance1(SpacedVector3 v1, SpacedVector3 v2) {
		final double dx = Math.abs(v2.x - v1.x);
		final double dy = Math.abs(v2.y - v1.y);
		final double dz = Math.abs(v2.z - v1.z);
		return dx + dy + dz;
	}

	/**
	 * Compute the distance between two vectors according to the L<sub>2</sub> norm.
	 * <p>Calling this method is equivalent to calling:
	 * <code>v1.subtract(v2).getNorm()</code> except that no intermediate
	 * vector is built</p>
	 *
	 * @param v1 first vector
	 * @param v2 second vector
	 * @return the distance between v1 and v2 according to the L<sub>2</sub> norm
	 */
	public static double distance(SpacedVector3 v1, SpacedVector3 v2) {
		final double dx = v2.x - v1.x;
		final double dy = v2.y - v1.y;
		final double dz = v2.z - v1.z;
		return Math.sqrt(dx * dx + dy * dy + dz * dz);
	}

	/**
	 * Compute the distance between two vectors according to the L<sub>&infin;</sub> norm.
	 * <p>Calling this method is equivalent to calling:
	 * <code>v1.subtract(v2).getNormInf()</code> except that no intermediate
	 * vector is built</p>
	 *
	 * @param v1 first vector
	 * @param v2 second vector
	 * @return the distance between v1 and v2 according to the L<sub>&infin;</sub> norm
	 */
	public static double distanceInf(SpacedVector3 v1, SpacedVector3 v2) {
		final double dx = Math.abs(v2.x - v1.x);
		final double dy = Math.abs(v2.y - v1.y);
		final double dz = Math.abs(v2.z - v1.z);
		return Math.max(Math.max(dx, dy), dz);
	}

	/**
	 * Compute the square of the distance between two vectors.
	 * <p>Calling this method is equivalent to calling:
	 * <code>v1.subtract(v2).getNormSq()</code> except that no intermediate
	 * vector is built</p>
	 *
	 * @param v1 first vector
	 * @param v2 second vector
	 * @return the square of the distance between v1 and v2
	 */
	public static double distanceSq(SpacedVector3 v1, SpacedVector3 v2) {
		final double dx = v2.x - v1.x;
		final double dy = v2.y - v1.y;
		final double dz = v2.z - v1.z;
		return dx * dx + dy * dy + dz * dz;
	}

	// Implementation of Ardors ReadOnlyVector3

	@Override
	public float getXf() {
		return (float) x;
	}

	@Override
	public float getYf() {
		return (float) y;
	}

	@Override
	public float getZf() {
		return (float) z;
	}

	@Override
	public double getValue(int index) {
		switch (index) {
			case 0:
				return x;
			case 1:
				return y;
			case 2:
				return z;
			default:
				throw new IllegalArgumentException("Index must be between 0 and 2 but was " + index);
		}
	}

	@Override
	public Vector3 add(double x, double y, double z, Vector3 store) {
		if (store == null) {
			store = new Vector3();
		}
		store.set(Vectors.fromSpaced(add(new SpacedVector3(x, y, z))));
		return store;
	}

	@Override
	public Vector3 add(ReadOnlyVector3 source, Vector3 store) {
		return add(source.getX(), source.getY(), source.getZ(), store);
	}

	@Override
	public Vector3 subtract(double x, double y, double z, Vector3 store) {
		if (store == null) {
			store = new Vector3();
		}
		store.set(Vectors.fromSpaced(subtract(new SpacedVector3(x, y, z))));
		return store;
	}

	@Override
	public Vector3 subtract(ReadOnlyVector3 source, Vector3 store) {
		return subtract(source.getX(), source.getY(), source.getZ(), store);
	}

	@Override
	public Vector3 multiply(double scalar, Vector3 store) {
		if (store == null) {
			store = new Vector3();
		}
		store.set(Vectors.fromSpaced(scalarMultiply(scalar)));
		return store;
	}

	@Override
	public Vector3 multiply(ReadOnlyVector3 scale, Vector3 store) {
		if (store == null) {
			store = new Vector3();
		}
		store.set(x * scale.getX(), y * scale.getY(), z * scale.getZ());
		return store;
	}

	@Override
	public Vector3 multiply(double x, double y, double z, Vector3 store) {
		if (store == null) {
			store = new Vector3();
		}
		store.set(this.x * x, this.y * y, this.z * z);
		return store;
	}

	@Override
	public Vector3 divide(double scalar, Vector3 store) {
		if (store == null) {
			store = new Vector3();
		}
		store.set(x / scalar, y / scalar, z / scalar);
		return store;
	}

	@Override
	public Vector3 divide(ReadOnlyVector3 scale, Vector3 store) {
		if (store == null) {
			store = new Vector3();
		}
		store.set(x / scale.getX(), y / scale.getY(), z / scale.getZ());
		return store;
	}

	@Override
	public Vector3 divide(double x, double y, double z, Vector3 store) {
		if (store == null) {
			store = new Vector3();
		}
		store.set(this.x / x, this.y / y, this.z / z);
		return store;
	}

	@Override
	public Vector3 scaleAdd(double scale, ReadOnlyVector3 add, Vector3 store) {
		if (store == null) {
			store = new Vector3();
		}
		store.setX(x * scale + add.getX());
		store.setY(y * scale + add.getY());
		store.setZ(z * scale + add.getZ());
		return store;
	}

	@Override
	public Vector3 negate(Vector3 store) {
		if (store == null) {
			store = new Vector3();
		}
		store.set(Vectors.fromSpaced(negate()));
		return store;
	}

	@Override
	public Vector3 normalize(Vector3 store) {
		if (store == null) {
			store = new Vector3();
		}
		store.set(Vectors.fromSpaced(normalize()));
		return store;
	}

	@Override
	public Vector3 lerp(ReadOnlyVector3 endVec, double scalar, Vector3 store) {
		if (store == null) {
			store = new Vector3();
		}
		store.set(Vectors.fromSpaced(VectorMath.lerp(this, Vectors.fromArdor(endVec), scalar)));
		return store;
	}

	@Override
	public double length() {
		return getNorm();
	}

	@Override
	public double lengthSquared() {
		return getNormSq();
	}

	@Override
	public double distanceSquared(double x, double y, double z) {
		return SpacedVector3.distanceSq(this, new SpacedVector3(x, y, z));
	}

	@Override
	public double distanceSquared(ReadOnlyVector3 destination) {
		return distanceSquared(destination.getX(), destination.getY(), destination.getZ());
	}

	@Override
	public double distance(double x, double y, double z) {
		return SpacedVector3.distance(this, new SpacedVector3(x, y, z));
	}

	@Override
	public double distance(ReadOnlyVector3 destination) {
		return distance(destination.getX(), destination.getY(), destination.getZ());
	}

	@Override
	public double dot(double x, double y, double z) {
		return SpacedVector3.dotProduct(this, new SpacedVector3(x, y, z));
	}

	@Override
	public double dot(ReadOnlyVector3 vec) {
		return dot(vec.getX(), vec.getY(), vec.getZ());
	}

	@Override
	public Vector3 cross(double x, double y, double z, Vector3 store) {
		if (store == null) {
			store = new Vector3();
		}
		store.set(Vectors.fromSpaced(SpacedVector3.crossProduct(this, new SpacedVector3(x, y, z))));
		return store;
	}

	@Override
	public Vector3 cross(ReadOnlyVector3 vec, Vector3 store) {
		return cross(vec.getX(), vec.getY(), vec.getZ(), store);
	}

	@Override
	public double smallestAngleBetween(ReadOnlyVector3 otherVector) {
		return Math.acos(dot(otherVector.getX(), otherVector.getY(), otherVector.getZ()));
	}

	@Override
	public double[] toArray(double[] store) {
		if (store == null) {
			store = new double[3];
		}
		store[2] = z;
		store[1] = y;
		store[0] = x;
		return store;
	}

	@Override
	public Vector3 clone() {
		return new Vector3(x, y, z);
	}

	@Override
	public String toString() {
		return "SpacedVector3(" + x + ", " + y + ", " + z + ")";
	}
}
