package se.spaced.server.net.listeners.auth;

import se.spaced.messages.protocol.c2s.ClientPingMessages;
import se.spaced.messages.protocol.s2c.ServerPingMessages;

public class PingMessageAuth implements ClientPingMessages {

	private final ServerPingMessages response;


	public PingMessageAuth(ServerPingMessages response) {
		this.response = response;
	}

	@Override
	public void ping(int i) {
		response.pong(i);
	}
}