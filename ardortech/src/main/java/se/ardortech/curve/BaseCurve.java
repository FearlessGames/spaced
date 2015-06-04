package se.ardortech.curve;

import com.ardor3d.math.Matrix4;
import com.ardor3d.math.Vector3;
import com.ardor3d.math.type.ReadOnlyVector4;

public abstract class BaseCurve extends Curve {
	private float[] constraints;
	private int numConstraints;
	private int numSegments;

	protected BaseCurve(Matrix4 base) {
		super(base);
		freeConstraints();
	}

	protected BaseCurve(Matrix4 base, float[] constraints, int numConstraints) {
		this(base);
		setConstraints(constraints, numConstraints);
	}

	protected BaseCurve(Matrix4 base, Vector3 ... constraints) {
		this(base);
		setConstraints(constraints);
	}

	private void freeConstraints() {
		numConstraints = 0;
		constraints = null;
	}

	public final void setConstraints(Vector3 ... points) {
		freeConstraints();
		if (points.length > 0) {
			numConstraints = points.length;
			constraints = new float[numConstraints * 4];
			numSegments = calcNumSegments(numConstraints);
			for (int i = 0; i < numConstraints; i++) {
				constraints[i * 4    ] = points[i].getXf();
				constraints[i * 4 + 1] = points[i].getYf();
				constraints[i * 4 + 2] = points[i].getZf();
				constraints[i * 4 + 3] = 0;
			}
		}
	}

	public final void setConstraints(float[] constraints, int numConstraints) {
		freeConstraints();
		if (numConstraints > 0) {
			this.numConstraints = numConstraints;
			this.constraints = new float[numConstraints * 4];
			numSegments = calcNumSegments(numConstraints);
			System.arraycopy(constraints, 0, this.constraints, 0, this.numConstraints * 4);
		}
	}

	private Vector3 vectorMatrixMultiply3(ReadOnlyVector4 a, float[] matrix, int o) {
		Vector3 result = new Vector3();
		result.setX(a.getX() * matrix[o + 0] + a.getY() * matrix[o + 4] + a.getZ() * matrix[o +  8] + a.getW() * matrix[o + 12]);
		result.setY(a.getX() * matrix[o + 1] + a.getY() * matrix[o + 5] + a.getZ() * matrix[o +  9] + a.getW() * matrix[o + 13]);
		result.setZ(a.getX() * matrix[o + 2] + a.getY() * matrix[o + 6] + a.getZ() * matrix[o + 10] + a.getW() * matrix[o + 14]);
		return result;
	}

	@Override
	public Vector3 get(float t, int segment, EvaluationMethod evaluationMethod) {
		return new Vector3(vectorMatrixMultiply3(get(t, evaluationMethod), constraints, getConstraintsOffset(segment) * 4));
	}

	@Override
	public Vector3 getTangent(float t, int segment, EvaluationMethod evaluationMethod) {
		return new Vector3(vectorMatrixMultiply3(getTangent(t, evaluationMethod), constraints, getConstraintsOffset(segment) * 4));
	}

	@Override
	public int getNumSegments() {
		return numSegments;
	}
}