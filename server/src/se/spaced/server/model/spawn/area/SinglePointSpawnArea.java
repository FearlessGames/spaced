package se.spaced.server.model.spawn.area;

import org.hibernate.annotations.Type;
import se.ardortech.math.SpacedRotation;
import se.ardortech.math.SpacedVector3;
import se.fearlessgames.common.util.uuid.UUID;
import se.spaced.server.persistence.dao.impl.ExternalPersistableBase;
import se.spaced.shared.world.area.SinglePoint;

import javax.persistence.Entity;
import javax.persistence.Transient;
import java.util.concurrent.atomic.AtomicInteger;

@Entity
public class SinglePointSpawnArea extends ExternalPersistableBase implements SpawnArea {

	@Type(type = "xml")
	private SinglePoint spawnPoint;

	@Transient
	private AtomicInteger spawnCount = new AtomicInteger(0);

	protected SinglePointSpawnArea() {
	}

	public SinglePointSpawnArea(SpacedVector3 point, SpacedRotation rotation) {
		spawnPoint = new SinglePoint(point, rotation);
	}

	public SinglePointSpawnArea(UUID pk, SinglePoint spawnPoint) {
		super(pk);
		this.spawnPoint = spawnPoint;
	}

	@Override
	public SpawnPoint getNextSpawnPoint() {
		return new SpawnPoint(this, spawnPoint.getPoint(), spawnPoint.getRotation());
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

	public SinglePoint getSpawnPoint() {
		return spawnPoint;
	}
}
