package se.spaced.client.game.logic.local;

import se.fearlessgames.common.util.uuid.UUID;
import se.spaced.messages.protocol.Salts;

public interface LocalLoginLogic {
	void loginAccount(String accountName, String password, Salts authSalts);

	void authenticateAccount(String accountName, String password, Salts authSalts, String key);

	void loginCharacter(UUID playerId);

	void requestAuthSalts(String userName);
}
