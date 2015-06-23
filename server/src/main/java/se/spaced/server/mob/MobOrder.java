package se.spaced.server.mob;

import se.ardortech.math.SpacedVector3;
import se.fearless.common.time.TimeProvider;
import se.spaced.server.model.ServerEntity;
import se.spaced.server.model.movement.MovementService;
import se.spaced.shared.model.AnimationState;
import se.spaced.shared.model.PositionalData;
import se.spaced.shared.playback.MovementRecorder;
import se.spaced.shared.playback.PlaybackTransmitter;
import se.spaced.shared.playback.RecordingPoint;

public class MobOrder {
	private final MovementRecorder<AnimationState> recorder;
	private double speed;
	private PositionalData teleportTo;

	public MobOrder(TimeProvider timeProvider, final MovementService movementService, final ServerEntity mob) {
		recorder = new MovementRecorder<AnimationState>(new PlaybackTransmitter<AnimationState>() {
			@Override
			public void transmit(RecordingPoint<AnimationState> recordingPoint) {
				movementService.sendPlaybackData(mob, recordingPoint);
			}
		}, mob.getPosition(), mob.getRotation(), AnimationState.IDLE, timeProvider.now());
	}

	public MovementRecorder<AnimationState> getRecorder() {
		return recorder;
	}

	private SpacedVector3 walkTo;
	private ServerEntity lookAt;

	public void walkTo(SpacedVector3 target, double speed) {
		this.walkTo = target;
		this.speed = speed;
	}

	public void lookAt(ServerEntity target) {
		this.lookAt = target;
	}

	public SpacedVector3 getWalkTo() {
		return walkTo;
	}

	public ServerEntity getLookAt() {
		return lookAt;
	}

	public double getSpeed() {
		return speed;
	}

	@Override
	public String toString() {
		return "MobOrder{" +
				"speed=" + speed +
				", walkTo=" + walkTo +
				", lookAt=" + lookAt +
				'}';
	}

	public void teleportTo(PositionalData positionalData) {
		teleportTo = positionalData;
	}

	public PositionalData getTeleportTo() {
		return teleportTo;
	}

	public void setTeleportTo(PositionalData teleportTo) {
		this.teleportTo = teleportTo;
	}
}
