package se.spaced.shared.world.area;

import org.junit.Before;
import org.junit.Test;
import se.ardortech.math.SpacedVector3;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PathPlanner3dTest {

	private PathPlanner pathPlanner;

	@Before
	public void setUp() {
		PolygonGraph graph = new PolygonGraph();

		Polygon floor1 = new Polygon() {
			@Override
			public String toString() {
				return "floor1";
			}
		};
		floor1.add(new SpacedVector3(0, 0, 0));
		floor1.add(new SpacedVector3(0, 0, 10));
		floor1.add(new SpacedVector3(10, 0, 10));
		floor1.add(new SpacedVector3(10, 0, 0));
		floor1.add(new SpacedVector3(0, 0, 0));

		graph.addPolygon(floor1);

		Polygon ramp = new Polygon() {
			@Override
			public String toString() {
				return "ramp";
			}
		};
		ramp.add(new SpacedVector3(10, 0, 0));
		ramp.add(new SpacedVector3(10, 0, 10));
		ramp.add(new SpacedVector3(20, 5, 10));
		ramp.add(new SpacedVector3(20, 5, 0));
		ramp.add(new SpacedVector3(10, 0, 0));

		graph.addPolygon(ramp);

		Polygon floor2a = new Polygon() {
			@Override
			public String toString() {
				return "floor2a";
			}
		};
		floor2a.add(new SpacedVector3(20, 5, 0));
		floor2a.add(new SpacedVector3(20, 5, 10));
		floor2a.add(new SpacedVector3(20, 5, 20));
		floor2a.add(new SpacedVector3(30, 5, 20));
		floor2a.add(new SpacedVector3(30, 5, 0));
		floor2a.add(new SpacedVector3(20, 5, 0));
		graph.addPolygon(floor2a);

		Polygon floor2b = new Polygon() {
			@Override
			public String toString() {
				return "floor2b";
			}
		};
		floor2b.add(new SpacedVector3(20, 5, 10));
		floor2b.add(new SpacedVector3(20, 5, 20));
		floor2b.add(new SpacedVector3(0, 5, 10));
		floor2b.add(new SpacedVector3(0, 5, 0));
		floor2b.add(new SpacedVector3(10, 5, 0));
		floor2b.add(new SpacedVector3(20, 5, 10));

		graph.addPolygon(floor2b);

		graph.addBidirectionalConnection(floor1, ramp);
		graph.addBidirectionalConnection(ramp, floor2a);
		graph.addBidirectionalConnection(floor2a, floor2b);
		pathPlanner = new PathPlanner(graph);
	}

	@Test
	public void pathUptheRamp() throws Exception {
		SpacedVector3 origin = new SpacedVector3(5, 0, 5);
		SpacedVector3 targetPos = new SpacedVector3(25, 5, 5);
		double startDistance = SpacedVector3.distance(origin, targetPos);
		SpacedVector3 nextWayPoint = pathPlanner.getNextWayPoint(origin, targetPos, "");
		assertEquals(new SpacedVector3(10, 0, 5), nextWayPoint);
		double distanceToTarget = SpacedVector3.distance(nextWayPoint, targetPos);
		assertTrue(distanceToTarget < startDistance);

		pathAllTheWayToTarget(nextWayPoint, targetPos, 50);
	}

	@Test
	public void pathUptheRampStandingCloseToEdge() throws Exception {
		SpacedVector3 origin = new SpacedVector3(9.9999, 0, 5);
		SpacedVector3 targetPos = new SpacedVector3(25, 5, 5);
		SpacedVector3 nextWayPoint = pathPlanner.getNextWayPoint(origin, targetPos, "");
		assertEquals(new SpacedVector3(20, 5, 5), nextWayPoint);
	}

	@Test
	public void pathUptheRampInAnAngle() throws Exception {
		SpacedVector3 origin = new SpacedVector3(10, 0, 0);
		SpacedVector3 targetPos = new SpacedVector3(29.999999, 5, 10);
		SpacedVector3 nextWayPoint = pathPlanner.getNextWayPoint(origin, targetPos, "");
		assertEquals(new SpacedVector3(20, 5, 5.555555802469147), nextWayPoint);
	}

	@Test
	public void pathUptheRampInAnAngle2() throws Exception {
		SpacedVector3 origin = new SpacedVector3(15, 2.5, 0);
		SpacedVector3 targetPos = new SpacedVector3(29.999999, 5, 15);
		SpacedVector3 nextWayPoint = pathPlanner.getNextWayPoint(origin, targetPos, "");
		assertEquals(new SpacedVector3(20.0, 5.0, 5.7692311242603775), nextWayPoint);
	}



	@Test
	public void pathUpstairs() throws Exception {
		SpacedVector3 origin = new SpacedVector3(5, 0, 5);
		SpacedVector3 targetPos = new SpacedVector3(5, 5, 5);
		double startDistance = SpacedVector3.distance(origin, targetPos);
		SpacedVector3 nextWayPoint = pathPlanner.getNextWayPoint(origin, targetPos, "");
		double distanceToTarget = SpacedVector3.distance(nextWayPoint, targetPos);
		assertTrue(distanceToTarget > startDistance);

		pathAllTheWayToTarget(nextWayPoint, targetPos, 50);
	}

	private void pathAllTheWayToTarget(
			SpacedVector3 startPoint,
			SpacedVector3 targetPos,
			int maxIterations) {
		double distanceToTarget = Double.MAX_VALUE;
		for (int i = 0; i < maxIterations && distanceToTarget != 0; i++) {
			startPoint = pathPlanner.getNextWayPoint(startPoint, targetPos, "");
			distanceToTarget = SpacedVector3.distance(startPoint, targetPos);
		}
		assertEquals(startPoint, targetPos);
	}
}
