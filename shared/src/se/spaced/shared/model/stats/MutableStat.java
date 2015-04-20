package se.spaced.shared.model.stats;

public interface MutableStat extends Stat {
	void changeValue(double newValue);

	void increaseValue(double amount);

	void decreaseValue(double amount);
}
