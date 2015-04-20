package se.spaced.client.net.messagelisteners;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.spaced.client.model.ClientEntity;
import se.spaced.client.model.control.ClientTeleporter;
import se.spaced.client.model.control.states.LocalRecorder;
import se.spaced.messages.protocol.Entity;
import se.spaced.messages.protocol.s2c.ServerMovementMessages;
import se.spaced.shared.activecache.ActiveCache;
import se.spaced.shared.activecache.Job;
import se.spaced.shared.model.AnimationState;
import se.spaced.shared.model.PositionalData;
import se.spaced.shared.playback.RecordingPoint;

@Singleton
public class ServerMovementMessagesImpl implements ServerMovementMessages {
	private final Logger log = LoggerFactory.getLogger(getClass());
	private final ActiveCache<Entity, ClientEntity> entityCache;
	private final ClientTeleporter clientTeleporter;
	private final LocalRecorder recorder;

	@Inject
	public ServerMovementMessagesImpl(
			ActiveCache<Entity, ClientEntity> entityCache, ClientTeleporter clientTeleporter, LocalRecorder recorder) {
		this.entityCache = entityCache;
		this.clientTeleporter = clientTeleporter;
		this.recorder = recorder;
	}


	@Override
	public void teleportTo(PositionalData positionalData) {
		clientTeleporter.setDestination(positionalData);
	}

	@Override
	public void sendPlayback(Entity entity, final RecordingPoint<AnimationState> recordingPoint) {
		entityCache.runWhenReady(entity, new Job<ClientEntity>() {
			@Override
			public void run(ClientEntity value) {
				value.addRecordingPoint(recordingPoint);
			}
		});
	}

	@Override
	public void restartRecorder(PositionalData positionalData) {
		recorder.startRecording(positionalData.getPosition(), positionalData.getRotation(), AnimationState.IDLE);
		clientTeleporter.forcePosition(positionalData);
	}
}