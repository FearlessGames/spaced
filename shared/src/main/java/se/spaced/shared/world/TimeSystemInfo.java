package se.spaced.shared.world;

public class TimeSystemInfo {
	private static final int MILLIS_PER_SECOND = 1000;
	private final int hoursPerDay;
	private final int minutesPerHour;
	private final int secondsPerMinute;
	private final double speedFactor;

	public TimeSystemInfo(int hoursPerDay, int minutesPerHour, int secondsPerMinute, double speedFactor) {
		if (speedFactor == 0) {
			throw new IllegalArgumentException("Speed factor can't be 0");
		}
		this.hoursPerDay = hoursPerDay;
		this.minutesPerHour = minutesPerHour;
		this.secondsPerMinute = secondsPerMinute;
		this.speedFactor = speedFactor;
	}

	public int getHoursPerDay() {
		return hoursPerDay;
	}

	public int getMinutesPerHour() {
		return minutesPerHour;
	}

	public int getSecondsPerMinute() {
		return secondsPerMinute;
	}

	public double getSpeedFactor() {
		return speedFactor;
	}


	public long getCycleTimeInMillis() {
		return (long) (hoursPerDay * minutesPerHour * secondsPerMinute * MILLIS_PER_SECOND * speedFactor);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		TimeSystemInfo that = (TimeSystemInfo) o;

		if (hoursPerDay != that.hoursPerDay) {
			return false;
		}
		if (minutesPerHour != that.minutesPerHour) {
			return false;
		}
		if (secondsPerMinute != that.secondsPerMinute) {
			return false;
		}
		if (Double.compare(that.speedFactor, speedFactor) != 0) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		int result = hoursPerDay;
		result = 31 * result + minutesPerHour;
		result = 31 * result + secondsPerMinute;
		long temp = speedFactor != +0.0d ? Double.doubleToLongBits(speedFactor) : 0L;
		result = 31 * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	@Override
	public String toString() {
		return "TimeSystemInfo{" +
				"hoursPerDay=" + hoursPerDay +
				", minutesPerHour=" + minutesPerHour +
				", secondsPerMinute=" + secondsPerMinute +
				", speedFactor=" + speedFactor +
				'}';
	}
}
