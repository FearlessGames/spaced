package se.ardortech.math;

import com.ardor3d.math.Vector3;
import com.ardor3d.math.type.ReadOnlyVector3;

public class Vectors {

	private Vectors() {
	}

	public static SpacedVector3 fromArdor(ReadOnlyVector3 ardor) {
		return new SpacedVector3(ardor.getX(), ardor.getY(), ardor.getZ());
	}

	public static Vector3 fromSpaced(SpacedVector3 spacedVector3) {
		return new Vector3(spacedVector3.getX(), spacedVector3.getY(), spacedVector3.getZ());
	}

	public static SpacedVector3 setY(SpacedVector3 vector, double y) {
		return new SpacedVector3(vector.getX(), y, vector.getZ());
	}

	public static SpacedVector3 setX(SpacedVector3 vector, double x) {
		return new SpacedVector3(x, vector.getY(), vector.getZ());
	}

	public static SpacedVector3 setZ(SpacedVector3 vector, double z) {
		return new SpacedVector3(vector.getX(), vector.getY(), z);
	}
}
