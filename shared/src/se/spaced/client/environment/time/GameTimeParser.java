package se.spaced.client.environment.time;

import se.spaced.shared.world.TimeSystemInfo;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GameTimeParser {
	static final Pattern TIME_PATTERN = Pattern.compile("^(\\d\\d):(\\d\\d)$");

	public static GameTime parse(String s, TimeSystemInfo timeInfo) {
		Matcher matcher = TIME_PATTERN.matcher(s);
		if (matcher.matches()) {
			String hourString = matcher.group(1);
			String minuteString = matcher.group(2);
			int hours = Integer.parseInt(hourString);
			if (hours >= timeInfo.getHoursPerDay()) {
				throw new IllegalArgumentException("Out of range: " + s);
			}
			int minutes = Integer.parseInt(minuteString);
			if (minutes >= timeInfo.getMinutesPerHour()) {
				throw new IllegalArgumentException("Out of range: " + s);
			}
			long millisFromMinutes = 1000 * timeInfo.getSecondsPerMinute() * minutes;
			long millisFromHours = 1000 * timeInfo.getSecondsPerMinute() * timeInfo.getMinutesPerHour() * hours;
			return new GameTime((long) ((millisFromHours + millisFromMinutes) * timeInfo.getSpeedFactor()));
		}
		throw new IllegalArgumentException("Could not parse: " + s);
	}

	public static String toString(GameTime t, TimeSystemInfo timeInfo) {
		long millis = t.getValue();
		long millisPerMinute = (long) (timeInfo.getSecondsPerMinute() * 1000 * timeInfo.getSpeedFactor());
		int millisPerHour = (int) (timeInfo.getMinutesPerHour() * millisPerMinute);
		long hours = millis / millisPerHour;
		long minutes = ((millis - (hours * millisPerHour)) % millisPerHour) / millisPerMinute;

		return String.format("%02d:%02d", hours, minutes);
	}
}
