package se.spaced.shared.util.math;

import org.junit.Before;
import org.junit.Test;
import se.fearless.common.time.MockTimeProvider;

import static org.junit.Assert.assertEquals;

public class LinearTimeValueTest {
	private static final double EPSILON = 0.00000000000001;
	private MockTimeProvider timeProvider;

	@Before
	public void setup() {
		timeProvider = new MockTimeProvider();
	}

	@Test
	public void changesFromZeroToHundredInTenSeconds() {
		LinearTimeValue value = new LinearTimeValue(1000);
		value.setCurrentRate(timeProvider.now(), 10);
		assertEquals(0, value.getValue(timeProvider.now()), EPSILON);

		timeProvider.advanceTime(10 * 1000);
		assertEquals(100, value.getValue(timeProvider.now()), EPSILON);
	}


	@Test
	public void rateOfZero() {
		LinearTimeValue value = new LinearTimeValue(1000);
		value.setCurrentRate(timeProvider.now(), 0);
		assertEquals(0, value.getValue(timeProvider.now()), EPSILON);

		timeProvider.advanceTime(10 * 1000);
		assertEquals(0, value.getValue(timeProvider.now()), EPSILON);
	}


	@Test
	public void changesRateDuringTheChargeup() {
		LinearTimeValue value = new LinearTimeValue(200);
		value.setCurrentRate(timeProvider.now(), 10);
		assertEquals(0, value.getValue(timeProvider.now()), EPSILON);
		timeProvider.advanceTime(5 * 1000);

		assertEquals(50, value.getValue(timeProvider.now()), EPSILON);
		value.setCurrentRate(timeProvider.now(), 20);
		assertEquals(50, value.getValue(timeProvider.now()), EPSILON);

		timeProvider.advanceTime(5 * 1000);
		assertEquals(150, value.getValue(timeProvider.now()), EPSILON);
	}

	@Test
	public void testCap() {
		LinearTimeValue value = new LinearTimeValue(40);
		value.setCurrentRate(timeProvider.now(), 10);
		assertEquals(0, value.getValue(timeProvider.now()), EPSILON);
		timeProvider.advanceTime(5 * 1000 * 1000);

		assertEquals(40, value.getValue(timeProvider.now()), EPSILON);
		value.consume(timeProvider.now(), 10);
		assertEquals(30, value.getValue(timeProvider.now()), EPSILON);
	}

	@Test
	public void testReverseCap() {
		LinearTimeValue value = new LinearTimeValue(40);
		value.setCurrentRate(timeProvider.now(), -10);
		assertEquals(0, value.getValue(timeProvider.now()), EPSILON);
		timeProvider.advanceTime(5 * 1000 * 1000);

		assertEquals(0, value.getValue(timeProvider.now()), EPSILON);
		value.consume(timeProvider.now(), -10);
		assertEquals(10, value.getValue(timeProvider.now()), EPSILON);
	}


	@Test
	public void consume() {
		LinearTimeValue value = new LinearTimeValue(200);
		value.setCurrentRate(timeProvider.now(), 10);
		assertEquals(0, value.getValue(timeProvider.now()), EPSILON);
		timeProvider.advanceTime(5 * 1000);
		value.consume(timeProvider.now(), 30);
		assertEquals(20, value.getValue(timeProvider.now()), EPSILON);
		value.setCurrentRate(timeProvider.now(), 20);

		timeProvider.advanceTime(5 * 1000);
		assertEquals(120, value.getValue(timeProvider.now()), EPSILON);
	}


	@Test
	public void testSetter() {
		LinearTimeValue value = new LinearTimeValue(200);
		value.setCurrentRate(timeProvider.now(), 10);
		assertEquals(0, value.getValue(timeProvider.now()), EPSILON);
		timeProvider.advanceTime(5 * 1000);
		value.setValue(timeProvider.now(), 20);

		assertEquals(20, value.getValue(timeProvider.now()), EPSILON);
		value.setCurrentRate(timeProvider.now(), 20);

		timeProvider.advanceTime(5 * 1000);
		assertEquals(120, value.getValue(timeProvider.now()), EPSILON);
	}

	@Test
	public void testSetMaxValue() {
		LinearTimeValue value = new LinearTimeValue(200);
		value.setCurrentRate(timeProvider.now(), 10);
		assertEquals(0, value.getValue(timeProvider.now()), EPSILON);
		timeProvider.advanceTime(5 * 1000);

		assertEquals(50, value.getValue(timeProvider.now()), EPSILON);
		value.setMaxValue(timeProvider.now(), 20);

		assertEquals(20, value.getValue(timeProvider.now()), EPSILON);

		timeProvider.advanceTime(5 * 1000);
		assertEquals(20, value.getValue(timeProvider.now()), EPSILON);

		value.setMaxValue(timeProvider.now(), 200);
		assertEquals(20, value.getValue(timeProvider.now()), EPSILON);

		timeProvider.advanceTime(5 * 1000);
		assertEquals(70, value.getValue(timeProvider.now()), EPSILON);

		timeProvider.advanceTime(5 * 1000 * 1000);
		assertEquals(200, value.getValue(timeProvider.now()), EPSILON);
	}


	@Test
	public void setValueBelowLowerBoundAndThenIncrease() throws Exception {
		LinearTimeValue value = new LinearTimeValue(200);
		value.setCurrentRate(timeProvider.now(), 10);

		value.setValue(timeProvider.now(), -10);
		assertEquals(0, value.getValue(timeProvider.now()), EPSILON);

		timeProvider.advanceTime(5 * 1000);
		assertEquals(50, value.getValue(timeProvider.now()), EPSILON);
	}

	@Test
	public void setValueAboveUpperBoundAndThenCooldown() throws Exception {
		LinearTimeValue value = new LinearTimeValue(200);
		value.setCurrentRate(timeProvider.now(), -10);

		value.setValue(timeProvider.now(), 210);
		assertEquals(200, value.getValue(timeProvider.now()), EPSILON);

		timeProvider.advanceTime(5 * 1000);
		assertEquals(150, value.getValue(timeProvider.now()), EPSILON);
	}

}
