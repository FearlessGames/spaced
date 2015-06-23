package se.spaced.client.model.control;

import com.ardor3d.math.Matrix3;
import com.ardor3d.math.Quaternion;
import com.ardor3d.math.Transform;
import com.ardor3d.math.Vector2;
import com.ardor3d.math.Vector3;
import com.ardor3d.math.type.ReadOnlyVector3;
import com.ardor3d.renderer.Camera;
import com.google.inject.Inject;
import se.ardortech.math.Rotations;
import se.ardortech.math.SpacedRotation;
import se.ardortech.math.VectorMath;
import se.ardortech.math.Vectors;
import se.spaced.client.model.CharacterPhysics;
import se.spaced.client.model.UserCharacter;
import se.spaced.client.model.control.states.LocalRecorder;
import se.spaced.client.physics.PhysicsWorld;
import se.spaced.client.view.entity.EntityView;
import se.spaced.client.view.entity.VisualEntity;
import se.spaced.shared.model.AnimationState;
import se.spaced.shared.model.PositionalData;
import se.spaced.shared.model.xmo.AttachmentPointIdentifier;

public class PlaneCharacterControl extends CharacterControl {

	private final EntityView entityView;

	// Camera
	private final PlaneCamera cameraControl;

	// Control
	private final Vector3 tempV = new Vector3();
	private final Vector3 wantedVelocity = new Vector3();
	private final Quaternion tempQ = new Quaternion();
	private final Matrix3 tempM = new Matrix3();

	private static final double FLY_ACC = 10;
	private static final double FLY_SPEED = 100;
	private static final double FLY_FAST_SPEED = 1000;
	private double thrusterSetting;

	private static final double YAW_SPEED = 1;

	private static final double LEAN_PITCH_COMPENSATION = 0.75;
	private static final double LEAN_YAW_COMPENSATION = 0.75;

	private static final String GYRO = "World_Gyro";
	private static final String THROTTLE = "Throttle_Stick";
	private static final String STICK = "Steering_Stick";

	// Rudders, Fins; Nozzles and Such stuff
	private static final String NOZZLE_VECTOR = "Nozzle_Vector";
	private static final String L_RUDDER = "L_Rudder";
	private static final String R_RUDDER = "R_Rudder";
	private static final String L_TAIL = "L_Tail";
	private static final String R_TAIL = "R_Tail";
	private static final String L_FLAP = "L_Flap";
	private static final String R_FLAP = "R_Flap";
	private static final String L_FRONT = "L_Front";
	private static final String R_FRONT = "R_Front";

	private final Quaternion currentLeftFrontRot = new Quaternion();
	private final Quaternion currentRightFrontRot = new Quaternion();
	private final Quaternion currentRudderRot = new Quaternion();
	private final Quaternion currentLeftFlapRot = new Quaternion();
	private final Quaternion currentRightFlapRot = new Quaternion();
	private final Quaternion currentLeftTailRot = new Quaternion();
	private final Quaternion currentRightTailRot = new Quaternion();
	private final Quaternion currentNozzleRot = new Quaternion();
	private final Transform originalNozzleLS = new Transform();
	private final Transform originalRightTailLS = new Transform();
	private final Transform originalLeftTailLS = new Transform();
	private final Transform originalRightFlapLS = new Transform();
	private final Transform originalLeftFlapLS = new Transform();
	private final Transform originalRightFrontLS = new Transform();
	private final Transform originalLeftFrontLS = new Transform();
	private final Transform originalRightRudderLS = new Transform();
	private final Transform originalLeftRudderLS = new Transform();

	private final Vector2 nozzleInput = new Vector2();
	private final Vector2 leftFinsInput = new Vector2();
	private final Vector2 rightFinsInput = new Vector2();


	private final Transform originalThrottleLS = new Transform();
	private final Transform originalGyroLS = new Transform();
	private final Transform originalStickLS = new Transform();


	private final Quaternion currentGyroRot = new Quaternion();
	private final Quaternion currentStickRot = new Quaternion();

	private final Vector2 joystickInput = new Vector2();


	@Inject
	public PlaneCharacterControl(
			PhysicsWorld physicsWorld,
			UserCharacter userCharacter,
			CharacterControlLuaHandler luaHandler,
			PlaneCamera cameraControl,
			Camera camera,
			GroundImpactListener groundImpactListener,
			EntityView entityView, LocalRecorder recorder, ClientTeleporter teleporter) {
		super(userCharacter, luaHandler, physicsWorld, camera, groundImpactListener, recorder, teleporter);
		this.cameraControl = cameraControl;
		this.entityView = entityView;
	}

	@Override
	public void updateCamera(double dt, PhysicsWorld physicsWorld) {
		cameraControl.update(camera, userCharacter, physicsWorld, keys.lmbPressed());
	}

