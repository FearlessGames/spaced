package se.ardortech.math;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TestAABox {
	private static final double EPSILON = 0.0;

	@Test
	public void testCreate() {
		AABox aabb = new AABox(new SpacedVector3(1, 2, 3), new SpacedVector3(5, 6, 7));
		assertVector(aabb.getMin(), new SpacedVector3(1, 2, 3));
		assertVector(aabb.getMax(), new SpacedVector3(5, 6, 7));
		assertVector(aabb.getCenter(), new SpacedVector3(3, 4, 5));
		assertVector(aabb.getSize(), new SpacedVector3(4, 4, 4));
	}

	@Test
	public void testCreateFromCenterSize() {
		AABox aabb = AABox.fromCenterSize(new SpacedVector3(1, 2, 3), new SpacedVector3(4, 6, 8));
		assertVector(aabb.getMin(), new SpacedVector3(-1, -1, -1));
		assertVector(aabb.getMax(), new SpacedVector3(3, 5, 7));
		assertVector(aabb.getCenter(), new SpacedVector3(1, 2, 3));
		assertVector(aabb.getSize(), new SpacedVector3(4, 6, 8));
	}

	@Test
	public void testCreateCopy() {
		AABox aabb1 = new AABox(new SpacedVector3(1, 2, 3), new SpacedVector3(4, 5, 6));
		AABox aabb2 = new AABox(aabb1);
		assertBoxEquals(aabb1, aabb2);
	}

	@Test
	public void testSet() {
		AABox aabb1 = new AABox(new SpacedVector3(1, 2, 3), new SpacedVector3(4, 5, 6));
		AABox aabb2 = new AABox(new SpacedVector3(7, 8, 9), new SpacedVector3(10, 11, 12));
		aabb2.set(aabb1);
		assertBoxEquals(aabb1, aabb2);
	}

	@Test
	public void testSetMinMax() {
		AABox aabb1 = new AABox(new SpacedVector3(1, 2, 3), new SpacedVector3(4, 5, 6));
		AABox aabb2 = new AABox(new SpacedVector3(7, 8, 9), new SpacedVector3(10, 11, 12));
		aabb2.setMinMax(aabb1.getMin(), aabb1.getMax());
		assertBoxEquals(aabb1, aabb2);
	}

	@Test
	public void testSetCenterSize() {
		AABox aabb1 = new AABox(new SpacedVector3(1, 2, 3), new SpacedVector3(4, 5, 6));
		AABox aabb2 = new AABox(new SpacedVector3(7, 8, 9), new SpacedVector3(10, 11, 12));
		aabb2.setCenterSize(aabb1.getCenter(), aabb1.getSize());
		assertBoxEquals(aabb1, aabb2);
	}

	@Test
	public void testIsInside() {
		AABox aabb = new AABox(new SpacedVector3(1, 2, 3), new SpacedVector3(5, 6, 7));
		assertTrue(aabb.isInside(new SpacedVector3(3, 4, 5)));
		assertTrue(aabb.isInside(new SpacedVector3(1, 2, 3)));
		assertTrue(aabb.isInside(new SpacedVector3(5, 2, 3)));
		assertTrue(aabb.isInside(new SpacedVector3(1, 6, 3)));
		assertTrue(aabb.isInside(new SpacedVector3(5, 6, 3)));
		assertTrue(aabb.isInside(new SpacedVector3(1, 2, 7)));
		assertTrue(aabb.isInside(new SpacedVector3(5, 2, 7)));
		assertTrue(aabb.isInside(new SpacedVector3(1, 6, 7)));
		assertTrue(aabb.isInside(new SpacedVector3(5, 6, 7)));

		assertFalse(aabb.isInside(new SpacedVector3(0, 4, 5)));
		assertFalse(aabb.isInside(new SpacedVector3(6, 4, 5)));
		assertFalse(aabb.isInside(new SpacedVector3(3, 1, 5)));
		assertFalse(aabb.isInside(new SpacedVector3(3, 7, 5)));
		assertFalse(aabb.isInside(new SpacedVector3(3, 4, 2)));
		assertFalse(aabb.isInside(new SpacedVector3(3, 4, 8)));
	}

	@Test
	public void testIsInsideMargin() {
		double margin = 1;
		AABox aabb = new AABox(new SpacedVector3(1, 2, 3), new SpacedVector3(5, 6, 7));
		assertTrue(aabb.isInside(new SpacedVector3(3, 4, 5), margin));
		assertTrue(aabb.isInside(new SpacedVector3(0, 1, 2), margin));
		assertTrue(aabb.isInside(new SpacedVector3(6, 1, 2), margin));
		assertTrue(aabb.isInside(new SpacedVector3(0, 7, 2), margin));
		assertTrue(aabb.isInside(new SpacedVector3(6, 7, 2), margin));
		assertTrue(aabb.isInside(new SpacedVector3(0, 1, 8), margin));
		assertTrue(aabb.isInside(new SpacedVector3(6, 1, 8), margin));
		assertTrue(aabb.isInside(new SpacedVector3(0, 7, 8), margin));
		assertTrue(aabb.isInside(new SpacedVector3(6, 7, 8), margin));

		assertFalse(aabb.isInside(new SpacedVector3(-1, 4, 5), margin));
		assertFalse(aabb.isInside(new SpacedVector3(7, 4, 5), margin));
		assertFalse(aabb.isInside(new SpacedVector3(3, 0, 5), margin));
		assertFalse(aabb.isInside(new SpacedVector3(3, 8, 5), margin));
		assertFalse(aabb.isInside(new SpacedVector3(3, 4, 1), margin));
		assertFalse(aabb.isInside(new SpacedVector3(3, 4, 9), margin));
	}

	@Test
	public void testExpand() {
		AABox aabb = new AABox(new SpacedVector3(0, 0, 0), new SpacedVector3(0, 0, 0));
		aabb.expand(new SpacedVector3(0, 0, 0));
		assertBoxEquals(aabb, new AABox(new SpacedVector3(0, 0, 0), new SpacedVector3(0, 0, 0)));
		aabb.expand(new SpacedVector3(1, 0, 0));
		assertBoxEquals(aabb, new AABox(new SpacedVector3(0, 0, 0), new SpacedVector3(1, 0, 0)));
		aabb.expand(new SpacedVector3(-1, 0, 0));
		assertBoxEquals(aabb, new AABox(new SpacedVector3(-1, 0, 0), new SpacedVector3(1, 0, 0)));
		aabb.expand(new SpacedVector3(0, 1, 0));
		assertBoxEquals(aabb, new AABox(new SpacedVector3(-1, 0, 0), new SpacedVector3(1, 1, 0)));
		aabb.expand(new SpacedVector3(0, -1, 0));
		assertBoxEquals(aabb, new AABox(new SpacedVector3(-1, -1, 0), new SpacedVector3(1, 1, 0)));
		aabb.expand(new SpacedVector3(0, 0, 1));
		assertBoxEquals(aabb, new AABox(new SpacedVector3(-1, -1, 0), new SpacedVector3(1, 1, 1)));
		aabb.expand(new SpacedVector3(0, 0, -1));
		assertBoxEquals(aabb, new AABox(new SpacedVector3(-1, -1, -1), new SpacedVector3(1, 1, 1)));
	}

	@Test
	public void testOverlap() {
		AABox aabb = new AABox(new SpacedVector3(-1, -1, -1), new SpacedVector3(1, 1, 1));
		assertTrue(aabb.overlap(new AABox(new SpacedVector3(-2, -2, -2), new SpacedVector3(-1, -1, -1))));
		assertTrue(aabb.overlap(new AABox(new SpacedVector3( 1, -2, -2), new SpacedVector3( 2, -1, -1))));
		assertTrue(aabb.overlap(new AABox(new SpacedVector3(-2,  1, -2), new SpacedVector3(-1,  2, -1))));
		assertTrue(aabb.overlap(new AABox(new SpacedVector3( 1,  1, -2), new SpacedVector3( 2,  2, -1))));
		assertTrue(aabb.overlap(new AABox(new SpacedVector3(-2, -2,  1), new SpacedVector3(-1, -1,  2))));
		assertTrue(aabb.overlap(new AABox(new SpacedVector3( 1, -2,  1), new SpacedVector3( 2, -1,  2))));
		assertTrue(aabb.overlap(new AABox(new SpacedVector3(-2,  1,  1), new SpacedVector3(-1,  2,  2))));
		assertTrue(aabb.overlap(new AABox(new SpacedVector3( 1,  1,  1), new SpacedVector3( 2,  2,  2))));

		assertFalse(aabb.overlap(AABox.fromCenterSize(new SpacedVector3( 3,  0,  0), new SpacedVector3(2, 2, 2))));
		assertFalse(aabb.overlap(AABox.fromCenterSize(new SpacedVector3(-3,  0,  0), new SpacedVector3(2, 2, 2))));
		assertFalse(aabb.overlap(AABox.fromCenterSize(new SpacedVector3( 0,  3,  0), new SpacedVector3(2, 2, 2))));
		assertFalse(aabb.overlap(AABox.fromCenterSize(new SpacedVector3( 0, -3,  0), new SpacedVector3(2, 2, 2))));
		assertFalse(aabb.overlap(AABox.fromCenterSize(new SpacedVector3( 0,  0,  3), new SpacedVector3(2, 2, 2))));
		assertFalse(aabb.overlap(AABox.fromCenterSize(new SpacedVector3( 0,  0, -3), new SpacedVector3(2, 2, 2))));
	}

	@Test
	public void testTranslate() {
		AABox aabb1 = new AABox(new SpacedVector3(0, 0, 0), new SpacedVector3(1, 1, 1));
		AABox aabb2 = new AABox(new SpacedVector3(1, 1, 1), new SpacedVector3(2, 2, 2));
		aabb1.translate(new SpacedVector3(1, 1, 1));
		assertBoxEquals(aabb1, aabb2);
	}

	private void assertVector(SpacedVector3 v1, SpacedVector3 v2) {
		assertEquals(v2.getX(), v1.getX(), EPSILON);
		assertEquals(v2.getY(), v1.getY(), EPSILON);
		assertEquals(v2.getZ(), v1.getZ(), EPSILON);
	}

	private void assertBoxEquals(Box aabb1, Box aabb2) {
		assertVector(aabb1.getMin(), aabb2.getMin());
		assertVector(aabb1.getMax(), aabb2.getMax());
		assertVector(aabb1.getCenter(), aabb2.getCenter());
		assertVector(aabb1.getSize(), aabb2.getSize());
	}
}