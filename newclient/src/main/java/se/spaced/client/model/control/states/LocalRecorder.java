package se.spaced.client.model.control.states;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import se.ardortech.math.SpacedRotation;
import se.ardortech.math.SpacedVector3;
import se.fearless.common.time.TimeProvider;
import se.spaced.client.model.UserCharacter;
import se.spaced.client.net.smrt.ServerConnection;
import se.spaced.shared.model.AnimationState;
import se.spaced.shared.playback.MovementRecorder;
import se.spaced.shared.playback.PlaybackTransmitter;
import se.spaced.shared.playback.RecordingPoint;

@Singleton
public class LocalRecorder {

	private final UserCharacter userCharacter;
	private final ServerConnection serverConnection;
	private final TimeProvider timeProvider;

	private MovementRecorder<AnimationState> recorder;
	private AnimationState currentAnimationState;

	@Inject
	public LocalRecorder(UserCharacter userCharacter, TimeProvider timeProvider, final ServerConnection serverConnection) {
		this.timeProvider = timeProvider;
		this.serverConnection = serverConnection;
		this.userCharacter = userCharacter;
	}

	public void record(AnimationState animationState) {
		if (currentAnimationState == null || !currentAnimationState.is(animationState)) {
			userCharacter.playAnimation(animationState);
			currentAnimationState = animationState;
		}
		add(currentAnimationState, userCharacter.getPosition(), userCharacter.getRotation());
	}

	private void add(AnimationState currentAnimationState, SpacedVector3 position, SpacedRotation rotation) {
		if (recorder != null) {
			recorder.add(timeProvider.now(), position, rotation, currentAnimationState);
		}
	}

	public void startRecording(SpacedVector3 startPosition, SpacedRotation startRotation, AnimationState startState) {
		currentAnimationState = startState;
		recorder = new MovementRecorder<AnimationState>(
				new PlaybackTransmitter<AnimationState>() {
					@Override
					public void transmit(RecordingPoint<AnimationState> recordingPoint) {
						serverConnection.getReceiver().movement().sendPlayback(recordingPoint);
					}
				},
				startPosition, startRotation, startState, timeProvider.now());
	}

	public AnimationState state() {
		if (recorder == null) {
			return AnimationState.IDLE;
		}
		return recorder.state();
	}
}
