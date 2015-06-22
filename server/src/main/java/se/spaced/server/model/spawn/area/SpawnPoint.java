package se.spaced.server.model.spawn.area;

import se.ardortech.math.SpacedRotation;
import se.ardortech.math.SpacedVector3;

public class SpawnPoint {

	private final SpacedVector3 point;
	private final SpacedRotation rotation;
	private final SpawnArea origin;

	public SpawnPoint(SpawnArea origin, SpacedVector3 point, SpacedRotation rotation) {
		this.origin = origin;
		this.point = point;
		this.rotation = rotation;
	}

	public SpacedRotation getRotation() {
		return rotation;
	}

	public SpacedVector3 getPosition() {
		return point;
	}

	public void addSpawn() {
		origin.addSpawn();
	}

	public void removeSpawn() {
		origin.removeSpawn();
	}

	public SpawnArea getOrigin() {
		return origin;
	}
}
