package se.spaced.shared.util.math;


public class LinearTimeValue {
	private double maxValue;

	private double lastUpdatedValue;
	private double currentRate;
	private long lastUpdatedTimestamp;

	public LinearTimeValue(double maxValue) {
		this.maxValue = maxValue;
	}

	public void reset(long now) {
		lastUpdatedTimestamp = now;
		lastUpdatedValue = getMaxValue();
	}

	public boolean isReady(long now, double cost) {
		double t = getValue(now);
		return t >= cost;
	}

	public double getValue(long now) {
		double t = lastUpdatedValue + currentRate * (now - lastUpdatedTimestamp);
		return Math.max(Math.min(maxValue, t), 0);
	}

	public boolean consume(long now, double cost) {
		if (isReady(now, cost)) {
			double t = getValue(now);
			lastUpdatedTimestamp = now;
			lastUpdatedValue = t - cost;
			return true;
		}
		return false;
	}

	public double getMaxValue() {
		return maxValue;
	}

	public void setCurrentRate(long now, double rate) {
		lastUpdatedValue = getValue(now);
		lastUpdatedTimestamp = now;
		currentRate = rate / 1000;
	}

	public double getCurrentRate() {
		return currentRate * 1000;
	}

	public void setValue(long now, double value) {
		lastUpdatedTimestamp = now;
		lastUpdatedValue = Math.max(Math.min(value, getMaxValue()), 0.0);
	}

	public void setMaxValue(long now, double maxValue) {
		double current = Math.min(maxValue, getValue(now));
		setValue(now, current);
		this.maxValue = maxValue;
	}
}
