package se.spaced.shared.playback;

import se.ardortech.math.SpacedRotation;
import se.ardortech.math.SpacedVector3;

public class RecordingPoint<T> {
	public final int delta;

	public final boolean sameState;
	public final T state;

	public final boolean sameRotation;
	public final SpacedRotation rotation;

	public final boolean samePosition;

	public final boolean exactPos;

	public final SpacedVector3 position;
	public final int size;
	public final int xi;
	public final int yi;
	public final int zi;

	public RecordingPoint(
			int delta,
			T state,
			boolean sameState,
			SpacedRotation rotation,
			boolean sameRotation,
			boolean samePosition, SpacedVector3 position,
			int size, int xi, int yi, int zi, boolean exactPos) {

		this.delta = delta;
		this.state = state;
		this.sameState = sameState;
		this.rotation = rotation;
		this.sameRotation = sameRotation;
		this.samePosition = samePosition;
		this.position = position;
		this.size = size;
		this.xi = xi;
		this.yi = yi;
		this.zi = zi;
		this.exactPos = exactPos;
	}

	@Override
	public String toString() {
		return "RecordingPoint{" +
				"delta=" + delta +
				", sameState=" + sameState +
				", state=" + state +
				", sameRotation=" + sameRotation +
				", rotation=" + rotation +
				", samePosition=" + samePosition +
				", exactPos=" + exactPos +
				", position=" + position +
				", size=" + size +
				", xi=" + xi +
				", yi=" + yi +
				", zi=" + zi +
				'}';
	}
}
