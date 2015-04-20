package se.spaced.client.model.control;

import com.ardor3d.math.Quaternion;
import com.ardor3d.math.Vector3;
import com.ardor3d.renderer.Camera;
import com.google.inject.Inject;
import se.ardortech.math.SpacedVector3;
import se.ardortech.math.Vectors;
import se.spaced.client.model.UserCharacter;
import se.spaced.client.physics.PhysicsWorld;


public class FollowCamera {
	private double desiredPlayerDistance = 14;
	private double playerDistance = 14;

	SpacedVector3 cameraToPlayer = SpacedVector3.ZERO;
	SpacedVector3 focusPosition = SpacedVector3.ZERO;
	SpacedVector3 focusOffset = new SpacedVector3(0, 1.7, 0);
	SpacedVector3 cameraDesiredPosition = SpacedVector3.ZERO;

	Quaternion cameraSteerRot = new Quaternion(Quaternion.IDENTITY);

	double dx = 0.1;
	double dy = 0.1;
	boolean directSteering;
	private final double sensitivity = 0.1;
	double cameraToPlayerY = 0;

	private final CameraCollisionHandler collHandler;

	@Inject
	public FollowCamera(CameraCollisionHandler collHandler) {
		this.collHandler = collHandler;
	}

	public void update(double dt, Camera camera, UserCharacter userCharacter, PhysicsWorld physicsWorld) {
		playerDistance = 0.9 * playerDistance + 0.1 * desiredPlayerDistance;

		focusPosition = userCharacter.getPosition().add(userCharacter.getRotation().applyTo(focusOffset));

		// Refresh c2p to match current camera position
		if (cameraToPlayer.equals(SpacedVector3.ZERO)) {
			cameraToPlayer = Vectors.fromArdor(camera.getLocation()).subtract(focusPosition);
			cameraToPlayerY = cameraToPlayer.getY();
		}

		if (directSteering) {
			// apply steering
			cameraSteerRot.fromAngleAxis(dx * dt, Vector3.UNIT_Y);
			cameraToPlayer = Vectors.fromArdor(cameraSteerRot.apply(cameraToPlayer, null));
			dx = 0;

			cameraSteerRot.fromAngleAxis(dy * dt, camera.getLeft());
			cameraToPlayer = Vectors.fromArdor(cameraSteerRot.apply(cameraToPlayer, null));
			dy = 0;
		} else {
			// apply follow
			cameraToPlayer = Vectors.fromArdor(camera.getLocation()).subtract(focusPosition).scalarMultiply(0.9);
			cameraToPlayer = new SpacedVector3(cameraToPlayer.getX(), cameraToPlayerY, cameraToPlayer.getZ());
		}

		cameraToPlayer = cameraToPlayer.normalize().scalarMultiply(playerDistance);
		cameraDesiredPosition = focusPosition.add(cameraToPlayer);

		collHandler.handleIt(camera,
				Vectors.fromSpaced(focusPosition),
				Vectors.fromSpaced(cameraDesiredPosition),
				Vector3.UNIT_Y);
	}

	public void setDirectSterring(boolean directSteering) {
		if (this.directSteering != directSteering) {
			resetPosition();
			this.directSteering = directSteering;
		}
	}

	public void onMouseWheel(Camera camera, int delta) {
		desiredPlayerDistance = (float) Math.max(0.2,
				Math.min(80, desiredPlayerDistance - delta * (0.02 * (0.2 + desiredPlayerDistance * 0.1))));
	}

	public void onMouseMove(int dx, int dy, boolean lmb, boolean rmb) {
		if (directSteering && lmb) {
			this.dx = dx * sensitivity;
			this.dy = dy * sensitivity;
		}
	}

	public void resetPosition() {
		cameraToPlayer = SpacedVector3.ZERO;
	}
}
