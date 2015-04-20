package se.spaced.client.model.control;

import com.ardor3d.math.Matrix3;
import com.ardor3d.math.Vector3;
import com.ardor3d.renderer.Camera;
import com.google.inject.Inject;
import se.ardortech.math.Vectors;
import se.spaced.client.model.UserCharacter;
import se.spaced.client.physics.PhysicsWorld;

public class ThirdPersonCamera {

	private double desiredPlayerDistance = 8;
	private double playerDistance = 8;
	private final Vector3 cameraCharacterOffset = new Vector3(0, 1.55, 0);
	private final Vector3 cameraDesiredPosition = new Vector3();
	private final Vector3 cameraPivot = new Vector3();
	private final Matrix3 workerMatrix = new Matrix3();
	private final Matrix3 mouseInputMatrix = new Matrix3();
	private final double mouseRotateSpeed = .005;
	private final Vector3 workerStoreA = new Vector3();
	private final CameraCollisionHandler collHandler;

	@Inject
	ThirdPersonCamera(CameraCollisionHandler collHandler) {
		this.collHandler = collHandler;
	}

	public void update(Camera camera, UserCharacter userCharacter, PhysicsWorld physicsWorld) {
		playerDistance = 0.75 * playerDistance + 0.25 * desiredPlayerDistance;

		applyRotationToCamera(camera);

		//System.out.println(camera.getDirection());

		Vectors.fromSpaced(userCharacter.getPosition()).add(cameraCharacterOffset, cameraPivot);
		camera.getDirection().scaleAdd(-playerDistance, cameraPivot, cameraDesiredPosition);

		collHandler.handleIt(camera, cameraPivot, cameraDesiredPosition, Vector3.UNIT_Y);
	}

	public void applyRotationToCamera(Camera camera) {

		mouseInputMatrix.applyPost(camera.getLeft(), workerStoreA);
		camera.setLeft(workerStoreA);
		mouseInputMatrix.applyPost(camera.getDirection(), workerStoreA);
		camera.setDirection(workerStoreA);
		mouseInputMatrix.applyPost(camera.getUp(), workerStoreA);
		camera.setUp(workerStoreA);

		camera.normalize();
		mouseInputMatrix.setIdentity();
	}

	public void onMouseMove(Camera camera, int dx, int dy, boolean lmb, boolean rmb) {

		if (dx != 0) {
			workerMatrix.fromAngleNormalAxis(mouseRotateSpeed * dx, Vector3.UNIT_Y);
			mouseInputMatrix.multiplyLocal(workerMatrix);
		}

		if (dy != 0) {
			workerMatrix.fromAngleNormalAxis(mouseRotateSpeed * dy, camera.getLeft());
			mouseInputMatrix.multiplyLocal(workerMatrix);
		}
	}

	public void onMouseWheel(Camera camera, int delta) {
		desiredPlayerDistance = (float) Math.max(0.2, Math.min(80, desiredPlayerDistance - delta * (0.02 * (0.2 + desiredPlayerDistance * 0.1))));
	}
}
