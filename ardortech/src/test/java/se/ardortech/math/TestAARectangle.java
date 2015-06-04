package se.ardortech.math;

import com.ardor3d.math.Vector2;
import com.ardor3d.math.type.ReadOnlyVector2;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TestAARectangle {
	private static final double EPSILON = 0.0;

	@Test
	public void testCreate() {
		AARectangle rectangle = new AARectangle(new Vector2(1, 2), new Vector2(5, 6));
		assertVector(rectangle.getMin(), new Vector2(1, 2));
		assertVector(rectangle.getMax(), new Vector2(5, 6));
		assertVector(rectangle.getCenter(), new Vector2(3, 4));
		assertVector(rectangle.getSize(), new Vector2(4, 4));
	}

	@Test
	public void testCreateFromCenterSize() {
		AARectangle rectangle = AARectangle.fromCenterSize(new Vector2(1, 2), new Vector2(4, 6));
		assertVector(rectangle.getMin(), new Vector2(-1, -1));
		assertVector(rectangle.getMax(), new Vector2(3, 5));
		assertVector(rectangle.getCenter(), new Vector2(1, 2));
		assertVector(rectangle.getSize(), new Vector2(4, 6));
	}

	@Test
	public void testCreateCopy() {
		AARectangle rectangle1 = new AARectangle(new Vector2(1, 2), new Vector2(4, 5));
		AARectangle rectangle2 = new AARectangle(rectangle1);
		assertRectangleEquals(rectangle1, rectangle2);
	}

	@Test
	public void testSet() {
		AARectangle rectangle1 = new AARectangle(new Vector2(1, 2), new Vector2(4, 5));
		AARectangle rectangle2 = new AARectangle(new Vector2(7, 8), new Vector2(10, 11));
		rectangle2.set(rectangle1);
		assertRectangleEquals(rectangle1, rectangle2);
	}

	@Test
	public void testSetMinMax() {
		AARectangle rectangle1 = new AARectangle(new Vector2(1, 2), new Vector2(4, 5));
		AARectangle rectangle2 = new AARectangle(new Vector2(7, 8), new Vector2(10, 11));
		rectangle2.setMinMax(rectangle1.getMin(), rectangle1.getMax());
		assertRectangleEquals(rectangle1, rectangle2);
	}

	@Test
	public void testSetCenterSize() {
		AARectangle rectangle1 = new AARectangle(new Vector2(1, 2), new Vector2(4, 5));
		AARectangle rectangle2 = new AARectangle(new Vector2(7, 8), new Vector2(10, 11));
		rectangle2.setCenterSize(rectangle1.getCenter(), rectangle1.getSize());
		assertRectangleEquals(rectangle1, rectangle2);
	}

	@Test
	public void testIsInside() {
		AARectangle rectangle = new AARectangle(new Vector2(1, 2), new Vector2(5, 6));
		assertTrue(rectangle.isInside(new Vector2(3, 4)));
		assertTrue(rectangle.isInside(new Vector2(1, 2)));
		assertTrue(rectangle.isInside(new Vector2(5, 2)));
		assertTrue(rectangle.isInside(new Vector2(1, 6)));
		assertTrue(rectangle.isInside(new Vector2(5, 6)));

		assertFalse(rectangle.isInside(new Vector2(0, 4)));
		assertFalse(rectangle.isInside(new Vector2(6, 4)));
		assertFalse(rectangle.isInside(new Vector2(3, 1)));
		assertFalse(rectangle.isInside(new Vector2(3, 7)));
	}

	@Test
	public void testExpand() {
		AARectangle rectangle = new AARectangle(new Vector2(0, 0), new Vector2(0, 0));
		rectangle.expand(new Vector2(0, 0));
		assertRectangleEquals(rectangle, new AARectangle(new Vector2(0, 0), new Vector2(0, 0)));
		rectangle.expand(new Vector2(1, 0));
		assertRectangleEquals(rectangle, new AARectangle(new Vector2(0, 0), new Vector2(1, 0)));
		rectangle.expand(new Vector2(-1, 0));
		assertRectangleEquals(rectangle, new AARectangle(new Vector2(-1, 0), new Vector2(1, 0)));
		rectangle.expand(new Vector2(0, 1));
		assertRectangleEquals(rectangle, new AARectangle(new Vector2(-1, 0), new Vector2(1, 1)));
		rectangle.expand(new Vector2(0, -1));
		assertRectangleEquals(rectangle, new AARectangle(new Vector2(-1, -1), new Vector2(1, 1)));
	}

	@Test
	public void testOverlap() {
		AARectangle rectangle = new AARectangle(new Vector2(-1, -1), new Vector2(1, 1));
		assertTrue(rectangle.overlap(new AARectangle(new Vector2(-2, -2), new Vector2(-1, -1))));
		assertTrue(rectangle.overlap(new AARectangle(new Vector2( 1, -2), new Vector2( 2, -1))));
		assertTrue(rectangle.overlap(new AARectangle(new Vector2(-2,  1), new Vector2(-1,  2))));
		assertTrue(rectangle.overlap(new AARectangle(new Vector2( 1,  1), new Vector2( 2,  2))));

		assertFalse(rectangle.overlap(AARectangle.fromCenterSize(new Vector2( 3,  0), new Vector2(2, 2))));
		assertFalse(rectangle.overlap(AARectangle.fromCenterSize(new Vector2(-3,  0), new Vector2(2, 2))));
		assertFalse(rectangle.overlap(AARectangle.fromCenterSize(new Vector2( 0,  3), new Vector2(2, 2))));
		assertFalse(rectangle.overlap(AARectangle.fromCenterSize(new Vector2( 0, -3), new Vector2(2, 2))));
	}

	@Test
	public void testTranslate() {
		AARectangle rectangle1 = new AARectangle(new Vector2(0, 0), new Vector2(1, 1));
		AARectangle rectangle2 = new AARectangle(new Vector2(1, 1), new Vector2(2, 2));
		rectangle1.translate(new Vector2(1, 1));
		assertRectangleEquals(rectangle1, rectangle2);
	}

	private void assertVector(ReadOnlyVector2 v1, ReadOnlyVector2 v2) {
		assertEquals(v2.getX(), v1.getX(), EPSILON);
		assertEquals(v2.getY(), v1.getY(), EPSILON);
	}

	private void assertRectangleEquals(Rectangle rectangle1, Rectangle rectangle2) {
		assertVector(rectangle1.getMin(), rectangle2.getMin());
		assertVector(rectangle1.getMax(), rectangle2.getMax());
		assertVector(rectangle1.getCenter(), rectangle2.getCenter());
		assertVector(rectangle1.getSize(), rectangle2.getSize());
	}
}