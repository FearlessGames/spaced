package se.ardortech.curve;

import com.ardor3d.math.Matrix4;

public class HermiteCurve extends BaseCurve {

	private static final Matrix4 hermite = new Matrix4( 2.0f, -2.0f,  1.0f,  1.0f,
            										   -3.0f,  3.0f, -2.0f, -1.0f,
            										    0.0f,  0.0f,  1.0f,  0.0f,
            										    1.0f,  0.0f,  0.0f,  0.0f);
	public HermiteCurve() {
		super(hermite);
	}

	public HermiteCurve(float[] constraints, int numConstraints) {
		super(hermite, constraints, numConstraints);
	}

	@Override
	protected int calcNumSegments(int numConstraints) {
		return numConstraints / 4;
	}

	@Override
	protected int getConstraintsOffset(int segment) {
		return segment * 4;
	}
}