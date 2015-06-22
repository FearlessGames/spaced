package se.spaced.server.net.listeners.auth;

import se.spaced.messages.protocol.c2s.ClientAccountMessages;
import se.spaced.messages.protocol.s2c.ServerConnectionMessages;
import se.spaced.server.account.Account;
import se.spaced.server.model.Player;
import se.spaced.server.model.PlayerType;
import se.spaced.server.net.ClientConnection;
import se.spaced.server.persistence.DuplicateObjectException;
import se.spaced.server.player.PlayerCreationException;
import se.spaced.server.player.PlayerCreationService;
import se.spaced.shared.model.Gender;
import se.spaced.shared.model.player.PlayerCreationFailure;


public class ClientAccountMessageAuth implements ClientAccountMessages {
	private final ServerConnectionMessages response;
	private final ClientConnection clientConnection;
	private final PlayerCreationService playerCreationService;


	public ClientAccountMessageAuth(
			ClientConnection clientConnection,
			ServerConnectionMessages response,
			PlayerCreationService playerCreationService) {
		this.clientConnection = clientConnection;
		this.response = response;
		this.playerCreationService = playerCreationService;
	}

	@Override
	public void createCharacter(final String name, final Gender gender) {
		Account account = clientConnection.getAccount();

		//todo: implmenet a clientside selection if its a gm or not, for now, only create as regular

		try {
			Player newPlayer = playerCreationService.createDefaultPlayer(account, name, gender, PlayerType.REGULAR);
			clientConnection.getReceiver().account().playerCreated(newPlayer.createEntityData());
		} catch (DuplicateObjectException e) {
			clientConnection.getReceiver().account().failedToCreatePlayer(name, PlayerCreationFailure.NAME_EXISTS);
		} catch (PlayerCreationException e) {
			clientConnection.getReceiver().account().failedToCreatePlayer(name, e.getPlayerCreationFailure());
		}
	}
}