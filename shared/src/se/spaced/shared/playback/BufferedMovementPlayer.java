package se.spaced.shared.playback;

import se.ardortech.math.SpacedRotation;
import se.ardortech.math.SpacedVector3;

public class BufferedMovementPlayer<T> {
	private static final double SPEED_UP_AT = 500; // ms
	private static final double SPEEDUP_FACTOR = 1.5;

	private static final double SLOW_DOWN_AT = 50; // ms
	private static final double SLOWDOWN_FACTOR = 0.5;

	private final MovementPlayer<T> player;

	private boolean ready;

	private long prev;
	private double playerTime;

	public BufferedMovementPlayer(PlaybackUpdater<T> playbackUpdater, MovementPoint<T> firstPoint) {
		this.player = new MovementPlayer<T>(playbackUpdater, firstPoint, firstPoint.timestamp);
	}

	public void addData(RecordingPoint<T> recordingPoint) {
		player.addData(recordingPoint);
	}

	public void step(long now) {
		if (!ready) {
			player.step(0);
			long startTime = player.getStartTime();
			if (startTime == 0) {
				return;
			}
			ready = true;
			playerTime = startTime;
			prev = now;
		}
		long diff = now - prev;
		prev = now;

		long endTime = player.getEndTime();
		double timeLeft = endTime - playerTime;
		playerTime += getPlaybackSpeed(timeLeft) * diff;

		// In case of lagged or missing packets, don't go past the end
		if (playerTime > endTime) {
			playerTime = endTime;
		}

		player.step((long) playerTime);
	}

	private double getPlaybackSpeed(double timeLeft) {
		if (timeLeft < SLOW_DOWN_AT) {
			return SLOWDOWN_FACTOR;
		} else if (timeLeft > SPEED_UP_AT) {
			return SPEEDUP_FACTOR;
		}
		return 1.0;
	}

	public SpacedVector3 position() {
		return player.position();
	}

	public SpacedRotation rotation() {
		return player.rotation();
	}

	public T state() {
		return player.state();
	}
}
