package se.spaced.shared.model.stats;

import se.fearless.common.publisher.Subscriber;

public abstract class DerivedStat extends AbstractStat implements Subscriber<Stat> {

	private double value;

	protected DerivedStat(String name, Stat... dependencies) {
		super(name);
		for (Stat dependency : dependencies) {
			dependency.subscribe(this);
		}
	}

	@Override
	public final double getValue() {
		return value;
	}

	@Override
	public final void update(Stat arg) {
		value = recalculateValue();
		publisher.updateSubscribers(this);
	}

	protected abstract double recalculateValue();
}
