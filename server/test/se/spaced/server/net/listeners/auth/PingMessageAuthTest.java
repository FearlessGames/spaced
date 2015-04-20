package se.spaced.server.net.listeners.auth;

import org.junit.Test;
import se.spaced.messages.protocol.s2c.ServerPingMessages;

import static se.mockachino.Mockachino.*;

public class PingMessageAuthTest {
	@Test
	public void testPing() throws Exception {
		ServerPingMessages serverPingMessages = mock(ServerPingMessages.class);
		PingMessageAuth pingMessageAuth = new PingMessageAuth(serverPingMessages);
		int id = 1337;
		pingMessageAuth.ping(id);

		verifyOnce().on(serverPingMessages).pong(id);
	}
}
