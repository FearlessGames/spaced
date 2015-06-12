package se.spaced.shared.model.stats;

import org.apache.commons.math.util.MathUtils;
import se.fearless.common.publisher.SimplePublisher;
import se.fearless.common.publisher.Subscriber;

public abstract class AbstractStat implements Stat {
	private static final double EPSILON = 1e-6;

	private final String name;
	protected SimplePublisher<Stat> publisher = new SimplePublisher<Stat>();

	AbstractStat(String name) {
		this.name = name;
	}


	@Override
	public String toString() {
		return name + ": " + getValue();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof AbstractStat)) {
			return false;
		}

		AbstractStat stat = (AbstractStat) o;

		if (!name.equals(stat.name)) {
			return false;
		} else if (!MathUtils.equals(getValue(), stat.getValue(), EPSILON)) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		int result = name.hashCode();
		return (int) (result * 31 + getValue());
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void subscribe(Subscriber<Stat> statSubscriber) {
		publisher.subscribe(statSubscriber);
	}

	@Override
	public void unsubscribe(Subscriber<Stat> statSubscriber) {
		publisher.unsubscribe(statSubscriber);
	}
}
