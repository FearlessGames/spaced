package se.ardortech.math;

import com.ardor3d.math.Quaternion;
import com.ardor3d.math.type.ReadOnlyVector3;
import se.krka.kahlua.integration.annotations.LuaMethod;
import se.krka.kahlua.integration.expose.ReturnValues;

import java.io.Serializable;

/**
 * This class implements rotations in a three-dimensional space.
 * <p/>
 * <p>Rotations can be represented by several different mathematical
 * entities (matrices, axe and angle, Cardan or Euler angles,
 * quaternions). This class presents an higher level abstraction, more
 * user-oriented and hiding this implementation details. Well, for the
 * curious, we use quaternions for the internal representation. The
 * user can build a rotation from any of these representations, and
 * any of these representations can be retrieved from a
 * <code>Rotation</code> instance (see the various constructors and
 * getters). In addition, a rotation can also be built implicitely
 * from a set of vectors and their image.</p>
 * <p>This implies that this class can be used to convert from one
 * representation to another one. For example, converting a rotation
 * matrix into a set of Cardan angles from can be done using the
 * followong single line of code:</p>
 * <pre>
 * double[] angles = new Rotation(matrix, 1.0e-10).getAngles(RotationOrder.XYZ);
 * </pre>
 * <p>Focus is oriented on what a rotation <em>do</em> rather than on its
 * underlying representation. Once it has been built, and regardless of its
 * internal representation, a rotation is an <em>operator</em> which basically
 * transforms three dimensional {@link SpacedVector3 vectors} into other three
 * dimensional {@link SpacedVector3 vectors}. Depending on the application, the
 * meaning of these vectors may vary and the semantics of the rotation also.</p>
 * <p>For example in an spacecraft attitude simulation tool, users will often
 * consider the vectors are fixed (say the Earth direction for example) and the
 * rotation transforms the coordinates coordinates of this vector in inertial
 * frame into the coordinates of the same vector in satellite frame. In this
 * case, the rotation implicitely defines the relation between the two frames.
 * Another example could be a telescope control application, where the rotation
 * would transform the sighting direction at rest into the desired observing
 * direction when the telescope is pointed towards an object of interest. In this
 * case the rotation transforms the directionf at rest in a topocentric frame
 * into the sighting direction in the same topocentric frame. In many case, both
 * approaches will be combined, in our telescope example, we will probably also
 * need to transform the observing direction in the topocentric frame into the
 * observing direction in inertial frame taking into account the observatory
 * location and the Earth rotation.</p>
 * <p/>
 * <p>These examples show that a rotation is what the user wants it to be, so this
 * class does not push the user towards one specific definition and hence does not
 * provide methods like <code>projectVectorIntoDestinationFrame</code> or
 * <code>computeTransformedDirection</code>. It provides simpler and more generic
 * methods: {@link #applyTo(SpacedVector3) applyTo(SpacedVector3)} and {@link
 * #applyInverseTo(SpacedVector3) applyInverseTo(SpacedVector3)}.</p>
 * <p/>
 * <p>Since a rotation is basically a vectorial operator, several rotations can be
 * composed together and the composite operation <code>r = r<sub>1</sub> o
 * r<sub>2</sub></code> (which means that for each vector <code>u</code>,
 * <code>r(u) = r<sub>1</sub>(r<sub>2</sub>(u))</code>) is also a rotation. Hence
 * we can consider that in addition to vectors, a rotation can be applied to other
 * rotations as well (or to itself). With our previous notations, we would say we
 * can apply <code>r<sub>1</sub></code> to <code>r<sub>2</sub></code> and the result
 * we get is <code>r = r<sub>1</sub> o r<sub>2</sub></code>. For this purpose, the
 * class provides the methods: {@link #applyTo(SpacedRotation) applyTo(Rotation)} and
 * {@link #applyInverseTo(SpacedRotation) applyInverseTo(Rotation)}.</p>
 * <p/>
 * <p>Rotations are guaranteed to be immutable objects.</p>
 *
 * @version $Revision: 772119 $ $Date: 2009-05-06 05:43:28 -0400 (Wed, 06 May 2009) $
 * @see SpacedVector3
 * @see SpacedRotationOrder
 * @since 1.2
 */

public class SpacedRotation implements Serializable {

	/**
	 * Identity rotation.
	 */
	public static final SpacedRotation IDENTITY = new SpacedRotation(0.0, 0.0, 0.0, 1.0, false);

	/**
	 * Serializable version identifier
	 */
	private static final long serialVersionUID = -2153622329907944313L;

	/**
	 * Scalar coordinate of the quaternion.
	 */
	private final double w;

	/**
	 * First coordinate of the vectorial part of the quaternion.
	 */
	private final double x;

	/**
	 * Second coordinate of the vectorial part of the quaternion.
	 */
	private final double y;

	/**
	 * Third coordinate of the vectorial part of the quaternion.
	 */
	private final double z;

	/**
	 * Build a rotation from the quaternion coordinates.
	 * <p>A rotation can be built from a <em>normalized</em> quaternion,
	 * i.e. a quaternion for which q<sub>0</sub><sup>2</sup> +
	 * q<sub>1</sub><sup>2</sup> + q<sub>2</sub><sup>2</sup> +
	 * q<sub>3</sub><sup>2</sup> = 1. If the quaternion is not normalized,
	 * the constructor can normalize it in a preprocessing step.</p>
	 *
	 * @param x					  first coordinate of the vectorial part of the quaternion
	 * @param y					  second coordinate of the vectorial part of the quaternion
	 * @param z					  third coordinate of the vectorial part of the quaternion
	 * @param w					  scalar part of the quaternion
	 * @param needsNormalization if true, the coordinates are considered
*                           not to be normalized, a normalization preprocessing step is performed
	 */
	public SpacedRotation(
			double x, double y, double z, double w,
			boolean needsNormalization) {
		if (needsNormalization) {
			// normalization preprocessing
			double inv = 1.0 / Math.sqrt(w * w + x * x + y * y + z * z);
			w *= inv;
			x *= inv;
			y *= inv;
			z *= inv;
		}
		this.w = w;
		this.x = x;
		this.y = y;
		this.z = z;


	}

