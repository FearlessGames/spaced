package se.spaced.shared.world.area;

import com.ardor3d.math.Vector2;
import org.junit.Test;
import se.ardortech.math.Box;
import se.ardortech.math.SpacedVector3;

import java.awt.geom.Rectangle2D;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class PolygonTest {

	private static final double EPSILON = 1e-10;

	@Test
	public void contains() throws Exception {
		Polygon polygon = new Polygon();

		polygon.add(new SpacedVector3(10, 0, 10));
		polygon.add(new SpacedVector3(5, 0, 0));
		polygon.add(new SpacedVector3(-10, 0, 10));
		polygon.add(new SpacedVector3(-10, 0, -10));
		polygon.add(new SpacedVector3(10, 0, -10));
		polygon.add(new SpacedVector3(10, 0, 10));

		assertTrue(polygon.containsPoint(new Vector2(1, 1)));
		assertTrue(polygon.containsPoint(new Vector2(9, 6)));
		assertTrue(polygon.containsPoint(new Vector2(-9, -9)));
		assertTrue(polygon.containsPoint(new Vector2(-9, 9)));
		assertTrue(polygon.containsPoint(new Vector2(9, -9)));

		assertFalse(polygon.containsPoint(new Vector2(0, 15)));
		
	}

	@Test
	public void containsPointy() throws Exception {
		Polygon polygon = new Polygon();

		polygon.add(new SpacedVector3(10, 0, 10));
		polygon.add(new SpacedVector3(5, 0, 0));
		polygon.add(new SpacedVector3(-13, 0, 17));
		polygon.add(new SpacedVector3(-2, 0, 1));
		polygon.add(new SpacedVector3(-10, 0, -13));
		polygon.add(new SpacedVector3(8, 0, -10));
		polygon.add(new SpacedVector3(10, 0, 10));

		assertTrue(polygon.containsPoint(new Vector2(1, 1)));
		assertTrue(polygon.containsPoint(new Vector2(9, 6)));
		assertFalse(polygon.containsPoint(new Vector2(9, -9)));
		assertFalse(polygon.containsPoint(new Vector2(-9, 9)));
		assertFalse(polygon.containsPoint(new Vector2(-9, -9)));

		assertFalse(polygon.containsPoint(new Vector2(0, 15)));
	}

	@Test
	public void boundsForConvex() throws Exception {
		Polygon p1 = new Polygon();

		p1.add(new SpacedVector3(10, 0, 13));
		p1.add(new SpacedVector3(30, 0, 15));
		p1.add(new SpacedVector3(45, 0, 3));
		p1.add(new SpacedVector3(27, 0, 0));
		p1.add(new SpacedVector3(2, 0, 5));
		p1.add(new SpacedVector3(10, 0, 13));

		Rectangle2D rect = p1.getBoundingRect();
		assertEquals(2, rect.getX(), EPSILON);
		assertEquals(0, rect.getY(), EPSILON);
		assertEquals(43, rect.getWidth(), EPSILON);
		assertEquals(15, rect.getHeight(), EPSILON);
	}

	@Test
	public void boundingBox() throws Exception {
		Polygon floor1 = new Polygon();

		floor1.add(new SpacedVector3(0, 0, 0));
		floor1.add(new SpacedVector3(0, 0, 10));
		floor1.add(new SpacedVector3(10, 0, 10));
		floor1.add(new SpacedVector3(10, 0, 0));
		floor1.add(new SpacedVector3(0, 0, 0));

		Box boundingBox = floor1.getBoundingBox();
		assertNotNull(boundingBox);
		assertEquals(5, boundingBox.getCenter().getX(), EPSILON);
		assertEquals(0, boundingBox.getCenter().getY(), EPSILON);
		assertEquals(5, boundingBox.getCenter().getZ(), EPSILON);
	}

	@Test
	public void boundingBoxOnlyOnePoint() throws Exception {
		Polygon pointPoly = new Polygon();
		pointPoly.add(new SpacedVector3(10, 0, 10));

		try {
			Box boundingBox = pointPoly.getBoundingBox();
			fail();
		} catch (IllegalStateException e) {

		}
	}
}
