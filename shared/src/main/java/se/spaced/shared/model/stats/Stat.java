package se.spaced.shared.model.stats;

import se.fearless.common.publisher.Publisher;

public interface Stat extends Publisher<Stat> {
	/**
	 * The current value of this stat.
	 *
	 * @return the current value
	 */
	double getValue();

	String getName();
}
