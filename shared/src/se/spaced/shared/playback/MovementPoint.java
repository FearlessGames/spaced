package se.spaced.shared.playback;

import se.ardortech.math.SpacedRotation;
import se.ardortech.math.SpacedVector3;
import se.ardortech.math.VectorMath;

public class MovementPoint<T> {
	public final long timestamp;
	public final SpacedVector3 position;
	public final SpacedRotation rotation;
	public final T state;

	public MovementPoint(long timestamp, T state, SpacedVector3 position, SpacedRotation rotation) {
		this.timestamp = timestamp;
		this.state = state;
		this.position = position;
		this.rotation = rotation;
	}

	@Override
	public String toString() {
		return "(" +
				"timestamp=" + timestamp +
				", position=" + position +
				", rotation=" + rotation +
				", state=" + state +
				')';
	}

	public boolean samePositional(MovementPoint<T> other) {
		if (!state.equals(other.state)) {
			return false;
		}
		if (!position.equals(other.position)) {
			return false;
		}
		if (!rotation.equals(other.rotation)) {
			return false;
		}
		return true;
	}

	private static <T> double getFactor(long t, MovementPoint<T> from, MovementPoint<T> to) {
		long diff = to.timestamp - from.timestamp;
		if (diff == 0) {
			return 0;
		}
		if (t <= from.timestamp) {
			return 0;
		}
		if (t >= to.timestamp) {
			return 1;
		}
		return ((double) t - from.timestamp) / diff;
	}

	public static <T> SpacedVector3 position(long t, MovementPoint<T> from, MovementPoint<T> to) {
		return VectorMath.lerp(from.position, to.position, getFactor(t, from, to));
	}

	public static <T> SpacedRotation rotation(long t, MovementPoint<T> from, MovementPoint<T> to) {
		return VectorMath.slerp(from.rotation, to.rotation, getFactor(t, from, to));
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		MovementPoint that = (MovementPoint) o;

		if (timestamp != that.timestamp) {
			return false;
		}
		if (!position.equals(that.position)) {
			return false;
		}
		if (!rotation.equals(that.rotation)) {
			return false;
		}
		if (!state.equals(that.state)) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		int result = (int) (timestamp ^ (timestamp >>> 32));
		result = 31 * result + position.hashCode();
		result = 31 * result + rotation.hashCode();
		result = 31 * result + state.hashCode();
		return result;
	}

	public MovementPoint<T> apply(RecordingPoint<T> data) {
		long endTime = this.timestamp + data.delta;
		T state = data.sameState ? this.state : data.state;
		SpacedVector3 position = readPosition(data);
		SpacedRotation rotation = data.sameRotation ? this.rotation : data.rotation;
		return new MovementPoint<T>(endTime, state, position, rotation);
	}

	private SpacedVector3 readPosition(RecordingPoint<T> data) {
		if (data.samePosition) {
			return position;
		}
		if (data.exactPos) {
			return data.position;
		}
		double max = Math.pow(2, data.size);
		SpacedVector3 diff = new SpacedVector3(data.xi * max / 127, data.yi * max / 127, data.zi * max / 127);
		return position.add(diff);
	}

}