	@Override
	public void animate() {
		recorder.record(AnimationState.SIT_PILOT);
	}

	@Override
	public void updatePhysics(long millisPerFrame, PhysicsWorld physicsWorld) {
		double dt = millisPerFrame / 1000.0;
		CharacterPhysics self = userCharacter.getPhysics();

		// Adjust towards wanted speed
		self.setVelocity(VectorMath.lerp(self.getVelocity(), Vectors.fromArdor(wantedVelocity), Math.min(1.f, FLY_ACC * dt)));
		wantedVelocity.set(Vector3.ZERO);

		// Integrate
		self.setPosition(self.getPosition().add(self.getVelocity().scalarMultiply(dt)));

		// Collision detection and response
		double landscapeHeight = physicsWorld.getHeight(self.getPosition());
		if (self.getPosition().getY() < landscapeHeight) {
			self.setPosition(Vectors.setY(self.getPosition(), landscapeHeight));
			self.setGroundContact(true);
		}

		// Update with results
		userCharacter.setPositionalData(new PositionalData(self.getPosition(), self.getRotation()));
		updateMovingParts(dt);
	}

	public void updateMovingParts(double dt) {
		Rotations.fromSpaced(userCharacter.getPhysics().getRotation()).toRotationMatrix(tempM);

		VisualEntity ve = entityView.getEntity(userCharacter.getPk());
		Transform t = ve.getAttachmentJointLocalTransform(AttachmentPointIdentifier.VEHICLE, GYRO);
		if (t != null) {
			tempM.invertLocal();
			tempM.multiply(originalGyroLS.getMatrix(), tempM);

			// sluggyness
			tempQ.fromRotationMatrix(tempM);
			currentGyroRot.slerpLocal(tempQ, Math.min(1.0, 5 * dt));
			currentGyroRot.toRotationMatrix(tempM);

			t.setRotation(tempM);
		}

		t = ve.getAttachmentJointLocalTransform(AttachmentPointIdentifier.VEHICLE, THROTTLE);
		if (t != null && originalThrottleLS != null) {
			originalThrottleLS.getTranslation().add(0, -thrusterSetting / FLY_SPEED * 0.1, 0, tempV);
			t.setTranslation(tempV);
		}

		t = ve.getAttachmentJointLocalTransform(AttachmentPointIdentifier.VEHICLE, STICK);
		if (t != null) {
			tempQ.fromEulerAngles(-joystickInput.getX(), 0, joystickInput.getY());
			tempQ.toRotationMatrix(tempM);
			tempM.multiply(originalStickLS.getMatrix(), tempM);

			// sluggyness
			tempQ.fromRotationMatrix(tempM);
			currentStickRot.slerpLocal(tempQ, Math.min(1.0, 5 * dt));
			currentStickRot.toRotationMatrix(tempM);

			t.setRotation(tempM);
			joystickInput.set(0, 0);
		}

// Rudders, Fins; Nozzles and Such stuff

		t = ve.getAttachmentJointLocalTransform(AttachmentPointIdentifier.VEHICLE, NOZZLE_VECTOR);
		if (t != null) {
			tempQ.fromEulerAngles(0, 0, -nozzleInput.getY());
			tempQ.toRotationMatrix(tempM);
			tempM.multiply(originalNozzleLS.getMatrix(), tempM);

			// sluggyness
			tempQ.fromRotationMatrix(tempM);
			currentNozzleRot.slerpLocal(tempQ, Math.min(1.0, 1.5 * dt));
			currentNozzleRot.toRotationMatrix(tempM);

			t.setRotation(tempM);
			nozzleInput.set(0, 0);
		}

		t = ve.getAttachmentJointLocalTransform(AttachmentPointIdentifier.VEHICLE, L_TAIL);
		if (t != null) {
			tempQ.fromEulerAngles(0, 0, ((1 * -leftFinsInput.getX()) - (2 * leftFinsInput.getY())));
			tempQ.toRotationMatrix(tempM);
			tempM.multiply(originalLeftTailLS.getMatrix(), tempM);

			// sluggyness
			tempQ.fromRotationMatrix(tempM);
			currentLeftTailRot.slerpLocal(tempQ, Math.min(2.0, 2.8 * dt));
			currentLeftTailRot.toRotationMatrix(tempM);

			t.setRotation(tempM);
		}

		t = ve.getAttachmentJointLocalTransform(AttachmentPointIdentifier.VEHICLE, R_TAIL);
		if (t != null) {
			tempQ.fromEulerAngles(0, 0, ((1 * rightFinsInput.getX()) - (2 * rightFinsInput.getY())));
			tempQ.toRotationMatrix(tempM);
			tempM.multiply(originalRightTailLS.getMatrix(), tempM);

			// sluggyness
			tempQ.fromRotationMatrix(tempM);
			currentRightTailRot.slerpLocal(tempQ, Math.min(2.0, 2.8 * dt));
			currentRightTailRot.toRotationMatrix(tempM);

			t.setRotation(tempM);
		}

		t = ve.getAttachmentJointLocalTransform(AttachmentPointIdentifier.VEHICLE, L_RUDDER);
		if (t != null) {
			tempQ.fromEulerAngles(0, rightFinsInput.getX(), 0);
			tempQ.toRotationMatrix(tempM);
			tempM.multiply(originalLeftTailLS.getMatrix(), tempM);

			// sluggyness
			tempQ.fromRotationMatrix(tempM);
			currentRudderRot.slerpLocal(tempQ, Math.min(2.0, 2.8 * dt));
			currentRudderRot.toRotationMatrix(tempM);

			t.setRotation(tempM);
		}

		t = ve.getAttachmentJointLocalTransform(AttachmentPointIdentifier.VEHICLE, R_RUDDER);
		if (t != null) {
			tempQ.fromEulerAngles(0, rightFinsInput.getX(), 0);
			tempQ.toRotationMatrix(tempM);
			tempM.multiply(originalRightTailLS.getMatrix(), tempM);

			// sluggyness
			tempQ.fromRotationMatrix(tempM);
			currentRudderRot.slerpLocal(tempQ, Math.min(2.0, 2.8 * dt));
			currentRudderRot.toRotationMatrix(tempM);

			t.setRotation(tempM);
		}

		t = ve.getAttachmentJointLocalTransform(AttachmentPointIdentifier.VEHICLE, L_FRONT);
		if (t != null) {
			tempQ.fromEulerAngles(2 * -leftFinsInput.getX(), 0, (1 * -rightFinsInput.getX() + 2 * leftFinsInput.getY()));
			tempQ.toRotationMatrix(tempM);
			tempM.multiply(originalLeftTailLS.getMatrix(), tempM);

			// sluggyness
			tempQ.fromRotationMatrix(tempM);
			currentLeftFrontRot.slerpLocal(tempQ, Math.min(2.0, 1.5 * dt));
			currentLeftFrontRot.toRotationMatrix(tempM);

			t.setRotation(tempM);
		}

		t = ve.getAttachmentJointLocalTransform(AttachmentPointIdentifier.VEHICLE, R_FRONT);
		if (t != null) {
			tempQ.fromEulerAngles(2 * -rightFinsInput.getX(), 0, (1 * rightFinsInput.getX() + 2 * leftFinsInput.getY()));
			tempQ.toRotationMatrix(tempM);
			tempM.multiply(originalRightTailLS.getMatrix(), tempM);

			// sluggyness
			tempQ.fromRotationMatrix(tempM);
			currentRightFrontRot.slerpLocal(tempQ, Math.min(2.0, 1.5 * dt));
			currentRightFrontRot.toRotationMatrix(tempM);

			t.setRotation(tempM);
		}

		t = ve.getAttachmentJointLocalTransform(AttachmentPointIdentifier.VEHICLE, L_FLAP);
		if (t != null) {
			tempQ.fromEulerAngles(0, 0, ((8 * -leftFinsInput.getX()) - (1 * leftFinsInput.getY())));
			tempQ.toRotationMatrix(tempM);
			tempM.multiply(originalLeftTailLS.getMatrix(), tempM);

			// sluggyness
			tempQ.fromRotationMatrix(tempM);
			currentLeftFlapRot.slerpLocal(tempQ, Math.min(2.0, 0.8 * dt));
			currentLeftFlapRot.toRotationMatrix(tempM);

			t.setRotation(tempM);
			leftFinsInput.set(0, 0);
		}

		t = ve.getAttachmentJointLocalTransform(AttachmentPointIdentifier.VEHICLE, R_FLAP);
		if (t != null) {
			tempQ.fromEulerAngles(0, 0, ((8 * rightFinsInput.getX()) - (1 * rightFinsInput.getY())));
			tempQ.toRotationMatrix(tempM);
			tempM.multiply(originalRightTailLS.getMatrix(), tempM);

			// sluggyness
			tempQ.fromRotationMatrix(tempM);
			currentRightFlapRot.slerpLocal(tempQ, Math.min(2.0, 0.8 * dt));
			currentRightFlapRot.toRotationMatrix(tempM);

			t.setRotation(tempM);
			rightFinsInput.set(0, 0);
		}

		ve.updatePoseTransforms(AttachmentPointIdentifier.VEHICLE);
	}

