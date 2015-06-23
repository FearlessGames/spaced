package se.spaced.client.game.logic.local;


public interface LocalChatLogic {
	void say(String message);

	void whisper(String playerName, String message);

	void systemMessage(String message);
}
