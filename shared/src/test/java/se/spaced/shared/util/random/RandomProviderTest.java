package se.spaced.shared.util.random;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class RandomProviderTest {

	private static final int MIN = 123;
	private static final int MAX = 145;

	@Test
	public void testWithinIntegerRange() {
		RealRandomProvider provider = new RealRandomProvider();
		for (int i = 0; i < 1000; i++) {
			int value = provider.getInteger(MIN, MAX);
			assertTrue(value >= MIN && value <= MAX);
		}
	}

	@Test
	public void testWithinDoubleRange() {
		RealRandomProvider provider = new RealRandomProvider();
		for (int i = 0; i < 1000; i++) {
			double value = provider.getDouble(MIN, MAX);
			assertTrue(value >= MIN && value <= MAX);
		}
	}

}
