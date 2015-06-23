package se.spaced.server.model.world;

import com.google.common.collect.Maps;
import se.fearless.common.time.TimeProvider;
import se.spaced.shared.world.TimeSystemInfo;

import java.util.Map;

public class TimeServiceImpl implements TimeService {

	private final TimeProvider timeProvider;
	private final Map<String, ZoneTimeInfo> zones = Maps.newHashMap();

	public TimeServiceImpl(TimeProvider timeProvider) {
		this.timeProvider = timeProvider;
	}

	@Override
	public void registerZone(String name, TimeSystemInfo timeSystemInfo) {
		zones.put(name, new ZoneTimeInfo(timeSystemInfo, timeProvider.now()));

	}

	@Override
	public TimeSystemInfo getDayInfo(String name) {
		ZoneTimeInfo zoneTimeInfo = zones.get(name);
		if (zoneTimeInfo == null) {
			throw new RuntimeException("Requesting current day offset for unknown zone " + name);
		}
		return zoneTimeInfo.timeInfo;
	}

	@Override
	public long getCurrentDayOffsetInMillis(String name) {
		ZoneTimeInfo zoneTimeInfo = zones.get(name);
		if (zoneTimeInfo == null) {
			throw new RuntimeException("Requesting current day offset for unknown zone " + name);
		}
		return (timeProvider.now() - zoneTimeInfo.initTimeStamp) % zoneTimeInfo.timeInfo.getCycleTimeInMillis();
	}

	private static class ZoneTimeInfo {
		final TimeSystemInfo timeInfo;
		final long initTimeStamp;

		ZoneTimeInfo(TimeSystemInfo timeInfo, long initTimeStamp) {
			this.timeInfo = timeInfo;
			this.initTimeStamp = initTimeStamp;
		}
	}
}
