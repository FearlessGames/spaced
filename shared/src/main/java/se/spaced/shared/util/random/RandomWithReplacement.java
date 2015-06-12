package se.spaced.shared.util.random;


import com.google.common.base.Function;
import com.google.common.collect.Lists;
import se.fearless.common.collections.Collections3;

import java.util.List;
import java.util.Random;

public class RandomWithReplacement<S, R> {
	private final Function<S, R> transform;
	private final Random random;

	public RandomWithReplacement(Function<S, R> transform, Random random) {
		this.transform = transform;
		this.random = random;
	}

	public List<R> select(List<S> input, int picks) {
		List<R> result = Lists.newArrayListWithExpectedSize(picks);
		for (int i = 0; i < picks; i++) {
			S element = Collections3.getRandomElement(input, random);
			result.add(transform.apply(element));
		}
		return result;
	}
}