	@Override
	public void updateSteering(double dt, PhysicsWorld physicsWorld) {
		if (userCharacter.isAlive()) {

			double moveLR = keys.getMoveLR();
			double moveFB = keys.getMoveFB();

			// when leaning, apply some bank and pitch rotation about to enable mouseturning
			Quaternion currentRotation = Rotations.fromSpaced(userCharacter.getPositionalData().getRotation());
			double lean = currentRotation.getRotationColumn(0, tempV).getY();
			double yawCompensation = -lean * LEAN_YAW_COMPENSATION;
			double pitchCompensation = -(1 - Math.cos(lean * Math.PI / 2)) * LEAN_PITCH_COMPENSATION;
			// System.out.println("l: " + lean + " yc: "+yawCompensation+" pc: "+pitchCompensation);
			moveLR += yawCompensation;

			if (moveFB != 0) {
				thrusterSetting = Math.max(0,
						Math.min(FLY_SPEED, thrusterSetting + moveFB * dt * FLY_SPEED)); // 1 second for max thrust
			}
			if (moveLR != 0) {
				rotateShip(moveLR * dt * YAW_SPEED, 0, pitchCompensation * dt);
			}

			ReadOnlyVector3 forward = currentRotation.getRotationColumn(2, tempV);
			wantedVelocity.addLocal(forward);
			wantedVelocity.normalizeLocal().multiplyLocal(keys.isSprint() ? FLY_FAST_SPEED : thrusterSetting);
		}
	}

