package se.spaced.client.ardor.ui.api;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import se.krka.kahlua.integration.annotations.LuaMethod;
import se.spaced.client.core.states.GameState;
import se.spaced.client.core.states.GameStateContext;
import se.spaced.client.net.smrt.ServerConnection;
import se.spaced.shared.model.Gender;

@Singleton
public class AccountApi {
	private final ServerConnection serverConnection;
	private final GameStateContext context;
	private final GameState createCharacterState;
	private final GameState selectCharacterState;

	@Inject
	public AccountApi(
			final ServerConnection serverConnection, GameStateContext context,
			@Named("createCharacterState") GameState createCharacterState,
			@Named("selectCharacterState") GameState selectCharacterState) {
		this.serverConnection = serverConnection;
		this.context = context;
		this.createCharacterState = createCharacterState;
		this.selectCharacterState = selectCharacterState;
	}

	@LuaMethod(name = "CreateCharacterScreen", global = true)
	public void createCharacterScreen() {
		context.changeState(createCharacterState);
	}

	@LuaMethod(name = "SelectCharacterScreen", global = true)
	public void selectCharacterScreen() {
		context.changeState(selectCharacterState);
	}

	@LuaMethod(name = "CreateCharacter", global = true)
	public void createCharacter(final String name, final Gender gender) {
		serverConnection.getReceiver().account().createCharacter(name, gender);
	}
}
