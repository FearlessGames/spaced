package se.spaced.shared.world.area;

import org.junit.Before;
import org.junit.Test;
import se.ardortech.math.SpacedVector3;
import se.hiflyer.paparazzo.impl.SimplePath;

import static org.junit.Assert.assertEquals;

public class PathPlannerTest {

	private static final double EPSILON = 1e-8;

	private PathPlanner pathPlanner;
	private PolygonGraph polygonGraph;
	private Polygon p1;
	private Polygon p2;
	private Polygon p3;

	@Before
	public void setUp() throws Exception {
		polygonGraph = new PolygonGraph();

		p1 = new Polygon();

		p1.add(new SpacedVector3(10, 0, 13));
		p1.add(new SpacedVector3(30, 0, 15));
		p1.add(new SpacedVector3(45, 0, 3));
		p1.add(new SpacedVector3(27, 0, 0));
		p1.add(new SpacedVector3(2, 0, 5));
		p1.add(new SpacedVector3(10, 0, 13));

		polygonGraph.addPolygon(p1);

		p2 = new Polygon();

		p2.add(new SpacedVector3(30, 0, 15));
		p2.add(new SpacedVector3(57, 0, 22));
		p2.add(new SpacedVector3(60, 0, 13));
		p2.add(new SpacedVector3(45, 0, 3));
		p2.add(new SpacedVector3(30, 0, 15));

		polygonGraph.addPolygon(p2);

		p3 = new Polygon();

		p3.add(new SpacedVector3(57, 0, 22));
		p3.add(new SpacedVector3(73, 0, 20));
		p3.add(new SpacedVector3(78, 0, 10));
		p3.add(new SpacedVector3(67, 0, 5));
		p3.add(new SpacedVector3(60, 0, 13));
		p3.add(new SpacedVector3(57, 0, 22));
		polygonGraph.addPolygon(p3);
		pathPlanner = new PathPlanner(polygonGraph);
	}

	@Test
	public void getPointInSamePolygon() throws Exception {
		SpacedVector3 target = new SpacedVector3(30, 0, 14);
		SpacedVector3 next = pathPlanner.getNextWayPoint(new SpacedVector3(25, 0, 3), target, "");
		assertEquals(target, next);
	}

	@Test
	public void getPointInNeighbourStraightLine() throws Exception {
		SpacedVector3 target = new SpacedVector3(50, 0, 15);
		SpacedVector3 start = new SpacedVector3(25, 0, 3);

		polygonGraph.addBidirectionalConnection(p1, p2);
		polygonGraph.addBidirectionalConnection(p2, p3);
		SpacedVector3 next = pathPlanner.getNextWayPoint(start, target, "");
		assertEquals(new SpacedVector3(37.5, 0, 9), next);
	}

	@Test
	public void getNextWayPointUsesPortals() throws Exception {
		SpacedVector3 startPos = new SpacedVector3(70, 0, 7);
		SpacedVector3 targetPos = new SpacedVector3(46, 0, 7);
		SimplePath<Polygon> path = new SimplePath<Polygon>(p3);
		path.add(p2);
		polygonGraph.addNaturalConnection(p3, p2);
		SpacedVector3 nextPoint = pathPlanner.extractWaypointFromPath(startPos, targetPos, path);
		// new SpacedVector3(60, 0, 13) without gate inset

		assertEquals(59.778640563, nextPoint.getX(), EPSILON);
		assertEquals(0.0, nextPoint.getY(), EPSILON);
		assertEquals(13.664078308, nextPoint.getZ(), EPSILON);
	}
}
