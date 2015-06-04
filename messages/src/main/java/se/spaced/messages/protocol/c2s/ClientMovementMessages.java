package se.spaced.messages.protocol.c2s;

import se.smrt.core.SmrtProtocol;
import se.spaced.shared.model.AnimationState;
import se.spaced.shared.playback.RecordingPoint;

@SmrtProtocol
public interface ClientMovementMessages {

	void hitGround(float impactSpeed);

	void sendPlayback(RecordingPoint<AnimationState> recordingPoint);
}
