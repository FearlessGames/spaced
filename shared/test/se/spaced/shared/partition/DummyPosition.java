package se.spaced.shared.partition;

import com.ardor3d.math.Vector3;
import com.ardor3d.math.type.ReadOnlyVector3;

public class DummyPosition implements HasPosition {
	private final Vector3 vector;

	public DummyPosition(double x, double y, double z) {
		this(new Vector3(x, y, z));
	}

	public DummyPosition(Vector3 vector) {
		this.vector = vector;
	}

	@Override
	public ReadOnlyVector3 getPosition() {
		return vector;
	}

	public void setPosition(double x, double y, double z) {
		vector.set(x, y, z);
	}

	public void setPosition(ReadOnlyVector3 vector) {
		this.vector.set(vector);
	}

}
