package se.spaced.client.game.logic.remote;

import se.spaced.client.model.ClientEntity;

public interface RemoteChatLogic {
	// Chat
	void playerSaid(String player, String message);

	void playerWhispered(String fromName, String message);

	void selfWhispered(String toName, String message);

	void systemMessage(String message);

	void playerEmoted(ClientEntity clientEntity, String name, String emoteText);
}
