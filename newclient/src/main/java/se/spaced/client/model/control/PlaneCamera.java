package se.spaced.client.model.control;

import com.ardor3d.math.Quaternion;
import com.ardor3d.math.Vector2;
import com.ardor3d.math.Vector3;
import com.ardor3d.renderer.Camera;
import com.google.inject.Inject;
import se.ardortech.math.Rotations;
import se.ardortech.math.SpacedRotation;
import se.ardortech.math.SpacedVector3;
import se.ardortech.math.Vectors;
import se.spaced.client.model.UserCharacter;
import se.spaced.client.physics.PhysicsWorld;

public class PlaneCamera {

	private static final double MOUSE_ROTATE_SPEED = .005;
	private double desiredPlayerDistance = 8;
	private double playerDistance = 8;
	private final SpacedVector3 cameraCharacterOffset = new SpacedVector3(0, 1.47, 0);
	private final Vector3 cameraDesiredPosition = new Vector3();
	private final Vector3 cameraPivot = new Vector3();
	private final Quaternion workerQuatCam = new Quaternion();
	private final SpacedVector3[] axes = {SpacedVector3.ZERO, SpacedVector3.ZERO, SpacedVector3.ZERO};
	private final CameraCollisionHandler collHandler;

	private Vector2 lookOffset = new Vector2(Vector2.ZERO);
	private SpacedRotation lookOrientation = SpacedRotation.IDENTITY;

	@Inject
	public PlaneCamera(CameraCollisionHandler collHandler) {
		this.collHandler = collHandler;
	}

	public void update(Camera camera, UserCharacter userCharacter, PhysicsWorld physicsWorld, boolean lmb) {
		playerDistance = 0.75 * playerDistance + 0.25 * desiredPlayerDistance;

		if (!lmb) {
			lookOffset.multiplyLocal(0.5);
		}

		lookOrientation = userCharacter.getRotation();
		Vectors.fromSpaced(userCharacter.getPosition()).add(lookOrientation.applyTo(cameraCharacterOffset), cameraPivot);
		lookOrientation = lookOrientation.applyTo(Rotations.fromArdor(workerQuatCam.fromEulerAngles(lookOffset.getX(),
				0,
				lookOffset.getY() + desiredPlayerDistance * 0.3 / (1 + desiredPlayerDistance))));

		setRotation(camera, lookOrientation);
		camera.getDirection().scaleAdd(-playerDistance, cameraPivot, cameraDesiredPosition);

		collHandler.handleIt(camera, cameraPivot, cameraDesiredPosition, camera.getUp());
	}

	public void onMouseMove(int dx, int dy, boolean lmb, boolean rmb) {
		if (lmb && !rmb) {
			lookOffset.addLocal(dx * MOUSE_ROTATE_SPEED, dy * MOUSE_ROTATE_SPEED);
			double lx = Math.PI * 0.8;
			double ly = Math.PI / 4.f;

			if (lookOffset.getX() < -lx) {
				lookOffset.setX(-lx);
			}
			if (lookOffset.getX() > lx) {
				lookOffset.setX(lx);
			}
			if (lookOffset.getY() < -ly) {
				lookOffset.setY(-ly);
			}
			if (lookOffset.getY() > ly) {
				lookOffset.setY(ly);
			}
		}
	}

	public void setRotation(Camera camera, SpacedRotation q) {
		q.revert().toAxes(axes);
		camera.setAxes(axes[0], axes[1], axes[2]);
		camera.normalize();
	}

	public void addRotation(Camera camera, double heading, double attitude, double bank) {
		SpacedRotation camRot = SpacedRotation.fromAxes(camera.getLeft(), camera.getUp(), camera.getDirection());

		SpacedRotation delta = Rotations.fromEulerAngles(heading, attitude, bank);
		SpacedRotation newRot = camRot.applyTo(delta);
		newRot.toAxes(axes);
		camera.setAxes(axes[0], axes[1], axes[2]);
		camera.normalize();
	}

	public void onMouseWheel(Camera camera, int delta) {
		desiredPlayerDistance = (float) Math.max(0.2,
				Math.min(80, desiredPlayerDistance - delta * (0.02 * (0.2 + desiredPlayerDistance * 0.1))));
	}
}
