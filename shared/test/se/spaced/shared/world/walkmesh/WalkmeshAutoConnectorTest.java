package se.spaced.shared.world.walkmesh;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;
import se.ardortech.math.SpacedVector3;
import se.fearlessgames.common.util.uuid.UUIDFactory;
import se.fearlessgames.common.util.uuid.UUIDMockFactory;
import se.spaced.shared.world.area.Polygon;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class WalkmeshAutoConnectorTest {

	private Walkmesh walkmesh;
	private UUIDFactory uuidFactory;

	@Before
	public void setUp() throws Exception {
		uuidFactory = new UUIDMockFactory();

		List<SpacedVector3> floor1Points = Lists.newArrayList();
		floor1Points.add(new SpacedVector3(0, 0, 0));
		floor1Points.add(new SpacedVector3(0, 0, 10));
		floor1Points.add(new SpacedVector3(10, 0, 10));
		floor1Points.add(new SpacedVector3(10, 0, 0));
		floor1Points.add(new SpacedVector3(0, 0, 0));

		Polygon floor1 = new Polygon(uuidFactory.randomUUID(), floor1Points);

		List<SpacedVector3> rampPoints = Lists.newArrayList();
		rampPoints.add(new SpacedVector3(10, 0, 0));
		rampPoints.add(new SpacedVector3(10, 0, 10));
		rampPoints.add(new SpacedVector3(20, 5, 10));
		rampPoints.add(new SpacedVector3(20, 5, 0));
		rampPoints.add(new SpacedVector3(10, 0, 0));

		Polygon ramp = new Polygon(uuidFactory.randomUUID(), rampPoints);

		List<SpacedVector3> floor2aPoints = Lists.newArrayList();

		floor2aPoints.add(new SpacedVector3(20, 5, 0));
		floor2aPoints.add(new SpacedVector3(20, 5, 10));
		floor2aPoints.add(new SpacedVector3(20, 5, 20));
		floor2aPoints.add(new SpacedVector3(30, 5, 20));
		floor2aPoints.add(new SpacedVector3(30, 5, 0));
		floor2aPoints.add(new SpacedVector3(20, 5, 0));

		Polygon floor2a = new Polygon(uuidFactory.randomUUID(), floor2aPoints);

		List<SpacedVector3> floor2bPoints = Lists.newArrayList();

		floor2bPoints.add(new SpacedVector3(20, 5, 10));
		floor2bPoints.add(new SpacedVector3(20, 5, 20));
		floor2bPoints.add(new SpacedVector3(0, 5, 10));
		floor2bPoints.add(new SpacedVector3(0, 5, 0));
		floor2bPoints.add(new SpacedVector3(10, 5, 0));
		floor2bPoints.add(new SpacedVector3(20, 5, 10));

		Polygon floor2b = new Polygon(uuidFactory.randomUUID(), floor2bPoints);

		walkmesh = new Walkmesh();
		walkmesh.addPolygon(floor1);
		walkmesh.addPolygon(ramp);
		walkmesh.addPolygon(floor2a);
		walkmesh.addPolygon(floor2b);
	}

	@Test
	public void simpleCase() throws Exception {
		Iterable<List<Polygon>> adjecent = WalkmeshAutoConnector.extractAdjecentPolygons(walkmesh);
		assertEquals(6, Iterables.size(adjecent));
	}

	@Test
	public void includingInexactMatchings() throws Exception {
		Iterable<SpacedVector3> points = Lists.newArrayList(new SpacedVector3(30, 5, 0.45), new SpacedVector3(35, 5, 0),
				new SpacedVector3(35, 5, 20), new SpacedVector3(30, 5, 20.45));
		Polygon floor2c = new Polygon(uuidFactory.randomUUID(), points);
		floor2c.close();
		walkmesh.addPolygon(floor2c);

		Iterable<List<Polygon>> adjecent = WalkmeshAutoConnector.extractAdjecentPolygons(walkmesh);
		assertEquals(8, Iterables.size(adjecent));
	}
}
