package se.spaced.client.game.logic.implementations.chat;

import se.spaced.client.model.ClientEntity;

public interface ChatLogicListener {

	void systemMessage(String message);

	void playerSaid(String player, String message);

	void playerWhispered(String fromPlayer, String message);

	void selfWhispered(String toPlayer, String message);

	void playerEmoted(ClientEntity entity, String emoteText, String emoteAudioResource);
}
