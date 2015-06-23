package se.spaced.client.presenter;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.spaced.client.ardor.ui.api.ConnectionApi;
import se.spaced.client.ardor.ui.events.WorldGuiEvents;
import se.spaced.client.core.states.GameState;
import se.spaced.client.core.states.GameStateContext;
import se.spaced.client.model.listener.LoginListener;
import se.spaced.client.net.smrt.ServerConnectionListener;
import se.spaced.shared.events.EventHandler;
import se.spaced.shared.network.protocol.codec.datatype.EntityData;

import java.util.List;

@Singleton
public class LoginPresenter implements ServerConnectionListener, LoginListener {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private final EventHandler eventHandler;
	private final GameStateContext context;
	private final GameState disconnectedState;
	private final GameState loginState;
	private final GameState worldGameState;
	private final GameState selectCharacterState;
	private final ConnectionApi connectionApi;

	@Inject
	public LoginPresenter(
			EventHandler eventHandler,
			GameStateContext context,
			@Named("disconnectedState") GameState disconnectedState,
			@Named("loginState") GameState loginState,
			@Named("inGameState") GameState worldGameState,
			@Named("selectCharacterState") GameState selectCharacterState, ConnectionApi connectionApi) {
		this.eventHandler = eventHandler;
		this.context = context;
		this.disconnectedState = disconnectedState;
		this.loginState = loginState;
		this.worldGameState = worldGameState;
		this.selectCharacterState = selectCharacterState;
		this.connectionApi = connectionApi;
	}

	// ServerConnection listener
	@Override
	public void disconnected(final String message) {
		context.changeState(disconnectedState);
	}

	@Override
	public void connectionFailed(final String errorMessage) {
		context.changeState(disconnectedState);
	}

	@Override
	public void connectionSucceeded(final String host, final int port) {
		// TODO: add some sort of handshake
		context.changeState(loginState);
	}

	// LoginListener
	@Override
	public void successfulPlayerLogin() {
		eventHandler.fireEvent(WorldGuiEvents.PLAYER_LOGIN);
		context.changeState(worldGameState);
	}

	@Override
	public void failedPlayerLogin(final String message) {
		// TODO: Implement failed login / wrong password etc dialog in login state
		logger.warn("Failed to login player - {}", message);
		context.changeState(disconnectedState);
	}

	@Override
	public void characterListUpdated(final List<EntityData> characters) {
		connectionApi.setCharacters(characters);
		context.changeState(selectCharacterState);
	}

	@Override
	public void successfulPlayerLogout() {
		context.changeState(selectCharacterState);
	}
}
