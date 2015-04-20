package se.ardortech.math;

import com.ardor3d.math.Vector3;
import com.google.common.collect.Lists;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static se.ardortech.math.VectorMath.getNormal;
import static se.ardortech.math.VectorMath.standingIn;

public class VectorMathTest {
	private static final double EPSILON = 1e-8;

	@Test
	public void testGetDirection() {
		SpacedVector3 pos = new SpacedVector3(1, 2, 3);
		SpacedVector3 target = new SpacedVector3(4, 6, 3);
		SpacedVector3 direction = VectorMath.getDirection(pos, target);

		assertEquals(0.6, direction.getX(), EPSILON);
		assertEquals(0.8, direction.getY(), EPSILON);
		assertEquals(0, direction.getZ(), EPSILON);
	}

	@Test
	public void testMoveTowardsLocalSimple() {
		Vector3 pos = new Vector3();
		Vector3 target = new Vector3(10, 0, 0);
		double timeNotUsed = VectorMath.moveTowardsLocal(pos, target, 0.1, 5);
		assertEquals(0, timeNotUsed, EPSILON);
		assertEquals(0.5, pos.getX(), EPSILON);
		assertEquals(0, pos.getY(), EPSILON);
		assertEquals(0, pos.getZ(), EPSILON);
	}

	@Test
	public void testMoveTowardsFast() {
		SpacedVector3 pos = new SpacedVector3(0, 0, 0);
		SpacedVector3 target = new SpacedVector3(100, 0, 0);
		SpacedVector3 newPos = VectorMath.moveTowards(pos, target, 50.0, 1.0);

		assertEquals(50.0, newPos.getX(), EPSILON);
		assertEquals(0, newPos.getY(), EPSILON);
		assertEquals(0, newPos.getZ(), EPSILON);

		SpacedVector3 finalPos = VectorMath.moveTowards(newPos, target, 50.0, 1.5);

		assertEquals(100.0, finalPos.getX(), EPSILON);
		assertEquals(0, finalPos.getY(), EPSILON);
		assertEquals(0, finalPos.getZ(), EPSILON);

	}



	@Test
	public void testMoveTowardsLocalPastEndPoint() {
		Vector3 pos = new Vector3();
		Vector3 target = new Vector3(10, 0, 0);
		double timeNotUsed = VectorMath.moveTowardsLocal(pos, target, 5, 5);
		assertEquals(3, timeNotUsed, EPSILON);
		assertEquals(10.0, pos.getX(), EPSILON);
		assertEquals(0, pos.getY(), EPSILON);
		assertEquals(0, pos.getZ(), EPSILON);
	}


	@Test
	public void simpleLeftOf() throws Exception {
		SpacedVector3 viewpoint = new SpacedVector3(0, 0, 0);
		SpacedVector3 pointA = new SpacedVector3(30, 0, 0);
		SpacedVector3 pointB = new SpacedVector3(0, 0, 90);

		assertTrue(standingIn(viewpoint).is(pointA).leftOf(pointB));
		assertFalse(standingIn(viewpoint).is(pointB).leftOf(pointA));
	}

	@Test
	public void leftOf1() throws Exception {
		SpacedVector3 viewpoint = new SpacedVector3(10, 0, 10);
		SpacedVector3 pointA = new SpacedVector3(5, 0, 5);
		SpacedVector3 pointB = new SpacedVector3(7, 0, 6);

		assertTrue(standingIn(viewpoint).is(pointA).leftOf(pointB));
		assertFalse(standingIn(viewpoint).is(pointB).leftOf(pointA));
	}

	@Test
	public void leftOf2() throws Exception {
		SpacedVector3 viewpoint = new SpacedVector3(10, 0, 10);
		SpacedVector3 pointA = new SpacedVector3(12, 0, 5);
		SpacedVector3 pointB = new SpacedVector3(14, 0, 6);

		assertTrue(standingIn(viewpoint).is(pointA).leftOf(pointB));
		assertFalse(standingIn(viewpoint).is(pointB).leftOf(pointA));
	}

	@Test
	public void simpleNormal() throws Exception {
		SpacedVector3 normal = getNormal(SpacedVector3.ZERO, new SpacedVector3(1, 0, 0), new SpacedVector3(0, 0, 1));
		assertEquals(SpacedVector3.PLUS_J, normal);
	}

	@Test
	public void tiltedPlaneNormal() throws Exception {
		List<SpacedVector3> planePoints = Lists.newArrayList(
				new SpacedVector3(10, 0, 0),
				new SpacedVector3(10, 0, 10),
				new SpacedVector3(20, 5, 0));

		SpacedVector3 normal = VectorMath.getNormal(planePoints.get(0), planePoints.get(1), planePoints.get(2));
		SpacedVector3 n = new SpacedVector3(50, -100,0).normalize();
		assertEquals(n, normal);
	}

	@Test
	public void projectOntoXZPlane() throws Exception {
		SpacedVector3 projection = VectorMath.projectOntoPlane(new SpacedVector3(10, 10, 10),
				SpacedVector3.ZERO,
				new SpacedVector3(1, 0, 0),
				new SpacedVector3(0, 0, 1));

		assertEquals(new SpacedVector3(10, 0, 10), projection);
	}

	@Test
	public void nonTrivialProjection() throws Exception {
		SpacedVector3 projection = VectorMath.projectOntoPlane(new SpacedVector3(2, -1, 3),
				SpacedVector3.ZERO,
				new SpacedVector3(1.5, 1, 0.5),
				new SpacedVector3(-1 / 3.0, 1, -1 / 8.0));

		assertTrue(SpacedVector3.distance(new SpacedVector3(2.70445, -1.02348, .933603), projection) < 1e-4);
	}

	@Test
	public void tiltedPlaneProjection() throws Exception {
		List<SpacedVector3> planePoints = Lists.newArrayList(
				new SpacedVector3(10, 0, 0),
				new SpacedVector3(10, 0, 10),
				new SpacedVector3(20, 10, 0));
		SpacedVector3 projection = VectorMath.projectOntoPlane(new SpacedVector3(25, 5, 5),
				planePoints.get(0),
				planePoints.get(1),
				planePoints.get(2));

		assertTrue(SpacedVector3.distance(new SpacedVector3(15, 15, 5), projection) < 1e-4);
	}


	@Test
	public void trivialIntersection() throws Exception {
		SpacedVector3 intersection = VectorMath.findIntersection(SpacedVector3.ZERO, new SpacedVector3(10, 0, 10),
				new SpacedVector3(10, 0, 0), new SpacedVector3(0, 0, 10));
		assertEquals(new SpacedVector3(5, 0, 5), intersection);
	}
}
