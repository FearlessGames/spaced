package se.spaced.shared.world.area;

import com.google.common.collect.Iterables;
import org.junit.Before;
import org.junit.Test;
import se.ardortech.math.SpacedVector3;

import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class PolygonGraph3dTest {

	private PolygonGraph graph;
	private Polygon floor1;
	private Polygon ramp;
	private Polygon floor2a;
	private Polygon floor2b;

	@Before
	public void setUp() {
		graph = new PolygonGraph();

		floor1 = new Polygon() {
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

		ramp = new Polygon() {
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

		floor2a = new Polygon() {
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

		floor2b = new Polygon() {
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
	}

	@Test
	public void getAll() throws Exception {
		Collection<Polygon> allPolygons = graph.getAllPolygons();
		assertEquals(4, allPolygons.size());
	}

	@Test
	public void getNeighbourSimple() throws Exception {
		graph.addBidirectionalConnection(floor1, ramp);
		graph.addBidirectionalConnection(ramp, floor2a);
		graph.addBidirectionalConnection(floor2a, floor2b);

		Iterable<Polygon> neighbours = graph.getNeighbours(floor1);
		assertTrue(Iterables.contains(neighbours, ramp));
		assertEquals(1, Iterables.size(neighbours));
	}


	@Test
	public void getByPointFloor1() throws Exception {
		Polygon polygon = graph.getPolygon(new SpacedVector3(5, 0, 5));
		assertNotNull(polygon);
		assertEquals(floor1, polygon);
	}

	@Test
	public void getByPointFloor2b() throws Exception {
		Polygon polygon = graph.getPolygon(new SpacedVector3(5, 5, 5));
		assertNotNull(polygon);
		assertEquals(floor2b, polygon);
	}

	@Test
	public void getByPointJumpingFloor1() throws Exception {
		Polygon polygon = graph.getPolygon(new SpacedVector3(5, 0, 5));
		assertEquals(floor1, polygon);

		polygon = graph.getPolygon(new SpacedVector3(5, 1, 5));
		assertEquals(floor1, polygon);

		polygon = graph.getPolygon(new SpacedVector3(5, 2, 5));
		assertEquals(floor1, polygon);

		polygon = graph.getPolygon(new SpacedVector3(5, 3, 5));
		assertEquals(floor1, polygon);

		polygon = graph.getPolygon(new SpacedVector3(5, 4, 5));
		assertEquals(floor1, polygon);

		polygon = graph.getPolygon(new SpacedVector3(5, 4.6, 5));
		assertEquals(floor2b, polygon);
	}


	@Test
	public void getByPointFailsWrongLevel() throws Exception {
		Polygon polygon = graph.getPolygon(new SpacedVector3(25, 0, 15));
		assertTrue(floor2a.equals(polygon));
	}
}
