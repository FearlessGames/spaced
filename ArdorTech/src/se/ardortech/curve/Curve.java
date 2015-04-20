package se.ardortech.curve;

import com.ardor3d.math.Matrix4;
import com.ardor3d.math.Vector3;
import com.ardor3d.math.Vector4;

public abstract class Curve {

	public enum EvaluationMethod {
		BruteForce,
		Horners
	}

	private final Matrix4 base;

	protected Curve(Matrix4 base) {
		this.base = base;
	}

	protected Vector4 get(float t, EvaluationMethod evaluationMethod) {
		if (base == null) {
			return new Vector4(0.0f, 0.0f, 0.0f, 0.0f);
		}

		Vector4 result = new Vector4();
		// ****************************************
		// Blend Function
		// ****************************************
		if (evaluationMethod == EvaluationMethod.BruteForce) {
			// ****************************************
			// Brute Force
			// ****************************************
			Vector4 vT = new Vector4();
			vT.setW(1);
			vT.setZ(t);
			vT.setY(t * t);
			vT.setX(vT.getY() * t);

			base.applyPost(vT, result);
		} else if (evaluationMethod == EvaluationMethod.Horners) {
			// ****************************************
			// Horner's Rule
			// ****************************************
			result.setX(( ( t * data(0) + data(4) ) * t + data( 8) ) * t + data(12));
			result.setY(( ( t * data(1) + data(5) ) * t + data( 9) ) * t + data(13));
			result.setZ(( ( t * data(2) + data(6) ) * t + data(10) ) * t + data(14));
			result.setW(( ( t * data(3) + data(7) ) * t + data(11) ) * t + data(15));
		}
		return result;
	}

	protected Vector4 getTangent(float t, EvaluationMethod evaluationMethod) {
		if (base == null) {
			return new Vector4(0.0f, 0.0f, 0.0f, 0.0f);
		}

		Vector4 result = new Vector4();

		// ****************************************
		// Blend Function
		// ****************************************
		if (evaluationMethod == EvaluationMethod.BruteForce) {
			// ****************************************
			// Brute Force
			// ****************************************
			float f3T2 = 3 * t * t;
			float f2T = 2 * t;
			result.setX(data(0) * f3T2 + data(4) * f2T + data( 8));
			result.setY(data(1) * f3T2 + data(5) * f2T + data( 9));
			result.setZ(data(2) * f3T2 + data(6) * f2T + data(10));
			result.setW(data(3) * f3T2 + data(7) * f2T + data(11));
		} else if (evaluationMethod == EvaluationMethod.Horners) {
			// ****************************************
			// Horner's Rule
			// ****************************************
			float f3T = 3 * t;
			result.setX(( data(0) * f3T + data(4) * 2 ) * t + data( 8));
			result.setY(( data(1) * f3T + data(5) * 2 ) * t + data( 9));
			result.setZ(( data(2) * f3T + data(6) * 2 ) * t + data(10));
			result.setW(( data(3) * f3T + data(7) * 2 ) * t + data(11));
		}
		return result;
	}

	// Usage of matrix is a bit bloated, slow utility method to minimize code
	private double data(int index) {
		return base.getValue(index % 4, index / 4);
	}

	public abstract Vector3 get(float t, int segment, EvaluationMethod evaluationMethod);
	public abstract Vector3 getTangent(float t, int segment, EvaluationMethod evaluationMethod);
	protected abstract int calcNumSegments(int numConstraints);
	protected abstract int getConstraintsOffset(int segment);
	public abstract int getNumSegments();
}
