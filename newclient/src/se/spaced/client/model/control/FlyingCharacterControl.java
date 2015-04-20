package se.spaced.client.model.control;

import com.ardor3d.renderer.Camera;
import com.google.inject.Inject;
import se.ardortech.math.SpacedVector3;
import se.ardortech.math.VectorMath;
import se.ardortech.math.Vectors;
import se.spaced.client.model.CharacterPhysics;
import se.spaced.client.model.UserCharacter;
import se.spaced.client.model.control.states.LocalRecorder;
import se.spaced.client.physics.PhysicsWorld;
import se.spaced.shared.model.AnimationState;
import se.spaced.shared.model.PositionalData;

public class FlyingCharacterControl extends CharacterControl {

	// Camera
	private final ThirdPersonCamera cameraControl;

	// Control
	private SpacedVector3 wantedVelocity = SpacedVector3.ZERO;

	private double flyAcc = 10;
	private double flySpeed = 100;
	private double flyFasterSpeed = 1000;

	@Inject
	public FlyingCharacterControl(
			PhysicsWorld physicsWorld,
			UserCharacter userCharacter,
			CharacterControlLuaHandler luaHandler,
			ThirdPersonCamera cameraControl,
			Camera camera,
			GroundImpactListener groundImpactListener, LocalRecorder recorder, ClientTeleporter teleporter) {
		super(userCharacter, luaHandler, physicsWorld, camera, groundImpactListener, recorder, teleporter);
		this.cameraControl = cameraControl;
	}

	@Override
	public void updateCamera(double dt, PhysicsWorld physicsWorld) {
		cameraControl.update(camera, userCharacter, physicsWorld);
	}

	@Override
	public void updatePhysics(long millisPerFrame, PhysicsWorld physicsWorld) {
		double dt = millisPerFrame / 1000.0;

		CharacterPhysics self = userCharacter.getPhysics();

		// Adjust towards wanted speed
		self.setVelocity(VectorMath.lerp(self.getVelocity(), wantedVelocity, Math.min(1.f, flyAcc * dt)));
		wantedVelocity = SpacedVector3.ZERO;

		// Integrate
		self.setPosition(self.getPosition().add(self.getVelocity().scalarMultiply(dt)));

		// Update with results
		userCharacter.setPositionalData(new PositionalData(self.getPosition(), self.getRotation()));
	}

	@Override
	public void updateSteering(double dt, PhysicsWorld physicsWorld) {
		if (userCharacter.isAlive()) {

			int moveFB = keys.getMoveFB();
			if (moveFB != 0) {
				wantedVelocity = wantedVelocity.add(Vectors.fromArdor(camera.getDirection()).scalarMultiply(moveFB));
			}

			int strafeLR = keys.getMoveLR();
			if (strafeLR != 0) {
				wantedVelocity = wantedVelocity.add(Vectors.fromArdor(camera.getLeft()).scalarMultiply(strafeLR));
			}

			wantedVelocity = wantedVelocity.normalize().scalarMultiply(keys.isSprint() ? flyFasterSpeed : flySpeed);
		}
	}

	@Override
	public void animate() {
		recorder.record(AnimationState.SIT);
	}

	@Override
	public void onMouseMove(int dx, int dy, boolean lmb, boolean rmb) {
		cameraControl.onMouseMove(camera, dx, dy, lmb, rmb);

		if (rmb) {
			SpacedVector3 lookingDir = new SpacedVector3(camera.getDirection().getX(), 0, camera.getDirection().getZ());
			if (lookingDir.getNormSq() > 0 && Math.abs(SpacedVector3.dotProduct(SpacedVector3.PLUS_J, lookingDir)) < 0.99f) {
				userCharacter.getPositionalData().setRotation(VectorMath.lookAt(lookingDir, SpacedVector3.PLUS_J));
			}
		}
	}

	@Override
	public void onMouseWheel(int delta) {
		cameraControl.onMouseWheel(camera, delta);
	}

	@Override
	public void onSelected() {
		recorder.record(AnimationState.FLY);
	}
}
