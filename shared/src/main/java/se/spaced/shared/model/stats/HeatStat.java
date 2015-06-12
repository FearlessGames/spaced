package se.spaced.shared.model.stats;

import se.fearless.common.publisher.Subscriber;
import se.fearless.common.time.TimeProvider;
import se.spaced.shared.util.math.LinearTimeValue;

public class HeatStat extends AbstractStat implements Subscriber<Stat> {

	private final LinearTimeValue coolness;
	private final TimeProvider timeProvider;
	private static final String NAME = "heat";
	private final Stat maxHeat;
	private final Stat coolRate;

	public HeatStat(TimeProvider timeProvider, AbstractStat maxHeat, AbstractStat coolRate) {
		super(NAME);
		this.maxHeat = maxHeat;
		this.coolRate = coolRate;
		this.coolness = new LinearTimeValue(maxHeat.getValue());
		this.coolness.setValue(timeProvider.now(), maxHeat.getValue());
		this.coolness.setCurrentRate(timeProvider.now(), coolRate.getValue());
		this.timeProvider = timeProvider;
		maxHeat.subscribe(this);
		coolRate.subscribe(this);
	}

	@Override
	public double getValue() {
		return maxHeat.getValue() - coolness.getValue(timeProvider.now());
	}

	public void setValue(double value) {
		coolness.setValue(timeProvider.now(), maxHeat.getValue() - value);
		publisher.updateSubscribers(this);
	}

	@Override
	public void update(Stat arg) {
		coolness.setCurrentRate(timeProvider.now(), coolRate.getValue());
		coolness.setMaxValue(timeProvider.now(), maxHeat.getValue());
		publisher.updateSubscribers(this);
	}

	public void generate(double amount) {
		coolness.consume(timeProvider.now(), amount);
		publisher.updateSubscribers(this);
	}
}
