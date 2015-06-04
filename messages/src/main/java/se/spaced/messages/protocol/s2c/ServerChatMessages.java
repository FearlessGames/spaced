package se.spaced.messages.protocol.s2c;

import se.smrt.core.SmrtProtocol;
import se.spaced.messages.protocol.Entity;

@SmrtProtocol
public interface ServerChatMessages {
	void playerSaid(String name, String message);

	void whisperFrom(String name, String message);

	void whisperTo(String name, String message);

	void systemMessage(String message);

	void emote(Entity performer, String emoteFile, String emoteText);
}
