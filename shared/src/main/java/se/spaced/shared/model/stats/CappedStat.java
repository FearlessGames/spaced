package se.spaced.shared.model.stats;

import se.fearlessgames.common.publisher.Subscriber;

public class CappedStat extends SimpleStat implements Subscriber<Stat> {
	private final AbstractStat limiter;
	private double maxValue;

	public CappedStat(String name, double startValue, AbstractStat limiter) {
		super(name, startValue);
		this.limiter = limiter;
		this.limiter.subscribe(this);
		updateMaxValue();
	}

	private void updateMaxValue() {
		maxValue = limiter.getValue();
		if (getValue() > maxValue) {
			changeValue(maxValue);
		}
	}

	@Override
	public void changeValue(double newValue) {
		super.changeValue(Math.min(newValue, maxValue));
	}

	@Override
	public void update(Stat arg) {
		updateMaxValue();
	}

	@Override
	public String toString() {
		return "CappedStat{" +
				"limiter=" + limiter +
				", maxValue=" + maxValue +
				'}';
	}
}
