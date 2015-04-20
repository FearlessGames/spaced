package se.spaced.shared.world.area;

import com.google.common.collect.Iterables;
import org.junit.Before;
import org.junit.Test;
import se.ardortech.math.SpacedVector3;

import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class PolygonGraphTest {

	private PolygonGraph graph;
	private Polygon p1;
	private Polygon p2;
	private Polygon p3;

	@Before
	public void setUp() {
		graph = new PolygonGraph();

		p1 = new Polygon();

		p1.add(new SpacedVector3(10, 0, 13));
		p1.add(new SpacedVector3(30, 0, 15));
		p1.add(new SpacedVector3(45, 0, 3));
		p1.add(new SpacedVector3(27, 0, 0));
		p1.add(new SpacedVector3(2, 0, 5));
		p1.add(new SpacedVector3(10, 0, 13));

		graph.addPolygon(p1);

		p2 = new Polygon();

		p2.add(new SpacedVector3(30, 0, 15));
		p2.add(new SpacedVector3(57, 0, 22));
		p2.add(new SpacedVector3(60, 0, 13));
		p2.add(new SpacedVector3(45, 0, 3));
		p2.add(new SpacedVector3(30, 0, 15));

		graph.addPolygon(p2);

		p3 = new Polygon();

		p3.add(new SpacedVector3(57, 0, 22));
		p3.add(new SpacedVector3(73, 0, 20));
		p3.add(new SpacedVector3(78, 0, 10));
		p3.add(new SpacedVector3(67, 0, 5));
		p3.add(new SpacedVector3(60, 0, 13));
		p3.add(new SpacedVector3(57, 0, 22));
		graph.addPolygon(p3);
	}

	@Test
	public void getAll() throws Exception {
		Collection<Polygon> allPolygons = graph.getAllPolygons();
		assertEquals(3, allPolygons.size());
	}

	@Test
	public void getNeighbourSimple() throws Exception {
		graph.addNaturalConnection(p1, p2);
		graph.addNaturalConnection(p2, p3);

		Iterable<Polygon> neighbours = graph.getNeighbours(p1);
		assertTrue(Iterables.contains(neighbours, p2));
		assertEquals(1, Iterables.size(neighbours));
	}

	@Test
	public void nonReflexive() throws Exception {
		graph.addNaturalConnection(p1, p2);
		graph.addNaturalConnection(p2, p3);

		Iterable<Polygon> neighbours3 = graph.getNeighbours(p3);
		assertEquals(0, Iterables.size(neighbours3));

		Iterable<Polygon> neighbours2 = graph.getNeighbours(p2);
		assertEquals(1, Iterables.size(neighbours2));
		assertEquals(p3, Iterables.getOnlyElement(neighbours2));
	}

	@Test
	public void connectReflexive() throws Exception {
		graph.addBidirectionalConnection(p1, p2);
		graph.addBidirectionalConnection(p2, p3);

		Iterable<Polygon> neighbours3 = graph.getNeighbours(p3);
		assertEquals(1, Iterables.size(neighbours3));
		assertEquals(p2, Iterables.getOnlyElement(neighbours3));

		Iterable<Polygon> neighbours2 = graph.getNeighbours(p2);
		assertEquals(2, Iterables.size(neighbours2));
		assertTrue(Iterables.contains(neighbours2, p1));
		assertTrue(Iterables.contains(neighbours2, p3));
	}

	@Test
	public void getByPoint() throws Exception {
		Polygon polygon = graph.getPolygon(new SpacedVector3(45, 0, 10));
		assertNotNull(polygon);
		assertEquals(p2, polygon);
	}

	@Test
	public void getByPoint2() throws Exception {
		Polygon polygon = graph.getPolygon(new SpacedVector3(70, 0, 20));
		assertNotNull(polygon);
		assertEquals(p3, polygon);
	}

	@Test
	public void getByPointFails() throws Exception {
		Polygon polygon = graph.getPolygon(new SpacedVector3(40, 0, 20));
		assertNotNull(polygon);
		assertFalse(p1.equals(polygon));
		assertFalse(p2.equals(polygon));
		assertFalse(p3.equals(polygon));
	}
}
