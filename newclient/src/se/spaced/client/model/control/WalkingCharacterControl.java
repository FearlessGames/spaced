package se.spaced.client.model.control;

import com.ardor3d.renderer.Camera;
import com.google.inject.Inject;
import se.ardortech.math.Rotations;
import se.ardortech.math.SpacedRotation;
import se.ardortech.math.SpacedVector3;
import se.ardortech.math.VectorMath;
import se.ardortech.math.Vectors;
import se.spaced.client.model.CharacterPhysics;
import se.spaced.client.model.UserCharacter;
import se.spaced.client.model.control.states.LocalRecorder;
import se.spaced.client.physics.PhysicsWorld;
import se.spaced.shared.model.AnimationState;

public class WalkingCharacterControl extends CharacterControl {

	public static final double GRAVITY_CONSTANT = 15;
	// Camera
	private final ThirdPersonCamera cameraControl;

	// Control
	SpacedVector3 inputVector = SpacedVector3.ZERO;
	SpacedRotation desiredRotation = SpacedRotation.IDENTITY;

	private static final double DOWN_SUCK = 2;
	private static final double MAX_WALK_SPEED = 9;
	private static final double MAX_STRAFE_SPEED = 7;
	private static final double MAX_BACK_SPEED = 3;
	private static final double MAX_AIRCONTROL_SPEED = 9;
	private final double jumpSpeed = 2; // v0=7 => 2.5m hopp f(t) = h0 + v0*t - 9.81*t*t/2

	private final double rotationSpeed = 14;

	private SpacedVector3 verticalJumpVector = SpacedVector3.ZERO;
	private SpacedVector3 horizontalJumpVector = SpacedVector3.ZERO;
	private double jumpTimer; // seconds


	private static final double JUMP_BOOST_TIME = 0.5; // seconds
	private static final double JUMP_BOOST_FACTOR = 0.1; // relative the initial jump factor
	private static final double MARIO_AIR_CONTROL = 0.6; // 0 = no air control, 1 = full air control, >1 = imba
	private boolean wantsToJump;
	private int cameraRotate;
	private int combatStance = 1;

	private static final SpacedVector3 DOWN_SUCK_VECTOR = new SpacedVector3(0, -DOWN_SUCK, 0);

	@Inject
	public WalkingCharacterControl(
			ThirdPersonCamera cameraControl, PhysicsWorld physicsWorld, UserCharacter userCharacter,
			CharacterControlLuaHandler luaHandler,
			Camera camera,
			GroundImpactListener groundImpactListener,
			LocalRecorder recorder, ClientTeleporter teleporter) {
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
		jumpTimer += dt;

		// Rotate towards desired orientation
		if (userCharacter.isAlive()) {
			userCharacter.getPositionalData().setRotation(speedSlerp(userCharacter.getPositionalData().getRotation(),
					desiredRotation,
					rotationSpeed * dt));
		}

		verticalJumpVector = SpacedVector3.ZERO;
		if (wantsToJump) {
			if (userCharacter.getPhysics().canJump()) {
				verticalJumpVector = Vectors.setY(verticalJumpVector, jumpSpeed);
				jumpTimer = 0;
			} else if (jumpTimer < JUMP_BOOST_TIME) {
				verticalJumpVector = Vectors.setY(verticalJumpVector, jumpSpeed * JUMP_BOOST_FACTOR);
			}
		}

		self.setVelocity(self.getVelocity().add(verticalJumpVector));

		horizontalJumpVector = SpacedVector3.ZERO;
		// Apply gravity if not on ground
		if (!self.getGroundContact() && self.getGroundHit()) {
			double dy = self.getVelocity().getY();
			self.setVelocity(Vectors.setY(self.getVelocity(), dy - GRAVITY_CONSTANT * dt));

			SpacedVector3 groundEffect = Vectors.setY(self.getGroundNormal(), 0);
			if (groundEffect.getNormSq() > 0.01) {
				groundEffect = groundEffect.normalize();

				// add some extra outpush
				self.setVelocity(self.getVelocity().add(groundEffect.scalarMultiply(20 * dt)));

				// Clamp v
				//	double tod = self.getVelocity().dot(workerStoreA);
				//	if(tod<0) {
				//		workerStoreA.multiplyLocal(-tod);
				//		self.getVelocity().addLocal(workerStoreA);
				//	}
			}
		} else if (!verticalJumpVector.equals(SpacedVector3.ZERO) || !self.getGroundContact()) {

			double dx = self.getVelocity().getX();
			double dy = self.getVelocity().getY();
			double dz = self.getVelocity().getZ();

			self.setVelocity(Vectors.setY(self.getVelocity(), dy - GRAVITY_CONSTANT * dt));
			horizontalJumpVector = inputVector;

			if (wantsToJump || !self.getFilteredGroundContact(15)) {
				// Apply mario style air control
				horizontalJumpVector = horizontalJumpVector.normalize().scalarMultiply(MAX_AIRCONTROL_SPEED * MARIO_AIR_CONTROL);

				// Ensure we don't overspeed
				horizontalJumpVector = horizontalJumpVector.add(new SpacedVector3(dx, 0, dz));
				if (horizontalJumpVector.getNormSq() > ((0.9 * MAX_AIRCONTROL_SPEED) * (0.9 * MAX_AIRCONTROL_SPEED))) {
					horizontalJumpVector = horizontalJumpVector.normalize().scalarMultiply(0.9 * MAX_AIRCONTROL_SPEED);
				}
			}
			horizontalJumpVector = horizontalJumpVector.subtract(new SpacedVector3(dx, 0, dz));

		} else {
			if (self.getGroundHit()) {
				// align run vector perpendicular to ground normal
				SpacedVector3 temp = SpacedVector3.crossProduct(inputVector, SpacedVector3.PLUS_J);
				self.setVelocity(SpacedVector3.crossProduct(self.getGroundNormal(), temp).normalize().scalarMultiply(
						inputVector.getNorm()));
				//was self.setVelocity(SpacedVector3.crossProduct(self.getGroundNormal(), temp).normalize().scalarMultiply(inputVector.getNorm()));
			} else {
				self.setVelocity(inputVector);
			}

			if (inputVector.getY() <= 0 && (inputVector.getX() != 0 || inputVector.getZ() != 0)) {
				self.setVelocity(self.getVelocity().add(DOWN_SUCK_VECTOR));

			}
		}

		// Integrate
		stepAndUpdateManModel(self.getVelocity().add(horizontalJumpVector), physicsWorld, dt);

		// inputVector.set(Vector3.ZERO);
	}

