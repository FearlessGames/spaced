package se.spaced.shared.util.math.interval;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class IntervalIntTest {

	@Test
	public void testContains() {
		IntervalInt interval = new IntervalInt(10, 20);
		assertTrue(interval.contains(10));
		assertTrue(interval.contains(15));
		assertTrue(interval.contains(20));
		assertFalse(interval.contains(9));
		assertFalse(interval.contains(21));
	}

	@Test
	public void testOverlaps() {
		IntervalInt interval = new IntervalInt(10, 20);
		assertFalse(interval.overlaps(new IntervalInt(9, 9)));
		assertFalse(interval.overlaps(new IntervalInt(21, 21)));
		assertFalse(interval.overlaps(new IntervalInt(-100, 9)));
		assertFalse(interval.overlaps(new IntervalInt(21, 100)));

		assertFalse(interval.overlaps(new IntervalInt(-100, 10)));
		assertFalse(interval.overlaps(new IntervalInt(20, 100)));

		assertTrue(interval.overlaps(new IntervalInt(9, 11)));
		assertTrue(interval.overlaps(new IntervalInt(19, 21)));
		assertTrue(interval.overlaps(new IntervalInt(14, 15)));
		assertTrue(interval.overlaps(new IntervalInt(5, 25)));
	}

	@Test
	public void testMerge1() {
		IntervalInt a = new IntervalInt(10, 20);
		IntervalInt b = new IntervalInt(40, 50);
		IntervalInt enclosing = a.merge(b);
		assertEquals(enclosing, new IntervalInt(10, 50));
	}

	@Test
	public void testMerge2() {
		IntervalInt a = new IntervalInt(10, 20);
		IntervalInt b = new IntervalInt(10, 20);
		IntervalInt enclosing = a.merge(b);
		assertEquals(enclosing, new IntervalInt(10, 20));
	}


	@Test
	public void testMerge3() {
		IntervalInt a = new IntervalInt(10, 20);
		IntervalInt b = new IntervalInt(14, 13);
		IntervalInt enclosing = a.merge(b);
		assertEquals(enclosing, new IntervalInt(10, 20));
	}

	@Test
	public void testLength() {
		IntervalInt a = new IntervalInt(10, 20);
		assertEquals(a.size(), 10);
	}

	@Test
	public void testIntersection1() {
		IntervalInt a = new IntervalInt(10, 20);
		IntervalInt b = new IntervalInt(15, 25);
		IntervalInt c = a.intersection(b);
		assertEquals(c, new IntervalInt(15, 20));
	}

	@Test
	public void testIntersection2() {
		IntervalInt a = new IntervalInt(10, 20);
		IntervalInt b = new IntervalInt(40, 70);
		IntervalInt c = a.intersection(b);
		assertEquals(c.size(), 0);
	}
}
