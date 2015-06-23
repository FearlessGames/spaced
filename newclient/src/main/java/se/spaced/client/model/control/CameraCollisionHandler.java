package se.spaced.client.model.control;

import com.ardor3d.math.Vector3;
import com.ardor3d.math.type.ReadOnlyVector3;
import com.ardor3d.renderer.Camera;
import com.google.inject.Inject;
import se.spaced.client.physics.CollisionFilter;
import se.spaced.client.physics.PhysicsWorld;

public class CameraCollisionHandler {

	Vector3 store = new Vector3();
	Vector3 newPosition = new Vector3();
	private final Vector3 right = new Vector3();
	private final Vector3 down = new Vector3();
	private final PhysicsWorld physicsWorld;

	@Inject
	public CameraCollisionHandler(PhysicsWorld physicsWorld) {
		this.physicsWorld = physicsWorld;
	}

	public void handleIt(Camera camera, ReadOnlyVector3 cameraTarget, ReadOnlyVector3 desiredPosition, ReadOnlyVector3 up) {

		camera.setLocation(desiredPosition);
		camera.lookAt(cameraTarget, up);

		double desiredDistance = cameraTarget.distance(desiredPosition);
		double closestFraction = 1;
		right.set(camera.getLeft().multiply(-1.0, store));
		down.set(camera.getUp().multiply(-1.0, store));
		closestFraction = Math.min(closestFraction, castRay(cameraTarget, desiredPosition.add(camera.getUp(), store)));
		closestFraction = Math.min(closestFraction, castRay(cameraTarget, desiredPosition.add(camera.getLeft(), store).add(down, store)));
		closestFraction = Math.min(closestFraction, castRay(cameraTarget, desiredPosition.add(right, store).add(down, store)));
		camera.getDirection().scaleAdd(-closestFraction * desiredDistance, cameraTarget, newPosition);

		// landscape
		camera.setLocation(newPosition);
	}

	private double castRay(ReadOnlyVector3 from, ReadOnlyVector3 to) {
		return physicsWorld.castRay(from, to, CollisionFilter.CAMERA_RAYCAST_MASK);
	}
}
