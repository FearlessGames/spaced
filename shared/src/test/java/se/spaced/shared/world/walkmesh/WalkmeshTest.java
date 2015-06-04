package se.spaced.shared.world.walkmesh;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.junit.Test;
import se.ardortech.math.SpacedRotation;
import se.ardortech.math.SpacedVector3;
import se.fearless.common.uuid.UUID;
import se.spaced.shared.world.area.Polygon;
import se.spaced.shared.world.area.PolygonGraph;

import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;

public class WalkmeshTest {
	@Test
	public void createPolyhonGraph() throws Exception {
		Walkmesh walkmesh = new Walkmesh();
		
		Polygon polygon1 = new Polygon(UUID.fromString("8be3650b-c819-4b38-8fb8-a01404c54e1f"),
				Lists.newArrayList(
						new SpacedVector3(0, 0, 0),
						new SpacedVector3(10, 0, 0),
						new SpacedVector3(10, 0, 10),
						new SpacedVector3(0, 0, 10),
						new SpacedVector3(0, 0, 0))
		);
		walkmesh.addPolygon(polygon1);

		Polygon polygon2 = new Polygon(UUID.fromString("a40697d4-0d8c-4d43-9da3-a01404c5f5f9"),
				Lists.newArrayList(
						new SpacedVector3(10, 0, 0),
						new SpacedVector3(20, 0, 0),
						new SpacedVector3(20, 0, 10),
						new SpacedVector3(10, 0, 10),
						new SpacedVector3(10, 0, 0))
		);
		walkmesh.addPolygon(polygon2);

		walkmesh.addConnection(WalkmeshConnection.create(polygon1, polygon2));

		PolygonGraph graph = walkmesh.addToPolygonGraph(new SpacedVector3(0, 0, 0), SpacedRotation.IDENTITY, new PolygonGraph());
		Collection<Polygon> allPolygons = graph.getAllPolygons();
		assertEquals(2, allPolygons.size());

		Polygon graphPolygon1 = graph.getPolygon(new SpacedVector3(5, 0, 5));
		assertNotNull(graphPolygon1);

		Polygon graphPolygon2 = graph.getPolygon(new SpacedVector3(15, 0, 5));
		assertNotNull(graphPolygon2);
		assertNotSame(graphPolygon1, graphPolygon2);

		Polygon restOfTheWorld = graph.getPolygon(new SpacedVector3(25, 0, 25));
		assertNotNull(restOfTheWorld);
		assertNotSame(restOfTheWorld, graphPolygon1);
		assertNotSame(restOfTheWorld, graphPolygon2);

		Iterable<Polygon> neighbours = graph.getNeighbours(graphPolygon1);
		assertEquals(1, Iterables.size(neighbours));
	}

	@Test
	public void createPolygonGraphAwayFromOrigo() throws Exception {
		Walkmesh walkmesh = new Walkmesh();

		Polygon polygon1 = new Polygon(UUID.fromString("8be3650b-c819-4b38-8fb8-a01404c54e1f"),
				Lists.newArrayList(
						new SpacedVector3(0, 0, 0),
						new SpacedVector3(10, 0, 0),
						new SpacedVector3(10, 0, 10),
						new SpacedVector3(0, 0, 10),
						new SpacedVector3(0, 0, 0))
		);
		walkmesh.addPolygon(polygon1);

		Polygon polygon2 = new Polygon(UUID.fromString("a40697d4-0d8c-4d43-9da3-a01404c5f5f9"),
				Lists.newArrayList(
						new SpacedVector3(10, 0, 0),
						new SpacedVector3(20, 0, 0),
						new SpacedVector3(20, 0, 10),
						new SpacedVector3(10, 0, 10),
						new SpacedVector3(10, 0, 0))
		);
		walkmesh.addPolygon(polygon2);

		walkmesh.addConnection(WalkmeshConnection.create(polygon1, polygon2));

		PolygonGraph graph = walkmesh.addToPolygonGraph(new SpacedVector3(100, -40, 200), SpacedRotation.IDENTITY, new PolygonGraph());

		Collection<Polygon> allPolygons = graph.getAllPolygons();
		assertEquals(2, allPolygons.size());

		Polygon graphPolygon1 = graph.getPolygon(new SpacedVector3(105, -40, 205));
		assertNotNull(graphPolygon1);

		Polygon graphPolygon2 = graph.getPolygon(new SpacedVector3(115, -40, 205));
		assertNotNull(graphPolygon2);
		assertNotSame(graphPolygon1, graphPolygon2);

		Polygon restOfTheWorld = graph.getPolygon(new SpacedVector3(25, 0, 25));
		assertNotNull(restOfTheWorld);
		assertNotSame(restOfTheWorld, graphPolygon1);
		assertNotSame(restOfTheWorld, graphPolygon2);

		Iterable<Polygon> neighbours = graph.getNeighbours(graphPolygon1);
		assertEquals(1, Iterables.size(neighbours));
	}


	@Test
	public void createPolygonGraphRotatedAndAwayFromOrigo() throws Exception {
		Walkmesh walkmesh = new Walkmesh();

		Polygon polygon1 = new Polygon(UUID.fromString("8be3650b-c819-4b38-8fb8-a01404c54e1f"),
				Lists.newArrayList(
						new SpacedVector3(0, 0, 0),
						new SpacedVector3(10, 0, 0),
						new SpacedVector3(10, 0, 10),
						new SpacedVector3(0, 0, 10),
						new SpacedVector3(0, 0, 0))
		);
		walkmesh.addPolygon(polygon1);

		Polygon polygon2 = new Polygon(UUID.fromString("a40697d4-0d8c-4d43-9da3-a01404c5f5f9"),
				Lists.newArrayList(
						new SpacedVector3(10, 0, 0),
						new SpacedVector3(20, 0, 0),
						new SpacedVector3(20, 0, 10),
						new SpacedVector3(10, 0, 10),
						new SpacedVector3(10, 0, 0))
		);
		walkmesh.addPolygon(polygon2);

		walkmesh.addConnection(WalkmeshConnection.create(polygon1, polygon2));

		PolygonGraph graph = walkmesh.addToPolygonGraph(new SpacedVector3(100, -40, 200), new SpacedRotation(SpacedVector3.PLUS_J, Math.PI), new PolygonGraph());

		Collection<Polygon> allPolygons = graph.getAllPolygons();
		assertEquals(2, allPolygons.size());

		Polygon graphPolygon1 = graph.getPolygon(new SpacedVector3(95, -40, 195));
		assertNotNull(graphPolygon1);

		Polygon graphPolygon2 = graph.getPolygon(new SpacedVector3(85, -40, 195));
		assertNotNull(graphPolygon2);
		assertNotSame(graphPolygon1, graphPolygon2);

		Polygon restOfTheWorld = graph.getPolygon(new SpacedVector3(105, -40, 205));
		assertNotNull(restOfTheWorld);
		assertNotSame(restOfTheWorld, graphPolygon1);
		assertNotSame(restOfTheWorld, graphPolygon2);

		Iterable<Polygon> neighbours = graph.getNeighbours(graphPolygon1);
		assertEquals(1, Iterables.size(neighbours));
	}
}
