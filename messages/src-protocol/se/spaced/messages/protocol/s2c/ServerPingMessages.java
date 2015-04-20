package se.spaced.messages.protocol.s2c;

import se.smrt.core.SmrtProtocol;

@SmrtProtocol
public interface ServerPingMessages {
	void pong(int id);
}
