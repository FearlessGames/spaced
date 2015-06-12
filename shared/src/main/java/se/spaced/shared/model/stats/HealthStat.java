package se.spaced.shared.model.stats;

import se.fearless.common.publisher.Subscriber;
import se.fearless.common.time.TimeProvider;
import se.spaced.shared.util.math.LinearTimeValue;

public class HealthStat extends SimpleStat implements Subscriber<Stat> {
	private static final String NAME = "health";
	
	private final LinearTimeValue regenTimer;
	private final TimeProvider timeProvider;
	private final Stat regenRate;
	private final Stat maxHealth;

	//startValue isnt actually used. Should probably clean up a bit in structure here
	public HealthStat(TimeProvider timeProvider, double startValue, AbstractStat maxHealth, AbstractStat regenRate) {
		super(NAME, startValue);
		this.regenRate = regenRate;
		this.timeProvider = timeProvider;
		this.maxHealth = maxHealth;

		regenTimer = new LinearTimeValue(maxHealth.getValue());
		regenTimer.setValue(timeProvider.now(), maxHealth.getValue());
		regenTimer.setCurrentRate(timeProvider.now(), regenRate.getValue());

		maxHealth.subscribe(this);
		regenRate.subscribe(this);
	}

	@Override
	public double getValue() {
		return regenTimer.getValue(timeProvider.now());
	}

	@Override
	public void changeValue(double value) {
		regenTimer.setValue(timeProvider.now(), value);
		publisher.updateSubscribers(this);
	}

	@Override
	public void update(Stat arg) {
		regenTimer.setMaxValue(timeProvider.now(), maxHealth.getValue());
		regenTimer.setCurrentRate(timeProvider.now(), regenRate.getValue());
		publisher.updateSubscribers(this);
	}

	@Override
	public String toString() {
		return "HealthStat{" +
				"currentValue=" + getValue() +
				'}';
	}
}
