package se.spaced.shared.util.random;


import com.google.common.base.Function;
import com.google.common.collect.Lists;
import se.fearlessgames.common.collections.Collections3;

import java.util.List;
import java.util.Random;

public class RandomWithoutReplacement<S, R> {
	private final Function<S, R> transform;
	private final Random random;

	public RandomWithoutReplacement(Function<S, R> transform, Random random) {
		this.transform = transform;
		this.random = random;
	}

	public List<R> select(List<S> input, int picks) {
		List<R> result = Lists.newArrayListWithExpectedSize(picks);
		List<S> copy = Lists.newArrayList(input);
		for (int i = 0; i < picks; i++) {
			S element = Collections3.removeRandomElement(copy, random);
			result.add(transform.apply(element));
		}
		return result;
	}
}
