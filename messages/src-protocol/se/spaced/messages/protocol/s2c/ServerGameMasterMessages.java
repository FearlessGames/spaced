package se.spaced.messages.protocol.s2c;

import se.smrt.core.SmrtProtocol;

@SmrtProtocol
public interface ServerGameMasterMessages {
	void successNotification(String message);

	void failureNotification(String message);
}
