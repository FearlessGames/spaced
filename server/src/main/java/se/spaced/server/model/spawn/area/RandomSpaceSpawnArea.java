package se.spaced.server.model.spawn.area;

import org.hibernate.annotations.Type;
import se.ardortech.math.SpacedRotation;
import se.ardortech.math.SpacedVector3;
import se.fearless.common.uuid.UUID;
import se.spaced.server.persistence.dao.impl.ExternalPersistableBase;
import se.spaced.shared.util.random.RandomProvider;
import se.spaced.shared.util.random.RealRandomProvider;
import se.spaced.shared.world.area.Cube;

import javax.persistence.Entity;
import javax.persistence.Transient;
import java.util.concurrent.atomic.AtomicInteger;

@Entity
public class RandomSpaceSpawnArea extends ExternalPersistableBase implements SpawnArea {

	@Type(type = "xml")
	private Cube cube;

	@Transient
	private RandomProvider randomProvider = new RealRandomProvider();

	@Transient
	private final AtomicInteger spawnCount = new AtomicInteger(0);

	protected RandomSpaceSpawnArea() {
	}

	public RandomSpaceSpawnArea(UUID pk, Cube cube) {
		super(pk);
		this.cube = cube;
	}

	public RandomSpaceSpawnArea(
			UUID uuid,
			SpacedVector3 corner,
			int width,
			int height,
			int depth,
			SpacedRotation rotation,
			RandomProvider randomProvider) {
		super(uuid);
		cube = new Cube(corner, width, height, depth, rotation);

		this.randomProvider = randomProvider;
	}

	@Override
	public SpawnPoint getNextSpawnPoint() {
		double offsetX = randomProvider.getDouble(0.0, (double) cube.getWidth());
		double offsetY = randomProvider.getDouble(0.0, (double) cube.getHeight());
		double offsetZ = randomProvider.getDouble(0.0, (double) cube.getDepth());
		SpacedVector3 offset = new SpacedVector3(offsetX, offsetY, offsetZ);

		return new SpawnPoint(this, cube.getCorner().add(offset), cube.getRotation());
	}

	@Override
	public void addSpawn() {
		spawnCount.incrementAndGet();
	}

	@Override
	public void removeSpawn() {
		spawnCount.decrementAndGet();
	}

	@Override
	public int getSpawnCount() {
		return spawnCount.get();
	}

	public Cube getCube() {
		return cube;
	}
}
