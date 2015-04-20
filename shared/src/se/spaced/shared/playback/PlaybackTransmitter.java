package se.spaced.shared.playback;

public interface PlaybackTransmitter<T> {
	void transmit(RecordingPoint<T> point);
}
