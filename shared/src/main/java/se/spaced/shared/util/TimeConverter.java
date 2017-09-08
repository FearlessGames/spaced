package se.spaced.shared.util;

public enum TimeConverter {
	ONE_MINUTE(60000), FIVE_MINUTES(300000), THIRTY_MINUTES(1800000);
	private final long millis;

	TimeConverter(long millis) {
		this.millis = millis;
	}

	public long getTimeInMillis() {
		return millis;
	}

}
