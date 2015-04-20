package se.spaced.shared.world.walkmesh;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;
import se.ardortech.math.SpacedRotation;
import se.ardortech.math.SpacedVector3;
import se.fearlessgames.common.util.uuid.UUID;
import se.spaced.shared.world.AreaPoint;
import se.spaced.shared.world.area.Polygon;

import java.util.Iterator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class LocalSpaceConverterTest {

	private Walkmesh walkmesh;
	private Polygon polygon1;
	private Polygon polygon2;
	private static final double EPSILON = 1e-4;

	@Before
	public void setUp() throws Exception {
		walkmesh = new Walkmesh();

		polygon1 = new Polygon(UUID.fromString("8be3650b-c819-4b38-8fb8-a01404c54e1f"),
				Lists.newArrayList(
						new SpacedVector3(0, 0, 0),
						new SpacedVector3(10, 0, 0),
						new SpacedVector3(10, 0, 10),
						new SpacedVector3(0, 0, 10),
						new SpacedVector3(0, 0, 0))
		);
		walkmesh.addPolygon(polygon1);

		polygon2 = new Polygon(UUID.fromString("a40697d4-0d8c-4d43-9da3-a01404c5f5f9"),
				Lists.newArrayList(
						new SpacedVector3(10, 0, 0),
						new SpacedVector3(20, 0, 0),
						new SpacedVector3(20, 0, 10),
						new SpacedVector3(10, 0, 10),
						new SpacedVector3(10, 0, 0))
		);
		walkmesh.addPolygon(polygon2);

		walkmesh.addConnection(WalkmeshConnection.create(polygon1, polygon2));
	}

	@Test
	public void convertWithTranslation() throws Exception {

		LocalSpaceConverter converter =  new LocalSpaceConverter(new SpacedVector3(20, 10, -10),
				SpacedRotation.IDENTITY);
		Walkmesh localSpaceMesh = converter.convert(walkmesh);

		ImmutableSet<Polygon> polygons = localSpaceMesh.getPolygons();
		assertEquals(2, polygons.size());

		Polygon localPolygon1 = Iterables.find(polygons, new Predicate<Polygon>() {
			@Override
			public boolean apply(Polygon walkmeshPolygon) {
				return walkmeshPolygon.getId().equals(polygon1.getId());
			}
		});
		Polygon localPolygon2 = Iterables.find(polygons, new Predicate<Polygon>() {
			@Override
			public boolean apply(Polygon walkmeshPolygon) {
				return walkmeshPolygon.getId().equals(polygon2.getId());
			}
		});

		assertEquals(new SpacedVector3(-20, -10, 10), localPolygon1.get(0));
		assertEquals(new SpacedVector3(-10, -10, 10), localPolygon1.get(1));
		assertEquals(new SpacedVector3(-10, -10, 20), localPolygon1.get(2));
		assertEquals(new SpacedVector3(-20, -10, 20), localPolygon1.get(3));

		assertEquals(new SpacedVector3(-10, -10, 10), localPolygon2.get(0));
		assertEquals(new SpacedVector3(  0, -10, 10), localPolygon2.get(1));
		assertEquals(new SpacedVector3(  0, -10, 20), localPolygon2.get(2));
		assertEquals(new SpacedVector3(-10, -10, 20), localPolygon2.get(3));

		ImmutableSet<WalkmeshConnection> connections = localSpaceMesh.getConnections();
		WalkmeshConnection connection = Iterables.get(connections, 0);

		assertEquals(polygon1.getId(), connection.getFrom());
		assertEquals(polygon2.getId(), connection.getTo());

		ImmutableList<AreaPoint> connectionAreaPoints = connection.getAreaPoints();
		AreaPoint point1 = Iterables.find(connectionAreaPoints, new Predicate<AreaPoint>() {
			@Override
			public boolean apply(AreaPoint point) {
				return point.getPoint().equals(new SpacedVector3(-10, -10, 10));
			}
		});
		assertNotNull(point1);

		AreaPoint point2 = Iterables.find(connectionAreaPoints, new Predicate<AreaPoint>() {
			@Override
			public boolean apply(AreaPoint point) {
				return point.getPoint().equals(new SpacedVector3(-10, -10, 20));
			}
		});
		assertNotNull(point2);
	}

	@Test
	public void convertWithRotation() throws Exception {

		LocalSpaceConverter converter = new LocalSpaceConverter(new SpacedVector3(0, 0, 0),
				new SpacedRotation(SpacedVector3.PLUS_J, Math.PI / 4));
		Walkmesh localSpaceMesh = converter.convert(walkmesh);


		ImmutableSet<Polygon> polygons = localSpaceMesh.getPolygons();
		Polygon localPolygon1 = Iterables.find(polygons, new Predicate<Polygon>() {
			@Override
			public boolean apply(Polygon walkmeshPolygon) {
				return walkmeshPolygon.getId().equals(polygon1.getId());
			}
		});
		Polygon localPolygon2 = Iterables.find(polygons, new Predicate<Polygon>() {
			@Override
			public boolean apply(Polygon walkmeshPolygon) {
				return walkmeshPolygon.getId().equals(polygon2.getId());
			}
		});

		assertVectorEquals(new SpacedVector3(0, 0, 0), localPolygon1.get(0));
		assertVectorEquals(new SpacedVector3(7.071068, 0.000000, -7.071068), localPolygon1.get(1));
		assertVectorEquals(new SpacedVector3(14.142136, 0.000000, -0.000000), localPolygon1.get(2));
		assertVectorEquals(new SpacedVector3(7.071068, 0.000000, 7.071068), localPolygon1.get(3));

		assertVectorEquals(new SpacedVector3(7.071068, 0.000000, -7.071068), localPolygon2.get(0));
		assertVectorEquals(new SpacedVector3(14.142136, 0.000000, -14.142136), localPolygon2.get(1));
		assertVectorEquals(new SpacedVector3(21.213203, 0.000000, -7.071068), localPolygon2.get(2));
		assertVectorEquals(new SpacedVector3(14.142136, 0.000000, -0.000000), localPolygon2.get(3));

		ImmutableSet<WalkmeshConnection> connections = localSpaceMesh.getConnections();
		WalkmeshConnection connection = Iterables.get(connections, 0);

		assertEquals(polygon1.getId(), connection.getFrom());
		assertEquals(polygon2.getId(), connection.getTo());

		ImmutableList<AreaPoint> connectionAreaPoints = connection.getAreaPoints();
		AreaPoint point1 = Iterables.find(connectionAreaPoints, new Predicate<AreaPoint>() {
			@Override
			public boolean apply(AreaPoint point) {
				return SpacedVector3.distance(point.getPoint(), new SpacedVector3(7.071068, 0.000000, -7.071068)) < 0.1;
			}
		});
		assertNotNull(point1);

		AreaPoint point2 = Iterables.find(connectionAreaPoints, new Predicate<AreaPoint>() {
			@Override
			public boolean apply(AreaPoint point) {
				return SpacedVector3.distance(point.getPoint(), new SpacedVector3(14.142136, 0.000000, -0.000000)) < 0.1;
			}
		});
		assertNotNull(point2);
	}

	private void assertVectorEquals(SpacedVector3 vector1, SpacedVector3 vector2) {
		assertEquals("x", vector1.getX(), vector2.getX(), EPSILON);
		assertEquals("y", vector1.getY(), vector2.getY(), EPSILON);
		assertEquals("z", vector1.getZ(), vector2.getZ(), EPSILON);
	}

	@Test
	public void convertRotation() throws Exception {
		SpacedRotation localSpace = new SpacedRotation(SpacedVector3.PLUS_J, Math.PI/2);
		LocalSpaceConverter converter = new LocalSpaceConverter(SpacedVector3.ZERO, localSpace);
		SpacedRotation result = converter.convert(SpacedRotation.IDENTITY);
		assertEquals(new SpacedRotation(SpacedVector3.PLUS_J, -Math.PI/2), result);

	}

	@Test
	public void convertWalkmeshBackAgain() throws Exception {
		SpacedRotation localSpace = new SpacedRotation(SpacedVector3.PLUS_J, Math.PI/2);
		LocalSpaceConverter converter =  new LocalSpaceConverter(new SpacedVector3(20, 10, -10),
				localSpace);
		Walkmesh localSpaceMesh = converter.convert(walkmesh);
		Walkmesh inverse = converter.inverse(localSpaceMesh);

		assertEqualsPolygons(walkmesh.getPolygons(), inverse.getPolygons());

		//assertEquals(walkmesh.getConnections(), inverse.getConnections());
	}

	private void assertEqualsPolygons(Iterable<Polygon> polygons1, Iterable<Polygon> polygons2) {
		Iterator<Polygon> i1 = polygons1.iterator();
		Iterator<Polygon> i2 = polygons2.iterator();
		while (i1.hasNext() && i2.hasNext() ) {
			Polygon polygon1 = i1.next();
			Polygon polygon2 = i2.next();
			assertEqualsPolygon(polygon1, polygon2);
		}
	}

	private void assertEqualsPolygon(Polygon polygon1, Polygon polygon2) {
		assertEquals(polygon1.size(), polygon2.size());
		ImmutableList<SpacedVector3> points1 = polygon1.getPoints();
		ImmutableList<SpacedVector3> points2 = polygon2.getPoints();
		for (int i = 0, pointsSize = points1.size(); i < pointsSize; i++) {
			SpacedVector3 v1 = points1.get(i);
			SpacedVector3 v2 = points2.get(i);
			assertVectorEquals(v1, v2);
		}
	}
}
