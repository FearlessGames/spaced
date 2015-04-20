package se.spaced.client.core.states;

import com.google.inject.Inject;
import com.google.inject.name.Named;

public class DisconnectedState implements GameState {
	private final GameState nextState;

	@Inject
	public DisconnectedState(@Named("selectServerState") GameState nextState) {
		this.nextState = nextState;
	}

	@Override
	public void exit() {
	}

	@Override
	public void start() {
	}

	@Override
	public void update(GameStateContext context, double timePerFrame) {
		context.changeState(nextState);
	}

	@Override
	public void updateFixed(GameStateContext context, long millisPerFrame) {
	}
}
