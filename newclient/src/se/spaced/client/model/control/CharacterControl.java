package se.spaced.client.model.control;

import com.ardor3d.renderer.Camera;
import se.ardortech.input.ClientMouseButton;
import se.ardortech.math.SpacedRotation;
import se.ardortech.math.SpacedVector3;
import se.ardortech.math.VectorMath;
import se.spaced.client.model.CharacterPhysics;
import se.spaced.client.model.UserCharacter;
import se.spaced.client.model.control.states.LocalRecorder;
import se.spaced.client.physics.PhysicsWorld;
import se.spaced.shared.model.AnimationState;
import se.spaced.shared.model.PositionalData;

public abstract class CharacterControl {

	protected final LocalRecorder recorder;
	private final ClientTeleporter clientTeleporter;

	protected UserCharacter userCharacter;
	protected CharacterControlLuaHandler keys;
	protected final PhysicsWorld physicsWorld;
	protected final Camera camera;
	private final GroundImpactListener groundImpactListener;

	public CharacterControl(
			UserCharacter userCharacter,
			CharacterControlLuaHandler luaHandler,
			PhysicsWorld physicsWorld,
			Camera camera,
			GroundImpactListener groundImpactListener, LocalRecorder recorder, ClientTeleporter clientTeleporter) {
		this.userCharacter = userCharacter;
		this.keys = luaHandler;
		this.physicsWorld = physicsWorld;
		this.camera = camera;
		this.groundImpactListener = groundImpactListener;
		this.recorder = recorder;
		this.clientTeleporter = clientTeleporter;
	}

	public void updateFixed(long millisPerFrame, PhysicsWorld physicsWorld) {
		updatePhysics(millisPerFrame, physicsWorld);
	}

	public void update(double dt, PhysicsWorld physicsWorld) {
		if (userCharacter == null) {
			return;
		}
		if (userCharacter.isAlive()) {
			updateSteering(dt, physicsWorld);
		}
		updateCamera(dt, physicsWorld);
		if (userCharacter.isAlive()) {
			animate();
		} else {
			recorder.record(AnimationState.DEAD);
		}
		clientTeleporter.handle(recorder);
	}

	public void stepAndUpdateManModel(SpacedVector3 desiredVelocity, PhysicsWorld physicsWorld, double dt) {
		CharacterPhysics self = userCharacter.getPhysics();

		// Set velocity and integrate
		physicsWorld.setManBodyVelocity(desiredVelocity);
		if (desiredVelocity.equals(SpacedVector3.ZERO) && self.getGroundContact()) {
			// Don't step the man, just update ground contact
			physicsWorld.stepManStill();
		} else {
			physicsWorld.stepMan(dt);
		}

		// Read back results and adjust for manheight
		self.setPosition(physicsWorld.getManPosition().subtract(new SpacedVector3(0, physicsWorld.getManHalfHeight(), 0)));

		// Update manifold
		boolean oldGround = self.getGroundContact();
		boolean newGround = physicsWorld.getGroundContact();
		if (!oldGround && newGround) {
			reportGroundImpact(self.getVelocity());
		}
		self.setGroundContact(newGround);
		self.setGroundNormal(physicsWorld.getGroundNormal());
		self.setGroundHit(physicsWorld.getGroundHit());

		// Update graphics representation with results
		userCharacter.setPositionalData(new PositionalData(self.getPosition(), self.getRotation()));
	}

	private void reportGroundImpact(SpacedVector3 velocity) {
		double impactSpeed = Math.abs(velocity.getY());
		if (impactSpeed > 8) {
			//groundImpactListener.notifyHit(impactSpeed);
		}
	}

	public SpacedRotation speedSlerp(SpacedRotation rotation, SpacedRotation desiredRotation, double v) {
		double dot = Math.min(1.0, Math.abs(SpacedRotation.dot(rotation, desiredRotation)));
		double theta = 2 * Math.acos(dot);
		if (theta != 0) {
			return VectorMath.slerp(rotation, desiredRotation, Math.min(1.0, v / theta));
		}
		return desiredRotation;
	}

	public abstract void updateSteering(double dt, PhysicsWorld physicsWorld);

	public abstract void updatePhysics(long millisPerFrame, PhysicsWorld physicsWorld);

	public abstract void updateCamera(double dt, PhysicsWorld physicsWorld);

	public abstract void animate();

	public abstract void onMouseMove(int dx, int dy, boolean lmb, boolean rmb);

	public abstract void onMouseWheel(int wheelDelta);

	public void onMouseButton(ClientMouseButton button, boolean pressed, int x, int y) {
	}

	public void onSelected() {
	}

	public void onDeselected() {
	}

}
