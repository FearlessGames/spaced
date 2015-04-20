package se.spaced.client.environment.time;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import se.spaced.shared.world.TimeSystemInfo;

@Singleton
public class HourMinuteGameTimeManager implements GameTimeManager {

	private TimeSystemInfo timeInfo;
	private long offset;

	@Inject
	public HourMinuteGameTimeManager(TimeSystemInfo timeInfo) {
		this.timeInfo = timeInfo;
	}


	@Override
	public GameTime fromSystemTime(long millis) {
		return new GameTime((millis - offset) % timeInfo.getCycleTimeInMillis());
	}

	@Override
	public GameTime getCycleTime() {
		return new GameTime(timeInfo.getCycleTimeInMillis());
	}

	@Override
	public double getDayFraction(GameTime t) {
		return (double) t.getValue() / timeInfo.getCycleTimeInMillis();
	}

	@Override
	public void setLocalTimeInfo(TimeSystemInfo timeSystemInfo, long currentTimeOffset, long now) {
		timeInfo = timeSystemInfo;
		offset = now - currentTimeOffset;
	}

	@Override
	public TimeSystemInfo getTimeInfo() {
		return timeInfo;
	}
}
