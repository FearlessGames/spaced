package se.spaced.server.model.world;

import se.spaced.shared.world.TimeSystemInfo;

public interface TimeService {
	void registerZone(String name, TimeSystemInfo timeSystemInfo);

	TimeSystemInfo getDayInfo(String name);

	long getCurrentDayOffsetInMillis(String name);
}
