package se.spaced.client.core.states;

public interface StateChangeListener {
	void onStateChange(GameState oldState, GameState newState);
}