	@Override
	public void animate() {
		CharacterPhysics self = userCharacter.getPhysics();
		if (keys.getLungeRight() == 1) {
			recorder.record(AnimationState.ATTACK_SWING_RIGHT);

		} else if (keys.getLungeRight() == 2) {
			recorder.record(AnimationState.ATTACK_SWING_RIGHT_HIGH);

		} else if (keys.getJumpKickSlash() == 1) {
			recorder.record(AnimationState.MELEE_KICK_SLASH_BACKHAND);

		} else if (keys.getShootLeft() == 1) {
			recorder.record(AnimationState.ATTACK_AIM_LEFT);
			combatStance = 1;
		} else if (keys.getStanceRighthand() == 1) {
			recorder.record(AnimationState.MELEE_STANCE_HIGH);
			combatStance = 2;
		} else if (keys.getStanceRighthand() == 2) {
			recorder.record(AnimationState.MELEE_STANCE_LOW);
			combatStance = 3;
		} else if (keys.getStanceRighthand() == 3) {
			recorder.record(AnimationState.MELEE_STANCE_BACKHAND);
			combatStance = 2;
		} else if (keys.getStanceRighthand() == 4) {
			recorder.record(AnimationState.MELEE_STANCE_STAB);
			combatStance = 2;
		} else if (keys.getStanceRighthand() == 4) {
			recorder.record(AnimationState.MELEE_STANCE_STAB);
			combatStance = 2;

		} else if (keys.getAimRifle() == 1) {
			recorder.record(AnimationState.COMBAT_AIM_RIFLE);
		} else if (keys.getFireRifle() == 1) {
			recorder.record(AnimationState.COMBAT_FIRE_RIFLE);
		} else if (keys.getSwingRifle() == 1) {
			recorder.record(AnimationState.COMBAT_SWING_RIFLE);
		} else if (keys.getStrikeRifle() == 1) {
			recorder.record(AnimationState.COMBAT_STRIKE_RIFLE_BUTT);

		} else if (!self.getFilteredGroundContact(9) || wantsToJump) {
			recorder.record(AnimationState.JUMP);

		} else if (self.getVelocity().getNormSq() > 0.01) {
			combatStance = 1;
			if (keys.getMoveFB() > 0 && keys.getMoveLR() > 0) {
				recorder.record(AnimationState.WALK_STRAFE_LEFT);
			} else if (keys.getMoveFB() > 0 && keys.getMoveLR() < 0) {
				recorder.record(AnimationState.WALK_STRAFE_RIGHT);
			} else if (keys.getMoveFB() < 0 && keys.getMoveLR() > 0) {
				recorder.record(AnimationState.WALK_BACK_STRAFE_LEFT);
			} else if (keys.getMoveFB() < 0 && keys.getMoveLR() < 0) {
				recorder.record(AnimationState.WALK_BACK_STRAFE_RIGHT);
			} else if (keys.getMoveFB() > 0) {
				recorder.record(AnimationState.WALK);
			} else if (keys.getMoveFB() < 0) {
				recorder.record(AnimationState.WALK_BACK);
			} else if (keys.getMoveLR() > 0) {
				recorder.record(AnimationState.STRAFE_LEFT);
			} else if (keys.getMoveLR() < 0) {
				recorder.record(AnimationState.STRAFE_RIGHT);
			}

		} else if (keys.getStanceLow() == 1) {
			recorder.record(AnimationState.MELEE_STANCE_PUSHED);
		} else if (keys.getStanceRifle() == 1) {
			recorder.record(AnimationState.COMBAT_STANCE_RIFLE);
		} else if (userCharacter.isInCombat()) {

			if (combatStance == 1) {
				recorder.record(AnimationState.COMBAT_STANCE_1);
			} else if (combatStance == 2) {
				recorder.record(AnimationState.COMBAT_STANCE_2);
			} else if (combatStance == 3) {
				recorder.record(AnimationState.COMBAT_STANCE_3);
			}


		} else if (keys.getMoveLR() != 0) {
			if (keys.getMoveLR() > 0) {
				recorder.record(AnimationState.TURN_LEFT);
			} else if (keys.getMoveLR() < 0) {
				recorder.record(AnimationState.TURN_RIGHT);
			}
		} else if ((cameraRotate != 0) && keys.rmbPressed()) {
			if (cameraRotate > 0) {
				recorder.record(AnimationState.TURN_LEFT);
				cameraRotate = 0;
			} else if (cameraRotate < 0) {
				recorder.record(AnimationState.TURN_RIGHT);
				cameraRotate = 0;
			}

		} else {
			recorder.record(AnimationState.IDLE);
		}
	}

