package se.spaced.server.model.spawn.area;

import com.ardor3d.math.Vector2;
import org.junit.Before;
import org.junit.Test;
import se.ardortech.math.SpacedRotation;
import se.ardortech.math.SpacedVector3;
import se.fearless.common.uuid.UUIDFactoryImpl;
import se.spaced.shared.util.random.RandomProvider;
import se.spaced.shared.util.random.RealRandomProvider;
import se.spaced.shared.world.area.Polygon;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class PolygonSpaceSpawnAreaTest {
	PolygonSpaceSpawnArea polygonSpaceSpawnArea;
	private Polygon polygon;

	@Before
	public void setUp() {
		polygon = new Polygon();

		polygon.add(new SpacedVector3(10, 0, 10));
		polygon.add(new SpacedVector3(5, 0, 0));
		polygon.add(new SpacedVector3(-10, 0, 10));
		polygon.add(new SpacedVector3(-10, 0, -10));
		polygon.add(new SpacedVector3(10, 0, -10));

		SpacedRotation rotation = new SpacedRotation(2, 3, 4, 1, true);
		RandomProvider randomProvider = new RealRandomProvider();

		polygonSpaceSpawnArea = new PolygonSpaceSpawnArea(UUIDFactoryImpl.INSTANCE.combUUID(),
				polygon,
				rotation,
				randomProvider);
	}

	@Test
	public void getSpawnPoint() {
		SpawnPoint spawnPoint = polygonSpaceSpawnArea.getNextSpawnPoint();
		assertNotNull(spawnPoint);
		assertTrue(polygon.containsPoint(new Vector2(spawnPoint.getPosition().getX(), spawnPoint.getPosition().getZ())));
	}

	@Test
	public void getPointAwayFromYZero() throws Exception {
		Polygon p = new Polygon();
		p.add(new SpacedVector3(20.14513397216797, 21.863087058067322, 14.241708755493164));
		p.add(new SpacedVector3(27.965469360351562, 21.915329337120056, 25.931337356567383));
		p.add(new SpacedVector3(38.855472564697266, 21.843969702720642, 34.03722381591797));
		p.add(new SpacedVector3(43.78850555419922, 21.854923605918884, 23.684040069580078));

		SpacedRotation rotation = new SpacedRotation(2, 3, 4, 1, true);
		RandomProvider randomProvider = new RealRandomProvider();

		polygonSpaceSpawnArea = new PolygonSpaceSpawnArea(UUIDFactoryImpl.INSTANCE.combUUID(),
				polygon,
				rotation,
				randomProvider);
		SpawnPoint nextSpawnPoint = polygonSpaceSpawnArea.getNextSpawnPoint();
		assertTrue(polygon.containsPoint(new Vector2(nextSpawnPoint.getPosition().getX(), nextSpawnPoint.getPosition().getZ())));
		assertTrue(nextSpawnPoint.getPosition().getY() - 21.0 < 0.5);
	}
}

