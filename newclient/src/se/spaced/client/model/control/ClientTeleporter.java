package se.spaced.client.model.control;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import se.spaced.client.model.UserCharacter;
import se.spaced.client.model.control.states.LocalRecorder;
import se.spaced.client.physics.PhysicsWorld;
import se.spaced.client.resources.zone.ScenegraphService;
import se.spaced.shared.model.AnimationState;
import se.spaced.shared.model.PositionalData;

@Singleton
public class ClientTeleporter {
	private PositionalData destination;

	private final PhysicsWorld physicsWorld;
	private final UserCharacter userCharacter;
	private final ScenegraphService scenegraphService;

	@Inject
	public ClientTeleporter(PhysicsWorld physicsWorld, UserCharacter userCharacter, ScenegraphService scenegraphService) {
		this.physicsWorld = physicsWorld;
		this.userCharacter = userCharacter;
		this.scenegraphService = scenegraphService;
	}

	public void setDestination(PositionalData destination) {
		this.destination = destination;
	}

	public void handle(LocalRecorder recorder) {
		PositionalData destination = this.destination;
		this.destination = null;
		if (destination == null) {
			return;
		}
		recorder.record(AnimationState.TELEPORT_OUT);
		forcePosition(destination);
		recorder.record(AnimationState.TELEPORT_IN);
	}

	public void forcePosition(PositionalData destination) {
		physicsWorld.teleportMan(destination.getPosition());
		userCharacter.getPhysics().setPosition(destination.getPosition());
		userCharacter.getPhysics().setRotation(destination.getRotation());
		userCharacter.setPositionalData(destination);
		userCharacter.setFrozen(true);
		scenegraphService.waitFrames(2, new Runnable() {
			@Override
			public void run() {
				userCharacter.setFrozen(false);
			}
		});
	}
}
