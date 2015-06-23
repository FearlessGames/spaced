package se.spaced.server.net.listeners.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.fearless.common.time.TimeProvider;
import se.spaced.messages.protocol.c2s.ClientMovementMessages;
import se.spaced.server.model.Player;
import se.spaced.server.model.movement.MovementService;
import se.spaced.server.net.ClientConnection;
import se.spaced.shared.model.AnimationState;
import se.spaced.shared.playback.MovementPoint;
import se.spaced.shared.playback.RecordingPoint;

public class ClientMovementMessagesAuth implements ClientMovementMessages {
	private final Logger log = LoggerFactory.getLogger(getClass());
	private final ClientConnection clientConnection;
	private final TimeProvider timeProvider;
	private final MovementService movementService;

	public ClientMovementMessagesAuth(
			ClientConnection clientConnection,
			TimeProvider timeProvider,
			MovementService movementService) {
		this.clientConnection = clientConnection;
		this.timeProvider = timeProvider;
		this.movementService = movementService;
	}

	@Override
	public void hitGround(float impactSpeed) {
		Player player = clientConnection.getPlayer();
		if (player != null) {
			movementService.hitGround(player, impactSpeed);
		}
	}

	@Override
	public void sendPlayback(RecordingPoint<AnimationState> recordingPoint) {
		Player player = clientConnection.getPlayer();
		if (player != null) {
			if (movementService.isMovementAllowed(player)) {
				MovementPoint<AnimationState> point = player.getMovementPoint();

				// TODO: fix this properly instead - we have redundant storage of the same data
				if (point == null) {
					point = new MovementPoint<AnimationState>(timeProvider.now(), player.getCurrentAnimation(), player.getPosition(), player.getRotation());
				}

				MovementPoint<AnimationState> point2 = point.apply(recordingPoint);

				movementService.moveAndRotateEntity(player, point2);
				movementService.sendPlaybackData(player, recordingPoint);
			} else {
				// Hmm, how to handle this best?
				clientConnection.getReceiver().movement().restartRecorder(player.getPositionalData().toPositionalData());
			}
		}
	}

}