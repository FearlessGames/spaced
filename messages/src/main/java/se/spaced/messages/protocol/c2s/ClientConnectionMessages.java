package se.spaced.messages.protocol.c2s;

import se.fearless.common.uuid.UUID;
import se.smrt.core.SmrtProtocol;

@SmrtProtocol
public interface ClientConnectionMessages {
	void loginAccount(String accountName, String hash, String address);

	void authenticateAccount(String accountName, String hash, String ip, String key);

	void requestPlayerList();

	void requestPlayerInfo(UUID playerId);

	void requestLoginSalts(String userName);

	void loginCharacter(UUID playerId);

	void logout();

	void getLocation(UUID playerId);
}
