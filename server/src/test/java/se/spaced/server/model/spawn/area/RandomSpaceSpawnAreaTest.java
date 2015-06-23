package se.spaced.server.model.spawn.area;

import com.ardor3d.bounding.BoundingBox;
import org.junit.Test;
import se.ardortech.math.SpacedRotation;
import se.ardortech.math.SpacedVector3;
import se.fearless.common.uuid.UUIDFactory;
import se.fearless.common.uuid.UUIDFactoryImpl;
import se.spaced.shared.util.random.RandomProvider;
import se.spaced.shared.util.random.RealRandomProvider;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class RandomSpaceSpawnAreaTest {

	@Test
	public void testGetNextSpawnPoint() {
		UUIDFactory uuidFactory = UUIDFactoryImpl.INSTANCE;
		SpacedVector3 corner = new SpacedVector3(100, 100, 100);
		SpacedRotation rotation = new SpacedRotation(2, 3, 4, 1, true);
		RandomProvider randomProvider = new RealRandomProvider();
		RandomSpaceSpawnArea area = new RandomSpaceSpawnArea(uuidFactory.combUUID(),
				corner, 100, 100, 100, rotation, randomProvider);

		BoundingBox box = new BoundingBox(corner, 100, 100, 100);
		for (int i = 0; i < 100; i++) {
			SpawnPoint spawnPoint = area.getNextSpawnPoint();

			assertTrue(box.contains(spawnPoint.getPosition()));
			assertEquals(rotation, spawnPoint.getRotation());
		}
	}
}
