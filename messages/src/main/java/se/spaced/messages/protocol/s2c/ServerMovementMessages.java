package se.spaced.messages.protocol.s2c;

import se.smrt.core.SmrtProtocol;
import se.spaced.messages.protocol.Entity;
import se.spaced.shared.model.AnimationState;
import se.spaced.shared.model.PositionalData;
import se.spaced.shared.playback.RecordingPoint;

@SmrtProtocol
public interface ServerMovementMessages {
	void teleportTo(PositionalData positionalData);

	void sendPlayback(Entity entity, RecordingPoint<AnimationState> recordingPoint);

	void restartRecorder(PositionalData positionalData);
}