	public void rotateShip(double heading, double attitude, double bank) {
		SpacedRotation newRotation = Rotations.fromEulerAngles(heading, attitude, bank);
		userCharacter.getPhysics().setRotation(userCharacter.getPhysics().getRotation().applyTo(newRotation));
		joystickInput.addLocal(attitude, bank);
		nozzleInput.addLocal(attitude, bank * 8);
		leftFinsInput.addLocal(attitude * 1, bank * 2);
		rightFinsInput.addLocal(attitude * 1, bank * 2);
	}

	@Override
	public void onMouseMove(int dx, int dy, boolean lmb, boolean rmb) {
		if (!keys.lmbPressed() || keys.rmbPressed()) {
			rotateShip(0, -dx * 0.005, -dy * 0.005);
		}

		cameraControl.onMouseMove(dx, dy, keys.lmbPressed(), keys.rmbPressed());
	}

	@Override
	public void onMouseWheel(int delta) {
		cameraControl.onMouseWheel(camera, delta);
	}

	@Override
	public void onSelected() {
		thrusterSetting = 0;

		// Extract original throttle position
		VisualEntity ve = entityView.getEntity(userCharacter.getPk());
		Transform t = ve.getAttachmentJointLocalTransform(AttachmentPointIdentifier.VEHICLE, THROTTLE);
		if (t != null) {
			originalThrottleLS.set(t);
		}
		t = ve.getAttachmentJointLocalTransform(AttachmentPointIdentifier.VEHICLE, GYRO);
		if (t != null) {
			originalGyroLS.set(t);
		}
		t = ve.getAttachmentJointLocalTransform(AttachmentPointIdentifier.VEHICLE, STICK);
		if (t != null) {
			originalStickLS.set(t);
		}
		t = ve.getAttachmentJointLocalTransform(AttachmentPointIdentifier.VEHICLE, NOZZLE_VECTOR);
		if (t != null) {
			originalNozzleLS.set(t);
		}
		t = ve.getAttachmentJointLocalTransform(AttachmentPointIdentifier.VEHICLE, L_TAIL);
		if (t != null) {
			originalLeftTailLS.set(t);
		}
		t = ve.getAttachmentJointLocalTransform(AttachmentPointIdentifier.VEHICLE, R_TAIL);
		if (t != null) {
			originalRightTailLS.set(t);
		}
		t = ve.getAttachmentJointLocalTransform(AttachmentPointIdentifier.VEHICLE, L_FLAP);
		if (t != null) {
			originalLeftFlapLS.set(t);
		}
		t = ve.getAttachmentJointLocalTransform(AttachmentPointIdentifier.VEHICLE, R_FLAP);
		if (t != null) {
			originalRightFlapLS.set(t);
		}

		t = ve.getAttachmentJointLocalTransform(AttachmentPointIdentifier.VEHICLE, L_RUDDER);
		if (t != null) {
			originalLeftRudderLS.set(t);
		}
		t = ve.getAttachmentJointLocalTransform(AttachmentPointIdentifier.VEHICLE, R_RUDDER);
		if (t != null) {
			originalRightRudderLS.set(t);
		}
		t = ve.getAttachmentJointLocalTransform(AttachmentPointIdentifier.VEHICLE, L_FRONT);
		if (t != null) {
			originalLeftFrontLS.set(t);
		}
		t = ve.getAttachmentJointLocalTransform(AttachmentPointIdentifier.VEHICLE, R_FRONT);
		if (t != null) {
			originalRightFrontLS.set(t);
		}

	}
}
