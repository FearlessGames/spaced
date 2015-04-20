package se.spaced.server.model.spawn.area;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Lists;
import com.google.common.collect.Multiset;
import org.junit.Test;
import se.ardortech.math.SpacedRotation;
import se.ardortech.math.SpacedVector3;

import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class CompositeSpawnAreaTest {

	@Test
	public void singleChild() throws Exception {
		SpacedVector3 pos = new SpacedVector3(10, 20, 30);
		SpacedRotation rot = new SpacedRotation(1, 2, 3, 4, true);
		SpawnArea spawnArea = new SinglePointSpawnArea(pos, rot);
		CompositeSpawnArea composite = new CompositeSpawnArea(new Random(1234), Lists.newArrayList(spawnArea));
		SpawnPoint nextSpawnPoint = composite.getNextSpawnPoint();
		assertEquals(spawnArea, nextSpawnPoint.getOrigin());
		assertEquals(pos, nextSpawnPoint.getPosition());
		assertEquals(rot, nextSpawnPoint.getRotation());

		SpawnPoint anotherSpawnPoint = composite.getNextSpawnPoint();
		assertEquals(spawnArea, anotherSpawnPoint.getOrigin());
		assertEquals(pos, anotherSpawnPoint.getPosition());
		assertEquals(rot, anotherSpawnPoint.getRotation());


	}

	@Test
	public void twoNodes() throws Exception {
		SpacedVector3 pos1 = new SpacedVector3(10, 20, 30);
		SpacedRotation rot1 = new SpacedRotation(1, 2, 3, 4, true);
		SpawnArea spawnArea1 = new SinglePointSpawnArea(pos1, rot1);
		SpacedVector3 pos2 = new SpacedVector3(100, 200, 300);
		SpacedRotation rot2 = new SpacedRotation(4, 1, 2, 3, true);
		SpawnArea spawnArea2 = new SinglePointSpawnArea(pos2, rot2);
		CompositeSpawnArea composite = new CompositeSpawnArea(new Random(1234), Lists.newArrayList(spawnArea1, spawnArea2));
		SpawnPoint nextSpawnPoint1 = composite.getNextSpawnPoint();
		nextSpawnPoint1.addSpawn();
		assertEquals(1, composite.getSpawnCount());
		SpawnPoint nextSpawnPoint2 = composite.getNextSpawnPoint();
		nextSpawnPoint2.addSpawn();

		assertFalse(nextSpawnPoint1.getPosition().equals(nextSpawnPoint2.getPosition()));

		assertEquals(2, composite.getSpawnCount());
	}

	@Test
	public void threeNodesWithDuplicates() throws Exception {
		SpacedVector3 pos1 = new SpacedVector3(10, 20, 30);
		SpacedRotation rot1 = new SpacedRotation(1, 2, 3, 4, true);
		SpawnArea spawnArea1 = new SinglePointSpawnArea(pos1, rot1);
		SpacedVector3 pos2 = new SpacedVector3(100, 200, 300);
		SpacedRotation rot2 = new SpacedRotation(4, 1, 2, 3, true);
		SpawnArea spawnArea2 = new SinglePointSpawnArea(pos2, rot2);
		SpacedVector3 pos3 = new SpacedVector3(1000, 2000, 3000);
		SpacedRotation rot3 = new SpacedRotation(1, 4, 2, 3, true);
		SpawnArea spawnArea3 = new SinglePointSpawnArea(pos3, rot3);
		CompositeSpawnArea composite = new CompositeSpawnArea(new Random(1234), Lists.newArrayList(spawnArea1, spawnArea2, spawnArea3));

		Multiset<SpacedVector3> positions = HashMultiset.create();
		for (int i = 0; i < 12; i++) {
			SpawnPoint nextSpawnPoint = composite.getNextSpawnPoint();
			nextSpawnPoint.addSpawn();
			positions.add(nextSpawnPoint.getPosition());
		}

		assertEquals(4, positions.count(pos1));
		assertEquals(4, positions.count(pos2));
		assertEquals(4, positions.count(pos3));

		assertEquals(12, composite.getSpawnCount());
	}
}
