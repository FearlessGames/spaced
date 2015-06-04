package se.ardortech.math;

import com.ardor3d.math.MathUtils;
import com.ardor3d.math.Quaternion;
import com.ardor3d.math.type.ReadOnlyQuaternion;

public class Rotations {

	private Rotations() {
	}

	public static SpacedRotation fromArdor(ReadOnlyQuaternion ardor) {
		return new SpacedRotation(ardor.getX(), ardor.getY(), ardor.getZ(), ardor.getW());
	}

	public static Quaternion fromSpaced(SpacedRotation spaced) {
		return new Quaternion(spaced.getX(), spaced.getY(), spaced.getZ(), spaced.getW());
	}

	public static SpacedRotation fromEulerAngles(double heading, double attitude, double bank) {
		double angle = heading * 0.5;
		final double sinHeading = MathUtils.sin(angle);
		final double cosHeading = MathUtils.cos(angle);
		angle = attitude * 0.5;
		final double sinAttitude = MathUtils.sin(angle);
		final double cosAttitude = MathUtils.cos(angle);
		angle = bank * 0.5;
		final double sinBank = MathUtils.sin(angle);
		final double cosBank = MathUtils.cos(angle);

		// variables used to reduce multiplication calls.
		final double cosHeadingXcosAttitude = cosHeading * cosAttitude;
		final double sinHeadingXsinAttitude = sinHeading * sinAttitude;
		final double cosHeadingXsinAttitude = cosHeading * sinAttitude;
		final double sinHeadingXcosAttitude = sinHeading * cosAttitude;

		final double w = (cosHeadingXcosAttitude * cosBank - sinHeadingXsinAttitude * sinBank);
		final double x = (cosHeadingXcosAttitude * sinBank + sinHeadingXsinAttitude * cosBank);
		final double y = (sinHeadingXcosAttitude * cosBank + cosHeadingXsinAttitude * sinBank);
		final double z = (cosHeadingXsinAttitude * cosBank - sinHeadingXcosAttitude * sinBank);

		return new SpacedRotation(x, y, z, w, true);
	}
}