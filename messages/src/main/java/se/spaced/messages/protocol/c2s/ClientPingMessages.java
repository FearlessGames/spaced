package se.spaced.messages.protocol.c2s;

import se.smrt.core.SmrtProtocol;

@SmrtProtocol
public interface ClientPingMessages {
	void ping(int id);
}
