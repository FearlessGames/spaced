package se.spaced.client.core.states;

public interface GameState {
	void exit();

	void start();

	void update(GameStateContext context, double timePerFrame);

	void updateFixed(GameStateContext context, long millisPerFrame);
}
