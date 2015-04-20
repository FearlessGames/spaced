package se.spaced.client.core.states;

public interface GameStateUpdater {
	void update(double timePerFrame);

	void updateFixed(long millisPerFrame);
}
