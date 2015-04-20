package se.spaced.shared.playback;

import se.ardortech.math.SpacedRotation;
import se.ardortech.math.SpacedVector3;

public class MovementRecorder<T> {
	private static final int EXACT_PERIOD = 5*1000; // milliseconds

	private static final double MAX_LENGTH = 100.0;
	private static final double SPLIT_FACTOR = 1.10;

	private static final int MAX_TIME = 255;

	private final PlaybackTransmitter<T> transmitter;

	private double pathLength;
	private T pathStartState;
	private SpacedVector3 pathStartPos;
	private SpacedRotation pathStartRot;

	private long pathStartTimestamp;
	private T pathEndState;
	private SpacedVector3 pathEndPos;
	private SpacedRotation pathEndRot;
	private long pathEndTimestamp;

	private int lastTransferMillis = EXACT_PERIOD;

	public MovementRecorder(PlaybackTransmitter<T> transmitter,
									SpacedVector3 startPosition,
									SpacedRotation startRotation,
									T startState,
									long timestamp) {
		this.transmitter = transmitter;
		pathLength = 0;
		pathStartPos = startPosition;
		pathEndPos = startPosition;
		pathStartRot = startRotation;
		pathEndRot = startRotation;
		pathStartTimestamp = timestamp;
		pathEndTimestamp = timestamp;
		pathStartState = startState;
		pathEndState = startState; 
	}

	public synchronized void add(long timestamp, SpacedVector3 position, SpacedRotation rotation, T state) {
		if (timestamp - pathStartTimestamp > MAX_TIME) {
			closePath();
		}

		double distance = pathEndPos.distance(position);
		if (pathLength + distance > SPLIT_FACTOR * pathStartPos.distance(position)) {
			closePath();
		}

		if (!state.equals(pathEndState)) {
			closePath();
		}

		// append to path
		pathLength += distance;
		pathEndPos = position;
		pathEndRot = rotation;
		pathEndTimestamp = timestamp;
		pathEndState = state;

		if (!state.equals(pathStartState)) {
			closePath();

			pathLength = 0;
			pathEndPos = position;
			pathEndRot = rotation;
			pathEndTimestamp = timestamp;
			pathEndState = state;
		}
	}

	public void forceSend() {
		closePath();
	}

	private void closePath() {
		int delta = (int) (pathEndTimestamp - pathStartTimestamp);

		// workaround if add was called infrequently (paused process or something similar)
		if (delta > MAX_TIME) {
			delta = MAX_TIME;
		}

		boolean stateUnchanged = pathStartState.equals(pathEndState);
		boolean posUnchanged = pathStartPos.equals(pathEndPos);
		boolean rotUnchanged = pathStartRot.equals(pathEndRot);
		boolean exactPos = false;

		int xi = 0;
		int yi = 0;
		int zi = 0;
		int size = 0;

		if (!posUnchanged) {
			SpacedVector3 v = pathEndPos.subtract(pathStartPos);

			double x = v.getX();
			double y = v.getY();
			double z = v.getZ();
			double absx = Math.abs(x);
			double absy = Math.abs(y);
			double absz = Math.abs(z);

			double max = Math.max(absx, Math.max(absy, absz));

			lastTransferMillis += delta;
			if (max >= MAX_LENGTH || lastTransferMillis >= EXACT_PERIOD) {
				exactPos = true;
				lastTransferMillis = 0;
			} else {
				exactPos = false;

				// 2^(size-1) < max <= 2^size
				size = (int) Math.ceil(Math.log(max) / Math.log(2));
				max = Math.pow(2, size);
				xi = (int) (127 * x / max);
				yi = (int) (127 * y / max);
				zi = (int) (127 * z / max);

				// Recalculate end point to minimize value drift
				SpacedVector3 diff = new SpacedVector3(xi * max / 127, yi * max / 127, zi * max / 127);
				pathEndPos = pathStartPos.add(diff);
			}
		}

		RecordingPoint<T> point
				= new RecordingPoint<T>(
				delta,
				pathEndState, stateUnchanged,
				pathEndRot, rotUnchanged,
				posUnchanged,
				pathEndPos, size, xi, yi, zi, exactPos);
		transmitter.transmit(point);

		// reset path
		pathStartTimestamp = pathEndTimestamp;
		pathStartPos = pathEndPos;
		pathStartRot = pathEndRot;
		pathStartState = pathEndState;
		pathLength = 0;
	}

	public T state() {
		return pathEndState;
	}
}