	@Override
	public void updateSteering(double dt, PhysicsWorld physicsWorld) {
		double x = 0.0;
		if (keys.rmbPressed()) {
			x = keys.getMoveLR();
		} else {
			SpacedRotation fromSteering = Rotations.fromEulerAngles(dt * keys.getMoveLR() * 4, 0, 0);
			desiredRotation = desiredRotation.applyTo(fromSteering);
		}

		SpacedVector3 desiredMovement = new SpacedVector3(x, 0, keys.getMoveFB()).normalize();

		double speedMod = userCharacter.getBaseStats().getSpeedModifier().getValue();
		desiredMovement = Vectors.setX(desiredMovement, desiredMovement.getX() * MAX_STRAFE_SPEED * speedMod);
		if (desiredMovement.getZ() > 0) {
			desiredMovement = Vectors.setZ(desiredMovement, desiredMovement.getZ() * MAX_WALK_SPEED * speedMod);
		} else {
			desiredMovement = Vectors.setZ(desiredMovement, desiredMovement.getZ() * MAX_BACK_SPEED * speedMod);
		}

		desiredMovement = userCharacter.getRotation().applyTo(desiredMovement);

		double lerpSpeed = 14 * 5;
		double distance = SpacedVector3.distance(desiredMovement, inputVector);
		if (distance < lerpSpeed * dt) {
			inputVector = desiredMovement;
		} else {
			inputVector = VectorMath.lerp(inputVector, desiredMovement, lerpSpeed * dt / distance);
		}

		wantsToJump = keys.isWantsToJump();
	}


	@Override
	public void onMouseMove(int dx, int dy, boolean lmb, boolean rmb) {
		cameraControl.onMouseMove(camera, dx, dy, lmb, rmb);

		if (rmb) {
			SpacedVector3 cameraAim = new SpacedVector3(camera.getDirection().getX(), 0, camera.getDirection().getZ());

			cameraRotate = dx;

			if (cameraAim.getNormSq() > 0 && Math.abs(SpacedVector3.dotProduct(cameraAim, SpacedVector3.PLUS_J)) < 0.99f) {
				desiredRotation = VectorMath.lookAt(cameraAim, SpacedVector3.PLUS_J);
			}
		}
	}

	@Override
	public void onMouseWheel(int delta) {
		cameraControl.onMouseWheel(camera, delta);
	}

	@Override
	public void onSelected() {
		physicsWorld.teleportMan(userCharacter.getPosition());
	}
}
