package se.spaced.client.core.states;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import java.awt.SplashScreen;

public class StartState implements GameState {
	private final GameState nextState;

	@Inject
	public StartState(@Named("promoState") GameState nextState) {
		this.nextState = nextState;
	}

	@Override
	public void exit() {
		SplashScreen splashScreen = SplashScreen.getSplashScreen();
		if (splashScreen != null) {
			splashScreen.close();
		}
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