	public SpacedRotation(double x, double y, double z, double w) {
		this.w = w;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	/**
	 * Build a rotation from an axis and an angle.
	 * <p>We use the convention that angles are oriented according to
	 * the effect of the rotation on vectors around the axis. That means
	 * that if (i, j, k) is a direct frame and if we first provide +k as
	 * the axis and PI/2 as the angle to this constructor, and then
	 * {@link #applyTo(SpacedVector3) apply} the instance to +i, we will get
	 * +j.</p>
	 *
	 * @param axis  axis around which to rotate
	 * @param angle rotation angle.
	 * @throws ArithmeticException if the axis norm is zero
	 */
	public SpacedRotation(SpacedVector3 axis, double angle) {

		double norm = axis.getNorm();
		if (norm == 0) {
			throw new RuntimeException("zero norm for rotation axis " + axis.toString());
		}

		double halfAngle = -0.5 * angle;
		double coeff = Math.sin(halfAngle) / norm;

		w = Math.cos(halfAngle);
		x = coeff * axis.getX();
		y = coeff * axis.getY();
		z = coeff * axis.getZ();

	}

	/**
	 * Build a rotation from a 3X3 matrix.
	 * <p/>
	 * <p>Rotation matrices are orthogonal matrices, i.e. unit matrices
	 * (which are matrices for which m.m<sup>T</sup> = I) with real
	 * coefficients. The module of the determinant of unit matrices is
	 * 1, among the orthogonal 3X3 matrices, only the ones having a
	 * positive determinant (+1) are rotation matrices.</p>
	 * <p/>
	 * <p>When a rotation is defined by a matrix with truncated values
	 * (typically when it is extracted from a technical sheet where only
	 * four to five significant digits are available), the matrix is not
	 * orthogonal anymore. This constructor handles this case
	 * transparently by using a copy of the given matrix and applying a
	 * correction to the copy in order to perfect its orthogonality. If
	 * the Frobenius norm of the correction needed is above the given
	 * threshold, then the matrix is considered to be too far from a
	 * true rotation matrix and an exception is thrown.<p>
	 *
	 * @param m			rotation matrix
	 * @param threshold convergence threshold for the iterative
	 *                  orthogonality correction (convergence is reached when the
	 *                  difference between two steps of the Frobenius norm of the
	 *                  correction is below this threshold)
	 */
	public SpacedRotation(double[][] m, double threshold) {

		// dimension check
		if ((m.length != 3) || (m[0].length != 3) ||
				(m[1].length != 3) || (m[2].length != 3)) {
			throw new RuntimeException(
					String.format("a %d,%d matrix cannot be a rotation matrix",
							m.length, m[0].length));
		}

		// compute a "close" orthogonal matrix
		double[][] ort = orthogonalizeMatrix(m, threshold);

		// check the sign of the determinant
		double det = ort[0][0] * (ort[1][1] * ort[2][2] - ort[2][1] * ort[1][2]) -
				ort[1][0] * (ort[0][1] * ort[2][2] - ort[2][1] * ort[0][2]) +
				ort[2][0] * (ort[0][1] * ort[1][2] - ort[1][1] * ort[0][2]);
		if (det < 0.0) {
			throw new RuntimeException(
					"the closest orthogonal matrix has a negative determinant " + det);
		}

		// There are different ways to compute the quaternions elements
		// from the matrix. They all involve computing one element from
		// the diagonal of the matrix, and computing the three other ones
		// using a formula involving a division by the first element,
		// which unfortunately can be zero. Since the norm of the
		// quaternion is 1, we know at least one element has an absolute
		// value greater or equal to 0.5, so it is always possible to
		// select the right formula and avoid division by zero and even
		// numerical inaccuracy. Checking the elements in turn and using
		// the first one greater than 0.45 is safe (this leads to a simple
		// test since qi = 0.45 implies 4 qi^2 - 1 = -0.19)
		double s = ort[0][0] + ort[1][1] + ort[2][2];
		if (s > -0.19) {
			// compute w and deduce x, y and z
			w = 0.5 * Math.sqrt(s + 1.0);
			double inv = 0.25 / w;
			x = inv * (ort[1][2] - ort[2][1]);
			y = inv * (ort[2][0] - ort[0][2]);
			z = inv * (ort[0][1] - ort[1][0]);
		} else {
			s = ort[0][0] - ort[1][1] - ort[2][2];
			if (s > -0.19) {
				// compute x and deduce w, y and z
				x = 0.5 * Math.sqrt(s + 1.0);
				double inv = 0.25 / x;
				w = inv * (ort[1][2] - ort[2][1]);
				y = inv * (ort[0][1] + ort[1][0]);
				z = inv * (ort[0][2] + ort[2][0]);
			} else {
				s = ort[1][1] - ort[0][0] - ort[2][2];
				if (s > -0.19) {
					// compute y and deduce w, x and z
					y = 0.5 * Math.sqrt(s + 1.0);
					double inv = 0.25 / y;
					w = inv * (ort[2][0] - ort[0][2]);
					x = inv * (ort[0][1] + ort[1][0]);
					z = inv * (ort[2][1] + ort[1][2]);
				} else {
					// compute z and deduce w, x and y
					s = ort[2][2] - ort[0][0] - ort[1][1];
					z = 0.5 * Math.sqrt(s + 1.0);
					double inv = 0.25 / z;
					w = inv * (ort[0][1] - ort[1][0]);
					x = inv * (ort[0][2] + ort[2][0]);
					y = inv * (ort[2][1] + ort[1][2]);
				}
			}
		}

	}

	public static SpacedRotation fromRotationMatrix(final double m00, final double m01, final double m02, final double m10,
            final double m11, final double m12, final double m20, final double m21, final double m22) {
        // Uses the Graphics Gems code, from
        // ftp://ftp.cis.upenn.edu/pub/graphics/shoemake/quatut.ps.Z
        // *NOT* the "Matrix and Quaternions FAQ", which has errors!

        // the trace is the sum of the diagonal elements; see
        // http://mathworld.wolfram.com/MatrixTrace.html
        final double t = m00 + m11 + m22;

        // we protect the division by s by ensuring that s>=1
        double x, y, z, w;
        if (t >= 0) { // |w| >= .5
            double s = Math.sqrt(t + 1); // |s|>=1 ...
            w = 0.5 * s;
            s = 0.5 / s; // so this division isn't bad
            x = (m21 - m12) * s;
            y = (m02 - m20) * s;
            z = (m10 - m01) * s;
        } else if ((m00 > m11) && (m00 > m22)) {
            double s = Math.sqrt(1.0 + m00 - m11 - m22); // |s|>=1
            x = s * 0.5; // |x| >= .5
            s = 0.5 / s;
            y = (m10 + m01) * s;
            z = (m02 + m20) * s;
            w = (m21 - m12) * s;
        } else if (m11 > m22) {
            double s = Math.sqrt(1.0 + m11 - m00 - m22); // |s|>=1
            y = s * 0.5; // |y| >= .5
            s = 0.5 / s;
            x = (m10 + m01) * s;
            z = (m21 + m12) * s;
            w = (m02 - m20) * s;
        } else {
            double s = Math.sqrt(1.0 + m22 - m00 - m11); // |s|>=1
            z = s * 0.5; // |z| >= .5
            s = 0.5 / s;
            x = (m02 + m20) * s;
            y = (m21 + m12) * s;
            w = (m10 - m01) * s;
        }

        return new SpacedRotation(x, y, z, w, true);
    }

	public static SpacedRotation fromAxes(ReadOnlyVector3 xAxis, ReadOnlyVector3 yAxis, ReadOnlyVector3 zAxis) {
		return fromRotationMatrix(xAxis.getX(), yAxis.getX(), zAxis.getX(), xAxis.getY(), yAxis.getY(), zAxis.getY(),
                xAxis.getZ(), yAxis.getZ(), zAxis.getZ());
	}

	/**
	 * Build the rotation that transforms a pair of vector into another pair.
	 * <p/>
	 * <p>Except for possible scale factors, if the instance were applied to
	 * the pair (u<sub>1</sub>, u<sub>2</sub>) it will produce the pair
	 * (v<sub>1</sub>, v<sub>2</sub>).</p>
	 * <p/>
	 * <p>If the angular separation between u<sub>1</sub> and u<sub>2</sub> is
	 * not the same as the angular separation between v<sub>1</sub> and
	 * v<sub>2</sub>, then a corrected v'<sub>2</sub> will be used rather than
	 * v<sub>2</sub>, the corrected vector will be in the (v<sub>1</sub>,
	 * v<sub>2</sub>) plane.</p>
	 *
	 * @param u1 first vector of the origin pair
	 * @param u2 second vector of the origin pair
	 * @param v1 desired image of u1 by the rotation
	 * @param v2 desired image of u2 by the rotation
	 * @throws IllegalArgumentException if the norm of one of the vectors is zero
	 */
	public SpacedRotation(SpacedVector3 u1, SpacedVector3 u2, SpacedVector3 v1, SpacedVector3 v2) {

		// norms computation
		double u1u1 = SpacedVector3.dotProduct(u1, u1);
		double u2u2 = SpacedVector3.dotProduct(u2, u2);
		double v1v1 = SpacedVector3.dotProduct(v1, v1);
		double v2v2 = SpacedVector3.dotProduct(v2, v2);
		if ((u1u1 == 0) || (u2u2 == 0) || (v1v1 == 0) || (v2v2 == 0)) {
			throw new RuntimeException("zero norm for rotation defining vector");
		}

		double u1x = u1.getX();
		double u1y = u1.getY();
		double u1z = u1.getZ();

		double u2x = u2.getX();
		double u2y = u2.getY();
		double u2z = u2.getZ();

		// normalize v1 in order to have (v1'|v1') = (u1|u1)
		double coeff = Math.sqrt(u1u1 / v1v1);
		double v1x = coeff * v1.getX();
		double v1y = coeff * v1.getY();
		double v1z = coeff * v1.getZ();
		v1 = new SpacedVector3(v1x, v1y, v1z);

		// adjust v2 in order to have (u1|u2) = (v1|v2) and (v2'|v2') = (u2|u2)
		double u1u2 = SpacedVector3.dotProduct(u1, u2);
		double v1v2 = SpacedVector3.dotProduct(v1, v2);
		double coeffU = u1u2 / u1u1;
		double coeffV = v1v2 / u1u1;
		double beta = Math.sqrt((u2u2 - u1u2 * coeffU) / (v2v2 - v1v2 * coeffV));
		double alpha = coeffU - beta * coeffV;
		double v2x = alpha * v1x + beta * v2.getX();
		double v2y = alpha * v1y + beta * v2.getY();
		double v2z = alpha * v1z + beta * v2.getZ();
		v2 = new SpacedVector3(v2x, v2y, v2z);

		// preliminary computation (we use explicit formulation instead
		// of relying on the SpacedVector3 class in order to avoid building lots
		// of temporary objects)
		SpacedVector3 uRef = u1;
		SpacedVector3 vRef = v1;
		double dx1 = v1x - u1.getX();
		double dy1 = v1y - u1.getY();
		double dz1 = v1z - u1.getZ();
		double dx2 = v2x - u2.getX();
		double dy2 = v2y - u2.getY();
		double dz2 = v2z - u2.getZ();
		SpacedVector3 k = new SpacedVector3(dy1 * dz2 - dz1 * dy2,
				dz1 * dx2 - dx1 * dz2,
				dx1 * dy2 - dy1 * dx2);
		double c = k.getX() * (u1y * u2z - u1z * u2y) +
				k.getY() * (u1z * u2x - u1x * u2z) +
				k.getZ() * (u1x * u2y - u1y * u2x);

		if (c == 0) {
			// the (x, y, z) vector is in the (u1, u2) plane
			// we try other vectors
			SpacedVector3 u3 = SpacedVector3.crossProduct(u1, u2);
			SpacedVector3 v3 = SpacedVector3.crossProduct(v1, v2);
			double u3x = u3.getX();
			double u3y = u3.getY();
			double u3z = u3.getZ();
			double v3x = v3.getX();
			double v3y = v3.getY();
			double v3z = v3.getZ();

			double dx3 = v3x - u3x;
			double dy3 = v3y - u3y;
			double dz3 = v3z - u3z;
			k = new SpacedVector3(dy1 * dz3 - dz1 * dy3,
					dz1 * dx3 - dx1 * dz3,
					dx1 * dy3 - dy1 * dx3);
			c = k.getX() * (u1y * u3z - u1z * u3y) +
					k.getY() * (u1z * u3x - u1x * u3z) +
					k.getZ() * (u1x * u3y - u1y * u3x);

			if (c == 0) {
				// the (x, y, z) vector is aligned with u1:
				// we try (u2, u3) and (v2, v3)
				k = new SpacedVector3(dy2 * dz3 - dz2 * dy3,
						dz2 * dx3 - dx2 * dz3,
						dx2 * dy3 - dy2 * dx3);
				c = k.getX() * (u2y * u3z - u2z * u3y) +
						k.getY() * (u2z * u3x - u2x * u3z) +
						k.getZ() * (u2x * u3y - u2y * u3x);

				if (c == 0) {
					// the (x, y, z) vector is aligned with everything
					// this is really the identity rotation
					w = 1.0;
					x = 0.0;
					y = 0.0;
					z = 0.0;
					return;
				}

				// we will have to use u2 and v2 to compute the scalar part
				uRef = u2;
				vRef = v2;

			}

		}

		// compute the vectorial part
		c = Math.sqrt(c);
		double inv = 1.0 / (c + c);
		x = inv * k.getX();
		y = inv * k.getY();
		z = inv * k.getZ();

		// compute the scalar part
		k = new SpacedVector3(uRef.getY() * z - uRef.getZ() * y,
				uRef.getZ() * x - uRef.getX() * z,
				uRef.getX() * y - uRef.getY() * x);
		c = SpacedVector3.dotProduct(k, k);
		w = SpacedVector3.dotProduct(vRef, k) / (c + c);

	}

	/**
	 * Build one of the rotations that transform one vector into another one.
	 * <p/>
	 * <p>Except for a possible scale factor, if the instance were
	 * applied to the vector u it will produce the vector v. There is an
	 * infinite number of such rotations, this constructor choose the
	 * one with the smallest associated angle (i.e. the one whose axis
	 * is orthogonal to the (u, v) plane). If u and v are colinear, an
	 * arbitrary rotation axis is chosen.</p>
	 *
	 * @param u origin vector
	 * @param v desired image of u by the rotation
	 * @throws IllegalArgumentException if the norm of one of the vectors is zero
	 */
	public SpacedRotation(SpacedVector3 u, SpacedVector3 v) {

		double normProduct = u.getNorm() * v.getNorm();
		if (normProduct == 0) {
			throw new RuntimeException("zero norm for rotation defining vector");
		}

		double dot = SpacedVector3.dotProduct(u, v);

		if (dot < ((2.0e-15 - 1.0) * normProduct)) {
			// special case u = -v: we select a PI angle rotation around
			// an arbitrary vector orthogonal to u
			SpacedVector3 w = u.orthogonal();
			this.w = 0.0;
			x = -w.getX();
			y = -w.getY();
			z = -w.getZ();
		} else {
			// general case: (u, v) defines a plane, we select
			// the shortest possible rotation: axis orthogonal to this plane
			w = Math.sqrt(0.5 * (1.0 + dot / normProduct));
			double coeff = 1.0 / (2.0 * w * normProduct);
			x = coeff * (v.getY() * u.getZ() - v.getZ() * u.getY());
			y = coeff * (v.getZ() * u.getX() - v.getX() * u.getZ());
			z = coeff * (v.getX() * u.getY() - v.getY() * u.getX());
		}

	}

	/**
	 * Build a rotation from three Cardan or Euler elementary rotations.
	 * <p/>
	 * <p>Cardan rotations are three successive rotations around the
	 * canonical axes X, Y and Z, each axis being used once. There are
	 * 6 such sets of rotations (XYZ, XZY, YXZ, YZX, ZXY and ZYX). Euler
	 * rotations are three successive rotations around the canonical
	 * axes X, Y and Z, the first and last rotations being around the
	 * same axis. There are 6 such sets of rotations (XYX, XZX, YXY,
	 * YZY, ZXZ and ZYZ), the most popular one being ZXZ.</p>
	 * <p>Beware that many people routinely use the term Euler angles even
	 * for what really are Cardan angles (this confusion is especially
	 * widespread in the aerospace business where Roll, Pitch and Yaw angles
	 * are often wrongly tagged as Euler angles).</p>
	 *
	 * @param order  order of rotations to use
	 * @param alpha1 angle of the first elementary rotation
	 * @param alpha2 angle of the second elementary rotation
	 * @param alpha3 angle of the third elementary rotation
	 */
	public SpacedRotation(
			SpacedRotationOrder order,
			double alpha1, double alpha2, double alpha3) {
		SpacedRotation r1 = new SpacedRotation(order.getA1(), alpha1);
		SpacedRotation r2 = new SpacedRotation(order.getA2(), alpha2);
		SpacedRotation r3 = new SpacedRotation(order.getA3(), alpha3);
		SpacedRotation composed = r1.applyTo(r2.applyTo(r3));
		w = composed.w;
		x = composed.x;
		y = composed.y;
		z = composed.z;
	}

	/**
	 * Revert a rotation.
	 * Build a rotation which reverse the effect of another
	 * rotation. This means that if r(u) = v, then r.revert(v) = u. The
	 * instance is not changed.
	 *
	 * @return a new rotation whose effect is the reverse of the effect
	 *         of the instance
	 */
	public SpacedRotation revert() {
		return new SpacedRotation(x, y, z, -w, false);
	}

	/**
	 * Get the scalar coordinate of the quaternion.
	 *
	 * @return scalar coordinate of the quaternion
	 */
	public double getW() {
		return w;
	}

	/**
	 * Get the first coordinate of the vectorial part of the quaternion.
	 *
	 * @return first coordinate of the vectorial part of the quaternion
	 */
	public double getX() {
		return x;
	}

	/**
	 * Get the second coordinate of the vectorial part of the quaternion.
	 *
	 * @return second coordinate of the vectorial part of the quaternion
	 */
	public double getY() {
		return y;
	}

	/**
	 * Get the third coordinate of the vectorial part of the quaternion.
	 *
	 * @return third coordinate of the vectorial part of the quaternion
	 */
	public double getZ() {
		return z;
	}

	/**
	 * Get the normalized axis of the rotation.
	 *
	 * @return normalized axis of the rotation
	 */
	@LuaMethod(name = "GetAxis")
	public SpacedVector3 getAxis() {
		double squaredSine = x * x + y * y + z * z;
		if (squaredSine == 0) {
			return new SpacedVector3(1, 0, 0);
		} else if (w < 0) {
			double inverse = 1 / Math.sqrt(squaredSine);
			return new SpacedVector3(x * inverse, y * inverse, z * inverse);
		}
		double inverse = -1 / Math.sqrt(squaredSine);
		return new SpacedVector3(x * inverse, y * inverse, z * inverse);
	}

	/**
	 * Get the angle of the rotation.
	 *
	 * @return angle of the rotation (between 0 and &pi;)
	 */
	public double getAngle() {
		if ((w < -0.1) || (w > 0.1)) {
			return 2 * Math.asin(Math.sqrt(x * x + y * y + z * z));
		} else if (w < 0) {
			return 2 * Math.acos(-w);
		}
		return 2 * Math.acos(w);
	}

	/**
	 * Get the Cardan or Euler angles corresponding to the instance.
	 * <p/>
	 * <p>The equations show that each rotation can be defined by two
	 * different values of the Cardan or Euler angles set. For example
	 * if Cardan angles are used, the rotation defined by the angles
	 * a<sub>1</sub>, a<sub>2</sub> and a<sub>3</sub> is the same as
	 * the rotation defined by the angles &pi; + a<sub>1</sub>, &pi;
	 * - a<sub>2</sub> and &pi; + a<sub>3</sub>. This method implements
	 * the following arbitrary choices:</p>
	 * <ul>
	 * <li>for Cardan angles, the chosen set is the one for which the
	 * second angle is between -&pi;/2 and &pi;/2 (i.e its cosine is
	 * positive),</li>
	 * <li>for Euler angles, the chosen set is the one for which the
	 * second angle is between 0 and &pi; (i.e its sine is positive).</li>
	 * </ul>
	 * <p/>
	 * <p>Cardan and Euler angle have a very disappointing drawback: all
	 * of them have singularities. This means that if the instance is
	 * too close to the singularities corresponding to the given
	 * rotation order, it will be impossible to retrieve the angles. For
	 * Cardan angles, this is often called gimbal lock. There is
	 * <em>nothing</em> to do to prevent this, it is an intrinsic problem
	 * with Cardan and Euler representation (but not a problem with the
	 * rotation itself, which is perfectly well defined). For Cardan
	 * angles, singularities occur when the second angle is close to
	 * -&pi;/2 or +&pi;/2, for Euler angle singularities occur when the
	 * second angle is close to 0 or &pi;, this implies that the identity
	 * rotation is always singular for Euler angles!</p>
	 *
	 * @param order rotation order to use
	 * @return an array of three angles, in the order specified by the set
	 */
	public double[] getAngles(SpacedRotationOrder order) {

		if (order == SpacedRotationOrder.XYZ) {

			// r (SpacedVector3.plusK) coordinates are :
			//  sin (theta), -cos (theta) sin (phi), cos (theta) cos (phi)
			// (-r) (SpacedVector3.plusI) coordinates are :
			// cos (psi) cos (theta), -sin (psi) cos (theta), sin (theta)
			// and we can choose to have theta in the interval [-PI/2 ; +PI/2]
			SpacedVector3 v1 = applyTo(SpacedVector3.PLUS_K);
			SpacedVector3 v2 = applyInverseTo(SpacedVector3.PLUS_I);
			if ((v2.getZ() < -0.9999999999) || (v2.getZ() > 0.9999999999)) {
				throw new RuntimeException("CardanEulerSingularityException");
			}
			return new double[]{
					Math.atan2(-(v1.getY()), v1.getZ()),
					Math.asin(v2.getZ()),
					Math.atan2(-(v2.getY()), v2.getX())
			};

		} else if (order == SpacedRotationOrder.XZY) {

			// r (SpacedVector3.plusJ) coordinates are :
			// -sin (psi), cos (psi) cos (phi), cos (psi) sin (phi)
			// (-r) (SpacedVector3.plusI) coordinates are :
			// cos (theta) cos (psi), -sin (psi), sin (theta) cos (psi)
			// and we can choose to have psi in the interval [-PI/2 ; +PI/2]
			SpacedVector3 v1 = applyTo(SpacedVector3.PLUS_J);
			SpacedVector3 v2 = applyInverseTo(SpacedVector3.PLUS_I);
			if ((v2.getY() < -0.9999999999) || (v2.getY() > 0.9999999999)) {
				throw new RuntimeException("CardanEulerSingularityException");
			}
			return new double[]{
					Math.atan2(v1.getZ(), v1.getY()),
					-Math.asin(v2.getY()),
					Math.atan2(v2.getZ(), v2.getX())
			};

		} else if (order == SpacedRotationOrder.YXZ) {

			// r (SpacedVector3.plusK) coordinates are :
			//  cos (phi) sin (theta), -sin (phi), cos (phi) cos (theta)
			// (-r) (SpacedVector3.plusJ) coordinates are :
			// sin (psi) cos (phi), cos (psi) cos (phi), -sin (phi)
			// and we can choose to have phi in the interval [-PI/2 ; +PI/2]
			SpacedVector3 v1 = applyTo(SpacedVector3.PLUS_K);
			SpacedVector3 v2 = applyInverseTo(SpacedVector3.PLUS_J);
			if ((v2.getZ() < -0.9999999999) || (v2.getZ() > 0.9999999999)) {
				throw new RuntimeException("CardanEulerSingularityException");
			}
			return new double[]{
					Math.atan2(v1.getX(), v1.getZ()),
					-Math.asin(v2.getZ()),
					Math.atan2(v2.getX(), v2.getY())
			};

		} else if (order == SpacedRotationOrder.YZX) {

			// r (SpacedVector3.plusI) coordinates are :
			// cos (psi) cos (theta), sin (psi), -cos (psi) sin (theta)
			// (-r) (SpacedVector3.plusJ) coordinates are :
			// sin (psi), cos (phi) cos (psi), -sin (phi) cos (psi)
			// and we can choose to have psi in the interval [-PI/2 ; +PI/2]
			SpacedVector3 v1 = applyTo(SpacedVector3.PLUS_I);
			SpacedVector3 v2 = applyInverseTo(SpacedVector3.PLUS_J);
			if ((v2.getX() < -0.9999999999) || (v2.getX() > 0.9999999999)) {
				throw new RuntimeException("CardanEulerSingularityException");
			}
			return new double[]{
					Math.atan2(-(v1.getZ()), v1.getX()),
					Math.asin(v2.getX()),
					Math.atan2(-(v2.getZ()), v2.getY())
			};

		} else if (order == SpacedRotationOrder.ZXY) {

			// r (SpacedVector3.plusJ) coordinates are :
			// -cos (phi) sin (psi), cos (phi) cos (psi), sin (phi)
			// (-r) (SpacedVector3.plusK) coordinates are :
			// -sin (theta) cos (phi), sin (phi), cos (theta) cos (phi)
			// and we can choose to have phi in the interval [-PI/2 ; +PI/2]
			SpacedVector3 v1 = applyTo(SpacedVector3.PLUS_J);
			SpacedVector3 v2 = applyInverseTo(SpacedVector3.PLUS_K);
			if ((v2.getY() < -0.9999999999) || (v2.getY() > 0.9999999999)) {
				throw new RuntimeException("CardanEulerSingularityException");
			}
			return new double[]{
					Math.atan2(-(v1.getX()), v1.getY()),
					Math.asin(v2.getY()),
					Math.atan2(-(v2.getX()), v2.getZ())
			};

		} else if (order == SpacedRotationOrder.ZYX) {

			// r (SpacedVector3.plusI) coordinates are :
			//  cos (theta) cos (psi), cos (theta) sin (psi), -sin (theta)
			// (-r) (SpacedVector3.plusK) coordinates are :
			// -sin (theta), sin (phi) cos (theta), cos (phi) cos (theta)
			// and we can choose to have theta in the interval [-PI/2 ; +PI/2]
			SpacedVector3 v1 = applyTo(SpacedVector3.PLUS_I);
			SpacedVector3 v2 = applyInverseTo(SpacedVector3.PLUS_K);
			if ((v2.getX() < -0.9999999999) || (v2.getX() > 0.9999999999)) {
				throw new RuntimeException("CardanEulerSingularityException");
			}
			return new double[]{
					Math.atan2(v1.getY(), v1.getX()),
					-Math.asin(v2.getX()),
					Math.atan2(v2.getY(), v2.getZ())
			};

		} else if (order == SpacedRotationOrder.XYX) {

			// r (SpacedVector3.plusI) coordinates are :
			//  cos (theta), sin (phi1) sin (theta), -cos (phi1) sin (theta)
			// (-r) (SpacedVector3.plusI) coordinates are :
			// cos (theta), sin (theta) sin (phi2), sin (theta) cos (phi2)
			// and we can choose to have theta in the interval [0 ; PI]
			SpacedVector3 v1 = applyTo(SpacedVector3.PLUS_I);
			SpacedVector3 v2 = applyInverseTo(SpacedVector3.PLUS_I);
			if ((v2.getX() < -0.9999999999) || (v2.getX() > 0.9999999999)) {
				throw new RuntimeException("CardanEulerSingularityException");
			}
			return new double[]{
					Math.atan2(v1.getY(), -v1.getZ()),
					Math.acos(v2.getX()),
					Math.atan2(v2.getY(), v2.getZ())
			};

		} else if (order == SpacedRotationOrder.XZX) {

			// r (SpacedVector3.plusI) coordinates are :
			//  cos (psi), cos (phi1) sin (psi), sin (phi1) sin (psi)
			// (-r) (SpacedVector3.plusI) coordinates are :
			// cos (psi), -sin (psi) cos (phi2), sin (psi) sin (phi2)
			// and we can choose to have psi in the interval [0 ; PI]
			SpacedVector3 v1 = applyTo(SpacedVector3.PLUS_I);
			SpacedVector3 v2 = applyInverseTo(SpacedVector3.PLUS_I);
			if ((v2.getX() < -0.9999999999) || (v2.getX() > 0.9999999999)) {
				throw new RuntimeException("CardanEulerSingularityException");
			}
			return new double[]{
					Math.atan2(v1.getZ(), v1.getY()),
					Math.acos(v2.getX()),
					Math.atan2(v2.getZ(), -v2.getY())
			};

		} else if (order == SpacedRotationOrder.YXY) {

			// r (SpacedVector3.plusJ) coordinates are :
			//  sin (theta1) sin (phi), cos (phi), cos (theta1) sin (phi)
			// (-r) (SpacedVector3.plusJ) coordinates are :
			// sin (phi) sin (theta2), cos (phi), -sin (phi) cos (theta2)
			// and we can choose to have phi in the interval [0 ; PI]
			SpacedVector3 v1 = applyTo(SpacedVector3.PLUS_J);
			SpacedVector3 v2 = applyInverseTo(SpacedVector3.PLUS_J);
			if ((v2.getY() < -0.9999999999) || (v2.getY() > 0.9999999999)) {
				throw new RuntimeException("CardanEulerSingularityException");
			}
			return new double[]{
					Math.atan2(v1.getX(), v1.getZ()),
					Math.acos(v2.getY()),
					Math.atan2(v2.getX(), -v2.getZ())
			};

		} else if (order == SpacedRotationOrder.YZY) {

			// r (SpacedVector3.plusJ) coordinates are :
			//  -cos (theta1) sin (psi), cos (psi), sin (theta1) sin (psi)
			// (-r) (SpacedVector3.plusJ) coordinates are :
			// sin (psi) cos (theta2), cos (psi), sin (psi) sin (theta2)
			// and we can choose to have psi in the interval [0 ; PI]
			SpacedVector3 v1 = applyTo(SpacedVector3.PLUS_J);
			SpacedVector3 v2 = applyInverseTo(SpacedVector3.PLUS_J);
			if ((v2.getY() < -0.9999999999) || (v2.getY() > 0.9999999999)) {
				throw new RuntimeException("CardanEulerSingularityException");
			}
			return new double[]{
					Math.atan2(v1.getZ(), -v1.getX()),
					Math.acos(v2.getY()),
					Math.atan2(v2.getZ(), v2.getX())
			};

		} else if (order == SpacedRotationOrder.ZXZ) {

			// r (SpacedVector3.plusK) coordinates are :
			//  sin (psi1) sin (phi), -cos (psi1) sin (phi), cos (phi)
			// (-r) (SpacedVector3.plusK) coordinates are :
			// sin (phi) sin (psi2), sin (phi) cos (psi2), cos (phi)
			// and we can choose to have phi in the interval [0 ; PI]
			SpacedVector3 v1 = applyTo(SpacedVector3.PLUS_K);
			SpacedVector3 v2 = applyInverseTo(SpacedVector3.PLUS_K);
			if ((v2.getZ() < -0.9999999999) || (v2.getZ() > 0.9999999999)) {
				throw new RuntimeException("CardanEulerSingularityException");
			}
			return new double[]{
					Math.atan2(v1.getX(), -v1.getY()),
					Math.acos(v2.getZ()),
					Math.atan2(v2.getX(), v2.getY())
			};

		} else { // last possibility is ZYZ

			// r (SpacedVector3.plusK) coordinates are :
			//  cos (psi1) sin (theta), sin (psi1) sin (theta), cos (theta)
			// (-r) (SpacedVector3.plusK) coordinates are :
			// -sin (theta) cos (psi2), sin (theta) sin (psi2), cos (theta)
			// and we can choose to have theta in the interval [0 ; PI]
			SpacedVector3 v1 = applyTo(SpacedVector3.PLUS_K);
			SpacedVector3 v2 = applyInverseTo(SpacedVector3.PLUS_K);
			if ((v2.getZ() < -0.9999999999) || (v2.getZ() > 0.9999999999)) {
				throw new RuntimeException("CardanEulerSingularityException");
			}
			return new double[]{
					Math.atan2(v1.getY(), v1.getX()),
					Math.acos(v2.getZ()),
					Math.atan2(v2.getY(), -v2.getX())
			};

		}

	}

	/**
	 * Get the 3X3 matrix corresponding to the instance
	 *
	 * @return the matrix corresponding to the instance
	 */
	public double[][] getMatrix() {

		// products
		double q0q0 = w * w;
		double q0q1 = w * x;
		double q0q2 = w * y;
		double q0q3 = w * z;
		double q1q1 = x * x;
		double q1q2 = x * y;
		double q1q3 = x * z;
		double q2q2 = y * y;
		double q2q3 = y * z;
		double q3q3 = z * z;

		// create the matrix
		double[][] m = new double[3][];
		m[0] = new double[3];
		m[1] = new double[3];
		m[2] = new double[3];

		m[0][0] = 2.0 * (q0q0 + q1q1) - 1.0;
		m[1][0] = 2.0 * (q1q2 - q0q3);
		m[2][0] = 2.0 * (q1q3 + q0q2);

		m[0][1] = 2.0 * (q1q2 + q0q3);
		m[1][1] = 2.0 * (q0q0 + q2q2) - 1.0;
		m[2][1] = 2.0 * (q2q3 - q0q1);

		m[0][2] = 2.0 * (q1q3 - q0q2);
		m[1][2] = 2.0 * (q2q3 + q0q1);
		m[2][2] = 2.0 * (q0q0 + q3q3) - 1.0;

		return m;

	}

	/**
	 * Apply the rotation to a vector.
	 *
	 * @param p1 vector to apply the rotation to
	 * @return a new vector which is the image of u by the rotation
	 */
	public SpacedVector3 applyTo(SpacedVector3 p1) {
		double zSq = z * z;
		double xSq = x * x;
		double ySQ = y * y;
		double wSq = w * w;
		double resultX = 2*( w *(y*p1.getZ() - z*p1.getY()) + x*(y*p1.getY() + z*p1.getZ())) - zSq *p1.getX() - ySQ *p1.getX() + wSq *p1.getX() + xSq *p1.getX();
		double resultY = 2*( y *(x*p1.getX() + z*p1.getZ()) + w*(z*p1.getX() - x*p1.getZ())) - xSq *p1.getY() + ySQ *p1.getY() - zSq *p1.getY() + wSq *p1.getY();
		double resultZ = 2*( z *(x*p1.getX() + y*p1.getY()) + w*(x*p1.getY() - y*p1.getX())) - xSq *p1.getZ() + wSq *p1.getZ() + zSq *p1.getZ() - ySQ *p1.getZ();
		return new SpacedVector3(resultX, resultY, resultZ);
	}

	/**
	 * Apply the inverse of the rotation to a vector.
	 *
	 * @param u vector to apply the inverse of the rotation to
	 * @return a new vector which such that u is its image by the rotation
	 */
	public SpacedVector3 applyInverseTo(SpacedVector3 u) {
		SpacedRotation inverse = getInverse();
		return inverse.applyTo(u);
	}

	private SpacedRotation getInverse() {
		SpacedRotation normalized = new SpacedRotation(x, y, z, w, true);
		return getConjugate(normalized);
	}

	private SpacedRotation getConjugate(SpacedRotation normalized) {
		return new SpacedRotation(-normalized.x, -normalized.y, -normalized.z, w);
	}

	/**
	 * Apply the instance to another r.
	 * Applying the instance to a r is computing the composition
	 * in an order compliant with the following rule : let u be any
	 * vector and v its image by r (i.e. r.applyTo(u) = v), let w be the image
	 * of v by the instance (i.e. applyTo(v) = w), then w = comp.applyTo(u),
	 * where comp = applyTo(r).
	 *
	 * @param r r to apply the r to
	 * @return a new r which is the composition of r by the instance
	 */
	public SpacedRotation applyTo(SpacedRotation r) {
		double resultX = x * r.getW() + y * r.getZ() - z * r.getY() + w * r.getX();
		double resultY = -x * r.getZ() + y * r.getW() + z * r.getX() + w * r.getY();
		double resultZ = x * r.getY() - y * r.getX() + z * r.getW() + w * r.getZ();
		double resultW = -x * r.getX() - y * r.getY() - z * r.getZ() + w * r.getW();
		return new SpacedRotation(resultX, resultY, resultZ, resultW, false);
	}

	/**
	 * Apply the inverse of the instance to another rotation.
	 * Applying the inverse of the instance to a rotation is computing
	 * the composition in an order compliant with the following rule :
	 * let u be any vector and v its image by r (i.e. r.applyTo(u) = v),
	 * let w be the inverse image of v by the instance
	 * (i.e. applyInverseTo(v) = w), then w = comp.applyTo(u), where
	 * comp = applyInverseTo(r).
	 *
	 * @param r rotation to apply the rotation to
	 * @return a new rotation which is the composition of r by the inverse
	 *         of the instance
	 */
	public SpacedRotation applyInverseTo(SpacedRotation r) {
		SpacedRotation inverse = getInverse();
		return inverse.applyTo(r);
	}

	/**
	 * Perfect orthogonality on a 3X3 matrix.
	 *
	 * @param m			initial matrix (not exactly orthogonal)
	 * @param threshold convergence threshold for the iterative
	 *                  orthogonality correction (convergence is reached when the
	 *                  difference between two steps of the Frobenius norm of the
	 *                  correction is below this threshold)
	 * @return an orthogonal matrix close to m
	 */
	private double[][] orthogonalizeMatrix(double[][] m, double threshold) {
		double[] m0 = m[0];
		double[] m1 = m[1];
		double[] m2 = m[2];
		double x00 = m0[0];
		double x01 = m0[1];
		double x02 = m0[2];
		double x10 = m1[0];
		double x11 = m1[1];
		double x12 = m1[2];
		double x20 = m2[0];
		double x21 = m2[1];
		double x22 = m2[2];
		double fn = 0;
		double fn1;

		double[][] o = new double[3][3];
		double[] o0 = o[0];
		double[] o1 = o[1];
		double[] o2 = o[2];

		// iterative correction: Xn+1 = Xn - 0.5 * (Xn.Mt.Xn - M)
		int i = 0;
		while (++i < 11) {

			// Mt.Xn
			double mx00 = m0[0] * x00 + m1[0] * x10 + m2[0] * x20;
			double mx10 = m0[1] * x00 + m1[1] * x10 + m2[1] * x20;
			double mx20 = m0[2] * x00 + m1[2] * x10 + m2[2] * x20;
			double mx01 = m0[0] * x01 + m1[0] * x11 + m2[0] * x21;
			double mx11 = m0[1] * x01 + m1[1] * x11 + m2[1] * x21;
			double mx21 = m0[2] * x01 + m1[2] * x11 + m2[2] * x21;
			double mx02 = m0[0] * x02 + m1[0] * x12 + m2[0] * x22;
			double mx12 = m0[1] * x02 + m1[1] * x12 + m2[1] * x22;
			double mx22 = m0[2] * x02 + m1[2] * x12 + m2[2] * x22;

			// Xn+1
			o0[0] = x00 - 0.5 * (x00 * mx00 + x01 * mx10 + x02 * mx20 - m0[0]);
			o0[1] = x01 - 0.5 * (x00 * mx01 + x01 * mx11 + x02 * mx21 - m0[1]);
			o0[2] = x02 - 0.5 * (x00 * mx02 + x01 * mx12 + x02 * mx22 - m0[2]);
			o1[0] = x10 - 0.5 * (x10 * mx00 + x11 * mx10 + x12 * mx20 - m1[0]);
			o1[1] = x11 - 0.5 * (x10 * mx01 + x11 * mx11 + x12 * mx21 - m1[1]);
			o1[2] = x12 - 0.5 * (x10 * mx02 + x11 * mx12 + x12 * mx22 - m1[2]);
			o2[0] = x20 - 0.5 * (x20 * mx00 + x21 * mx10 + x22 * mx20 - m2[0]);
			o2[1] = x21 - 0.5 * (x20 * mx01 + x21 * mx11 + x22 * mx21 - m2[1]);
			o2[2] = x22 - 0.5 * (x20 * mx02 + x21 * mx12 + x22 * mx22 - m2[2]);

			// correction on each elements
			double corr00 = o0[0] - m0[0];
			double corr01 = o0[1] - m0[1];
			double corr02 = o0[2] - m0[2];
			double corr10 = o1[0] - m1[0];
			double corr11 = o1[1] - m1[1];
			double corr12 = o1[2] - m1[2];
			double corr20 = o2[0] - m2[0];
			double corr21 = o2[1] - m2[1];
			double corr22 = o2[2] - m2[2];

			// Frobenius norm of the correction
			fn1 = corr00 * corr00 + corr01 * corr01 + corr02 * corr02 +
					corr10 * corr10 + corr11 * corr11 + corr12 * corr12 +
					corr20 * corr20 + corr21 * corr21 + corr22 * corr22;

			// convergence test
			if (Math.abs(fn1 - fn) <= threshold) {
				return o;
			}

			// prepare next iteration
			x00 = o0[0];
			x01 = o0[1];
			x02 = o0[2];
			x10 = o1[0];
			x11 = o1[1];
			x12 = o1[2];
			x20 = o2[0];
			x21 = o2[1];
			x22 = o2[2];
			fn = fn1;

		}

		// the algorithm did not converge after 10 iterations
		throw new RuntimeException(String.format("unable to orthogonalize matrix in %d iterations", i - 1));
	}

	/**
	 * Compute the <i>distance</i> between two rotations.
	 * <p>The <i>distance</i> is intended here as a way to check if two
	 * rotations are almost similar (i.e. they transform vectors the same way)
	 * or very different. It is mathematically defined as the angle of
	 * the rotation r that prepended to one of the rotations gives the other
	 * one:</p>
	 * <pre>
	 *        r<sub>1</sub>(r) = r<sub>2</sub>
	 * </pre>
	 * <p>This distance is an angle between 0 and &pi;. Its value is the smallest
	 * possible upper bound of the angle in radians between r<sub>1</sub>(v)
	 * and r<sub>2</sub>(v) for all possible vectors v. This upper bound is
	 * reached for some v. The distance is equal to 0 if and only if the two
	 * rotations are identical.</p>
	 * <p>Comparing two rotations should always be done using this value rather
	 * than for example comparing the components of the quaternions. It is much
	 * more stable, and has a geometric meaning. Also comparing quaternions
	 * components is error prone since for example quaternions (0.36, 0.48, -0.48, -0.64)
	 * and (-0.36, -0.48, 0.48, 0.64) represent exactly the same rotation despite
	 * their components are different (they are exact opposites).</p>
	 *
	 * @param r1 first rotation
	 * @param r2 second rotation
	 * @return <i>distance</i> between r1 and r2
	 */
	public static double distance(SpacedRotation r1, SpacedRotation r2) {
		return r1.applyInverseTo(r2).getAngle();
	}

	public static double dot(SpacedRotation r1, SpacedRotation r2) {
		return r1.getW() * r2.getW() + r1.getX() * r2.getX() + r1.getY() * r2.getY() + r1.getZ() * r2.getZ();
	}

	public SpacedRotation scalarMultiply(double scalar) {
		return new SpacedRotation(x * scalar, y * scalar, z * scalar, w * scalar);
	}

	public void toAxes(SpacedVector3[] axes) {
		if (axes.length < 3) {
            throw new IllegalArgumentException("axes array must have at least three elements");
        }
		double[][] matrix = getMatrix();
		axes[0] = new SpacedVector3(matrix[0][0], matrix[1][0], matrix[2][0]);
		axes[1] = new SpacedVector3(matrix[0][1], matrix[1][1], matrix[2][1]);
		axes[2] = new SpacedVector3(matrix[0][2], matrix[1][2], matrix[2][2]);
	}

	@Override
	public String toString() {
		return "SpacedRotation{" +
				"x=" + x +
				", y=" + y +
				", z=" + z +
				", w=" + w +
				'}';
	}


	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		SpacedRotation that = (SpacedRotation) o;

		if (Double.compare(that.w, w) != 0) {
			return false;
		}
		if (Double.compare(that.x, x) != 0) {
			return false;
		}
		if (Double.compare(that.y, y) != 0) {
			return false;
		}
		return Double.compare(that.z, z) == 0;
	}

	@Override
	public int hashCode() {
		int result;
		long temp;
		temp = w != +0.0d ? Double.doubleToLongBits(w) : 0L;
		result = (int) (temp ^ (temp >>> 32));
		temp = x != +0.0d ? Double.doubleToLongBits(x) : 0L;
		result = 31 * result + (int) (temp ^ (temp >>> 32));
		temp = y != +0.0d ? Double.doubleToLongBits(y) : 0L;
		result = 31 * result + (int) (temp ^ (temp >>> 32));
		temp = z != +0.0d ? Double.doubleToLongBits(z) : 0L;
		result = 31 * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	public Quaternion toQuaternion() {
		return new Quaternion(x, y, z, w);
	}

	@LuaMethod(name = "GetDirection")
	public void getDirection(ReturnValues returnValues) {
		SpacedVector3[] res = new SpacedVector3[3];
		toAxes(res);
		returnValues.push(res[0].getX(), res[0].getY(), res[0].getZ());
	}


}
