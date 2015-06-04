package se.spaced.shared.util.random;

import se.spaced.shared.util.math.interval.IntervalInt;

public interface RandomProvider {
	/**
	 * @param min
	 * @param max - must be larger than or equal to min
	 * @return an integer x such that min <= x <= max
	 */
	int getInteger(int min, int max);

	int getInteger(IntervalInt range);

	/**
	 * @param min
	 * @param max - must be larger than or equal to min
	 * @return a double x such min <= x <= max
	 */
	double getDouble(double min, double max);
}
