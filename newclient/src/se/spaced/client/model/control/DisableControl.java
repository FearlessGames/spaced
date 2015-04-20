package se.spaced.client.model.control;

import com.ardor3d.renderer.Camera;
import com.google.inject.Inject;
import se.spaced.client.model.control.states.LocalRecorder;
import se.spaced.client.physics.PhysicsWorld;
import se.spaced.shared.model.AnimationState;

public class DisableControl extends CharacterControl {

	@Inject
	public DisableControl(Camera camera, GroundImpactListener groundImpactListener, LocalRecorder recorder, ClientTeleporter teleporter) {
		super(null, null, null, camera, groundImpactListener, recorder, teleporter);
	}

	@Override
	public void updateSteering(double dt, PhysicsWorld physicsWorld) {
	}

	@Override
	public void updatePhysics(long millisPerFrame, PhysicsWorld physicsWorld) {
	}

	@Override
	public void updateCamera(double dt, PhysicsWorld physicsWorld) {
	}

	@Override
	public void animate() {
		recorder.record(AnimationState.IDLE);
	}

	@Override
	public void onMouseMove(int dx, int dy, boolean lmb, boolean rmb) {
	}

	@Override
	public void onMouseWheel(int wheelDelta) {
	}
}
