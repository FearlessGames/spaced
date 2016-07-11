package se.spaced.server.model.movement;

import se.ardortech.math.SpacedRotation;
import se.ardortech.math.SpacedVector3;
import se.spaced.server.model.ServerEntity;
import se.spaced.shared.model.AnimationState;
import se.spaced.shared.playback.MovementPoint;
import se.spaced.shared.playback.RecordingPoint;

public interface MovementService {

	void moveAndRotateEntity(ServerEntity entity, MovementPoint<AnimationState> point);

	void teleportEntity(ServerEntity entity, SpacedVector3 newPos, SpacedRotation rotation, long now);
	
	void hitGround(ServerEntity entity, float impactSpeed);

	void sendPlaybackData(ServerEntity entity, RecordingPoint<AnimationState> playbackData);

	boolean isMovementAllowed(ServerEntity entity);
}
