package se.spaced.shared.world;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class TimeSystemInfoTest {

	private static final double EPSILON = 1e-10;

	@Test
	public void earthDay() throws Exception {
		TimeSystemInfo earth = new TimeSystemInfo(24, 60, 60, 1.0);

		assertEquals(24, earth.getHoursPerDay());
		assertEquals(60, earth.getMinutesPerHour());
		assertEquals(86400 * 1000, earth.getCycleTimeInMillis());
	}

	@Test
	public void earthDayInHalfTime() throws Exception {
		TimeSystemInfo earth = new TimeSystemInfo(24, 60, 60, 0.5);

		assertEquals(24, earth.getHoursPerDay());
		assertEquals(60, earth.getMinutesPerHour());
		assertEquals(0.5, earth.getSpeedFactor(), EPSILON);
		assertEquals(43200 * 1000, earth.getCycleTimeInMillis());
	}

	@Test
	public void earthDayInDoubleTime() throws Exception {
		TimeSystemInfo earth = new TimeSystemInfo(24, 60, 60, 2.0);

		assertEquals(24, earth.getHoursPerDay());
		assertEquals(60, earth.getMinutesPerHour());
		assertEquals(172800 * 1000, earth.getCycleTimeInMillis());
	}

	@Test(expected = IllegalArgumentException.class)
	public void badSpeedFactor() throws Exception {
		TimeSystemInfo bad = new TimeSystemInfo(24, 60, 60, 0.0);
	}

	@Test
	public void decimalDay() throws Exception {
		TimeSystemInfo decimal = new TimeSystemInfo(10, 10, 10, 1.0);
		assertEquals(10, decimal.getHoursPerDay());
		assertEquals(10, decimal.getMinutesPerHour());
		assertEquals(10, decimal.getSecondsPerMinute());

		assertEquals(1000 * 1000, decimal.getCycleTimeInMillis());
	}

	@Test
	public void equals() throws Exception {
		TimeSystemInfo a = new TimeSystemInfo(10, 10, 10, 1.0);
		TimeSystemInfo b = new TimeSystemInfo(10, 10, 10, 2.0);
		TimeSystemInfo c = new TimeSystemInfo(10, 10, 60, 1.0);
		TimeSystemInfo d = new TimeSystemInfo(10, 60, 10, 1.0);
		TimeSystemInfo e = new TimeSystemInfo(24, 10, 10, 1.0);
		TimeSystemInfo a2 = new TimeSystemInfo(10, 10, 10, 1.0);

		assertFalse(a.equals(b));
		assertFalse(b.equals(a));
		assertFalse(a.hashCode() == b.hashCode());

		assertFalse(a.equals(c));
		assertFalse(c.equals(a));
		assertFalse(a.hashCode() == c.hashCode());

		assertFalse(a.equals(d));
		assertFalse(d.equals(a));
		assertFalse(a.hashCode() == d.hashCode());

		assertFalse(a.equals(e));
		assertFalse(e.equals(a));
		assertFalse(a.hashCode() == e.hashCode());

		assertEquals(a, a);
		assertEquals(a, a2);
		assertEquals(a2, a);
		assertEquals(a.hashCode(), a2.hashCode());

		assertFalse(a.equals(null));
		assertFalse(a.equals("foo"));
	}
}
