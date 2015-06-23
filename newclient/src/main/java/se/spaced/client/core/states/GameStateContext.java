package se.spaced.client.core.states;

public interface GameStateContext {
	void changeState(GameState state);
	GameState current();
}
