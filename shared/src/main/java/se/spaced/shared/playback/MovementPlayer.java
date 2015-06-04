package se.spaced.shared.playback;

import se.ardortech.math.SpacedRotation;
import se.ardortech.math.SpacedVector3;

import java.util.Deque;
import java.util.LinkedList;

public class MovementPlayer<T> {
	private final Deque<MovementPoint<T>> points = new LinkedList<MovementPoint<T>>();
	private final PlaybackUpdater<T> playbackUpdater;

	private MovementPoint<T> currentFirst;
	public MovementPoint<T> currentSecond;
	private MovementPoint<T> latest;
	private long now;

	public MovementPlayer(PlaybackUpdater<T> playbackUpdater, MovementPoint<T> firstPoint, long now) {
		this.playbackUpdater = playbackUpdater;
		currentFirst = firstPoint;
		latest = currentFirst;
		this.now = now;
	}

	public void addPoint(MovementPoint<T> point) {
		latest = point;
		points.add(point);
	}

	public void addData(RecordingPoint<T> data) {
		addPoint(latest.apply(data));
	}

	public void step(long now) {
		if (currentSecond == null) {
			currentSecond = points.pollFirst();
			if (currentSecond == null) {
				return;
			}
		}

		if (now <= this.now) {
			return;
		}
		this.now = now;

		while (currentSecond.timestamp <= now) {
			MovementPoint<T> nextPoint = points.pollFirst();
			if (!currentFirst.state.equals(currentSecond.state)) {
				playbackUpdater.updateState(currentSecond.timestamp, currentSecond.state, currentSecond.position, currentSecond.rotation);
			}
			currentFirst = currentSecond;
			currentSecond = nextPoint;

			if (nextPoint == null) {
				break;
			}
		}

		playbackUpdater.updatePosition(now, position(), rotation());
	}

	public SpacedVector3 position() {
		if (currentSecond == null) {
			return currentFirst.position;
		}
		return MovementPoint.position(now, currentFirst, currentSecond);
	}

	public SpacedRotation rotation() {
		if (currentSecond == null) {
			return currentFirst.rotation;
		}
		return MovementPoint.rotation(now, currentFirst, currentSecond);
	}

	public T state() {
		return currentFirst.state;
	}

	public long getEndTime() {
		return latest.timestamp;
	}

	public long getStartTime() {
		return currentFirst.timestamp;
	}
}
