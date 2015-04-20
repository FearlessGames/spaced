package se.spaced.client.core;

import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Names;
import se.spaced.client.core.states.GameState;
import se.spaced.client.core.states.GameStateHandler;
import se.spaced.client.core.states.StateChangeListener;
import se.spaced.shared.util.AbstractMockModule;
import se.spaced.shared.util.ListenerDispatcher;

public class MockCoreModule extends AbstractMockModule {

	@Override
	protected void configure() {
		bindMock(GameState.class, Names.named("startState"));
		bindMock(GameState.class, Names.named("disconnectedState"));
		bindMock(GameState.class, Names.named("selectServerState"));
		bindMock(GameState.class, Names.named("loginState"));
		bindMock(GameState.class, Names.named("inGameState"));
		bindMock(GameState.class, Names.named("selectCharacterState"));
		bindMock(GameState.class, Names.named("createCharacterState"));
	}

	@Provides
	@Singleton
	public GameStateHandler getGameStateHandler() {
		return new GameStateHandler(ListenerDispatcher.create(StateChangeListener.class));
	}
}