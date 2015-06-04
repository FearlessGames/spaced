package se.ardortech.curve;

import com.ardor3d.math.Matrix4;

public class BSplineCurve extends BaseCurve {

	private static final Matrix4 bSpline = new Matrix4(-1.0f,  3.0f, -3.0f,  1.0f,
            											3.0f, -6.0f,  3.0f,  0.0f,
            										   -3.0f,  0.0f,  3.0f,  0.0f,
            											1.0f,  4.0f,  1.0f,  0.0f);
	public BSplineCurve() {
		super(bSpline);
	}

	public BSplineCurve(float[] constraints, int numConstraints) {
		super(bSpline, constraints, numConstraints);
	}

	@Override
	protected int calcNumSegments(int numConstraints) {
		return numConstraints - 3;
	}

	@Override
	protected int getConstraintsOffset(int segment) {
		return segment;
	}
}