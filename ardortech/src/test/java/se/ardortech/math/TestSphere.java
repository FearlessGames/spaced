package se.ardortech.math;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TestSphere {
	private Sphere sphere;

	@Before
	public void setup() {
		sphere = new Sphere(new SpacedVector3(0, 0, 0), 5000);
	}

	@Test
	public void testIsInLoadRange() throws Exception {
		assertTrue(sphere.isInside(new SpacedVector3(4000, 0, 0), 100));
		assertFalse(sphere.isInside(new SpacedVector3(4950, 0, 0), 100));
	}

	@Test
	public void testIsInside() throws Exception {
		assertTrue(sphere.isInside(new SpacedVector3(4000, 0, 0), 0));
		assertTrue(sphere.isInside(new SpacedVector3(4999, 0, 0), 0));
		assertFalse(sphere.isInside(new SpacedVector3(5001, 0, 0), 0));
	}
}