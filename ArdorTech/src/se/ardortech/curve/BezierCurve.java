package se.ardortech.curve;

import com.ardor3d.math.Matrix4;
import com.ardor3d.math.Vector3;

public class BezierCurve extends BaseCurve {

	private static final Matrix4 bezier = new Matrix4(-1.0f,  3.0f, -3.0f,  1.0f,
			 										   3.0f, -6.0f,  3.0f,  0.0f,
			 										  -3.0f,  3.0f,  0.0f,  0.0f,
			 										   1.0f,  0.0f,  0.0f,  0.0f);
	public BezierCurve() {
		super(bezier);
	}

	public BezierCurve(float[] constraints, int numConstraints) {
		super(bezier, constraints, numConstraints);
	}

	public BezierCurve(Vector3 ... constraints) {
		super(bezier, constraints);
	}

	@Override
	protected int calcNumSegments(int numConstraints) {
		return (numConstraints - 1) / 3;
	}

	@Override
	protected int getConstraintsOffset(int segment) {
		return segment * 3;
	}
}