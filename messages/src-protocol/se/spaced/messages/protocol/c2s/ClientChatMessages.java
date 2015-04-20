package se.spaced.messages.protocol.c2s;

import se.smrt.core.SmrtProtocol;

@SmrtProtocol
public interface ClientChatMessages {
	void say(String message);

	void whisper(String name, String message);
}
