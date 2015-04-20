package se.spaced.shared.model.stats;

public class SimpleStat extends AbstractStat implements MutableStat {

	private double value;
	private final double lowerBound;

	public SimpleStat(String name, double value) {
		this(name, value, 0.0);
	}

	public SimpleStat(String name, double value, double lowerBound) {
		super(name);
		this.value = value;
		this.lowerBound = lowerBound;
	}

	@Override
	public double getValue() {
		return value;
	}

	@Override
	public void changeValue(double newValue) {
		value = Math.max(newValue, lowerBound);
		publisher.updateSubscribers(this);
	}

	@Override
	public void increaseValue(double amount) {
		changeValue(getValue() + amount);
	}

	@Override
	public void decreaseValue(double amount) {
		changeValue(getValue() - amount);
	}

	@Override
	public String toString() {
		return "SimpleStat{" +
				"name=" + getName() +
				"value=" + value +
				'}';
	}
}
