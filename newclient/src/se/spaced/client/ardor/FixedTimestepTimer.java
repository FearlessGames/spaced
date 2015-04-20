package se.spaced.client.ardor;

import com.ardor3d.util.ReadOnlyTimer;

public class FixedTimestepTimer implements ReadOnlyTimer {

	private final ReadOnlyTimer masterTimer;

	private static final long TIMER_RESOLUTION = 1000000000L;
	private static final double INVERSE_TIMER_RESOLUTION = 1.0 / TIMER_RESOLUTION;

	// The real time since start, never expose
	private long accumulatedNanos;
	private long lastMasterTime;

	// Fake stepped total time
	private long steppedTotalTime;

	// How long each fixed timestep is
	private final long fixedTimeStepNanos;

	// Cached time step variables for speed
	private final double tpf;
	private final double fps;

	public FixedTimestepTimer(ReadOnlyTimer masterTimer, long fixedTimeStepNanos) {
		this.masterTimer = masterTimer;
		this.lastMasterTime = masterTimer.getTime();
		this.fixedTimeStepNanos = fixedTimeStepNanos;

		tpf = fixedTimeStepNanos * INVERSE_TIMER_RESOLUTION;
		fps = 1.0 / tpf;
	}

	@Override
	public double getTimeInSeconds() {
		return getTime() * INVERSE_TIMER_RESOLUTION;
	}

	@Override
	public long getTime() {
		return steppedTotalTime;
	}

	@Override
	public long getResolution() {
		return TIMER_RESOLUTION;
	}

	@Override
	public double getFrameRate() {
		return fps;
	}

	@Override
	public double getTimePerFrame() {
		return tpf;
	}

	public double getFrameFraction() {
		return (double) (accumulatedNanos) / (double) fixedTimeStepNanos;
	}

	public boolean update(ReadOnlyTimer realTimer) {
		// Nom the actual real time
		long currentMasterTime = masterTimer.getTime();
		accumulatedNanos += currentMasterTime - lastMasterTime;
		lastMasterTime = currentMasterTime;

		// Step forward the clock if it's time.
		if (accumulatedNanos > fixedTimeStepNanos) {
			accumulatedNanos -= fixedTimeStepNanos;
			steppedTotalTime += fixedTimeStepNanos;
			return true;
		}
		return false;
	}
}
