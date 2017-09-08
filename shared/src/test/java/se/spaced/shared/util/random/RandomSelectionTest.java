package se.spaced.shared.util.random;

import com.google.common.base.Functions;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.junit.Test;

import java.util.List;
import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class RandomSelectionTest {

	@Test
	public void testSimpleWithReplacement() throws Exception {
		RandomWithReplacement<Integer, Integer> random = new RandomWithReplacement<Integer, Integer>(Functions.identity(), new Random(4711));
		List<Integer> numbers = Lists.newArrayList(1, 2, 3, 4, 5);
		List<Integer> picks = random.select(numbers, 5);
		assertEquals(5, picks.size());
		for (Integer pick : picks) {
			assertTrue(numbers.contains(pick));
		}
	}

	@Test
	public void testSimpleWithoutReplacement() throws Exception {
		RandomWithoutReplacement<Integer, Integer> random = new RandomWithoutReplacement<Integer, Integer>(Functions.identity(), new Random(4711));
		List<Integer> numbers = Lists.newArrayList(1, 2, 3, 4, 5);
		List<Integer> picks = random.select(numbers, 5);
		assertEquals(5, picks.size());
		for (Integer pick : picks) {
			assertTrue(numbers.contains(pick));
		}
		for (Integer number : numbers) {
			assertEquals(1, Iterables.frequency(picks, number));
		}

	}

}
