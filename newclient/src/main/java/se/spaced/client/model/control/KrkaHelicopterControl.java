package se.spaced.client.model.control;

import com.ardor3d.math.Quaternion;
import com.ardor3d.math.Vector3;
import com.ardor3d.math.type.ReadOnlyVector3;
import com.ardor3d.renderer.Camera;
import com.google.inject.Inject;
import se.ardortech.math.Rotations;
import se.ardortech.math.SpacedVector3;
import se.ardortech.math.VectorMath;
import se.ardortech.math.Vectors;
import se.spaced.client.model.CharacterPhysics;
import se.spaced.client.model.UserCharacter;
import se.spaced.client.model.control.states.LocalRecorder;
import se.spaced.client.physics.PhysicsWorld;
import se.spaced.shared.model.AnimationState;

public class KrkaHelicopterControl extends CharacterControl {

	// Camera
	private final FollowCamera cameraControl;

	// Helicopter Physics
	double thrust;
	double wantedThrust;
	double thrustAcc = 46;

	double motorMaxPower = 16.0;
	double motorIdlePower = 1.0;
	double dragCoeff = 0.0075;

	private double straightenSpeed = 1;

	// Temps
	SpacedVector3 rotorAcc = SpacedVector3.ZERO;
	Vector3[] axes = {new Vector3(), new Vector3(), new Vector3()};
	Vector3 store = new Vector3();
	Quaternion storeQ = new Quaternion();
	Quaternion directionQ = new Quaternion();
	SpacedVector3 gravity = new SpacedVector3(0, -9.82, 0);
	SpacedVector3 totalAcc = SpacedVector3.ZERO;
	SpacedVector3 drag = SpacedVector3.ZERO;

	@Inject
	public KrkaHelicopterControl(
			PhysicsWorld physicsWorld,
			UserCharacter userCharacter,
			CharacterControlLuaHandler luaHandler,
			FollowCamera cameraControl,
			Camera camera,
			GroundImpactListener groundImpactListener, LocalRecorder recorder, ClientTeleporter teleporter) {
		super(userCharacter, luaHandler, physicsWorld, camera, groundImpactListener, recorder, teleporter);
		this.cameraControl = cameraControl;
	}

	@Override
	public void onMouseMove(int dx, int dy, boolean lmb, boolean rmb) {
		cameraControl.onMouseMove(dx, dy, lmb || keys.rmbPressed(), rmb);

		if (keys.rmbPressed()) {
			store.set(camera.getDirection());
			store.setY(0);
			directionQ.lookAt(store, Vector3.UNIT_Y);
		}
	}

	@Override
	public void onMouseWheel(int wheelDelta) {
		cameraControl.onMouseWheel(camera, wheelDelta);
	}

	@Override
	public void updateCamera(double dt, PhysicsWorld physicsWorld) {
		cameraControl.setDirectSterring(keys.rmbPressed() || keys.lmbPressed());
		cameraControl.update(dt, camera, userCharacter, physicsWorld);
	}

	@Override
	public void updatePhysics(long millisPerFrame, PhysicsWorld physicsWorld) {
		double dt = millisPerFrame / 1000.0;
		if (wantedThrust > thrust) {
			thrust = Math.min(wantedThrust, thrust + thrustAcc * dt);
			straightenSpeed = 3 / thrust * 0.5f;
		} else if (wantedThrust < thrust) {
			thrust = Math.max(wantedThrust, thrust - thrustAcc * dt);
			straightenSpeed = 2;
		}


		CharacterPhysics self = userCharacter.getPhysics();
		SpacedVector3 v = self.getVelocity();

		// Helicopter model
		Rotations.fromSpaced(userCharacter.getRotation()).toAxes(axes);
		rotorAcc = Vectors.fromArdor(axes[1].multiply(thrust, store));
		drag = v.normalize();
		drag = drag.scalarMultiply(-v.getNormSq() * dragCoeff);

		// Straighten up
		//straightenUp(userCharacter, axes[2], straightenSpeed*dt);

		// Sum all forces
		totalAcc = new SpacedVector3(1.0, rotorAcc, 1.0, drag, 1.0, gravity);

		// Integrate
		if (self.getGroundContact()) {
			self.setVelocity(new SpacedVector3(0, Math.max(0, self.getVelocity().getY()) + totalAcc.scalarMultiply(dt).getY(), 0));
		} else {
			self.setVelocity(self.getVelocity().add(totalAcc.scalarMultiply(dt)));
		}

		stepAndUpdateManModel(self.getVelocity(), physicsWorld, millisPerFrame);
	}

	private void straightenUp(UserCharacter userCharacter, ReadOnlyVector3 forward, double lerpf) {
		store.set(forward.getX(), 0, forward.getZ());
		if (store.lengthSquared() > 0) {
			store.normalizeLocal();
			storeQ.lookAt(store, Vector3.UNIT_Y);
			directionQ.slerpLocal(storeQ, lerpf);
		}
	}

	@Override
	public void updateSteering(double dt, PhysicsWorld physicsWorld) {
		if (keys.isWantsToJump()) {
			wantedThrust = motorMaxPower;
		} else {
			wantedThrust = motorIdlePower;
		}

		if (!userCharacter.getPhysics().canJump()) {
			storeQ.fromEulerAngles(0, -keys.getMoveLR(), keys.getMoveFB());
		} else {
			storeQ.setIdentity();
		}
		directionQ.multiply(storeQ, storeQ);
		userCharacter.getPositionalData().setRotation(VectorMath.slerp(userCharacter.getRotation(), Rotations.fromArdor(storeQ), dt * 2));
	}

	@Override
	public void animate() {
		if (keys.isWantsToJump()) {
			recorder.record(AnimationState.FLY_THRUST);
		} else {
			recorder.record(AnimationState.FLY);
		}
	}

	@Override
	public void onSelected() {
		physicsWorld.teleportMan(userCharacter.getPosition());
		directionQ.set(Rotations.fromSpaced(userCharacter.getRotation()));
		cameraControl.resetPosition();
	}

	@Override
	public void onDeselected() {
	}
}
