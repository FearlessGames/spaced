package se.spaced.client.core;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.Singleton;
import com.google.inject.name.Names;
import se.spaced.client.core.states.CreateCharacterState;
import se.spaced.client.core.states.DisconnectedState;
import se.spaced.client.core.states.GameState;
import se.spaced.client.core.states.GameStateContext;
import se.spaced.client.core.states.GameStateHandler;
import se.spaced.client.core.states.GameStateUpdater;
import se.spaced.client.core.states.LoginState;
import se.spaced.client.core.states.PromoGameState;
import se.spaced.client.core.states.SelectCharacterState;
import se.spaced.client.core.states.SelectServerState;
import se.spaced.client.core.states.StartState;
import se.spaced.client.core.states.StateChangeListener;
import se.spaced.client.core.states.WorldGameState;
import se.spaced.shared.util.ListenerDispatcher;

public class CoreModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(GameStateContext.class).to(GameStateHandler.class).in(Scopes.SINGLETON);
		bind(GameStateUpdater.class).to(GameStateHandler.class).in(Scopes.SINGLETON);

		bind(GameState.class).annotatedWith(Names.named("startState")).to(StartState.class);
		bind(GameState.class).annotatedWith(Names.named("disconnectedState")).to(DisconnectedState.class);
		bind(GameState.class).annotatedWith(Names.named("selectServerState")).to(SelectServerState.class);
		bind(GameState.class).annotatedWith(Names.named("loginState")).to(LoginState.class);
		bind(GameState.class).annotatedWith(Names.named("selectCharacterState")).to(SelectCharacterState.class);
		bind(GameState.class).annotatedWith(Names.named("createCharacterState")).to(CreateCharacterState.class);
		bind(GameState.class).annotatedWith(Names.named("inGameState")).to(WorldGameState.class);
		bind(GameState.class).annotatedWith(Names.named("promoState")).to(PromoGameState.class);
	}

	@Provides
	@Singleton
	public ListenerDispatcher<StateChangeListener> getStateChangeListeners() {
		return ListenerDispatcher.create(StateChangeListener.class);
	}

	@Provides
	@Singleton
	public GameStateHandler getGameStateHandler(ListenerDispatcher<StateChangeListener> listeners) {
		return new GameStateHandler(listeners);
	}
}