package se.spaced.shared.playback;

import se.ardortech.math.SpacedRotation;
import se.ardortech.math.SpacedVector3;

public interface PlaybackUpdater<T> {
	void updateState(long t, T state, SpacedVector3 position, SpacedRotation rotation);
	void updatePosition(long t, SpacedVector3 position, SpacedRotation rotation);
}
