package se.spaced.client.environment.time;

import se.spaced.shared.world.TimeSystemInfo;

public interface GameTimeManager {

	GameTime fromSystemTime(long millis);

	GameTime getCycleTime();

	double getDayFraction(GameTime t);

	void setLocalTimeInfo(TimeSystemInfo timeSystemInfo, long currentTimeOffset, long now);

	TimeSystemInfo getTimeInfo();
}
