package se.spaced.client.net.ping;

import org.junit.Before;
import org.junit.Test;
import se.fearless.common.lifetime.LifetimeManager;
import se.fearless.common.mock.MockUtil;
import se.fearless.common.util.MockTimeProvider;
import se.spaced.client.net.smrt.ServerConnection;

import static org.junit.Assert.assertEquals;
import static se.mockachino.Mockachino.mock;
import static se.mockachino.Mockachino.verifyOnce;


public class PingManagerImplTest {
	private static final int FIRST_PING_ID = 1;
	private MockTimeProvider timeProvider;
	private PingManagerImpl pingManager;
	private ServerConnection serverConnection;
	private LifetimeManager lifetimeManager;

	@Before
	public void setup() {
		timeProvider = new MockTimeProvider();
		serverConnection = MockUtil.deepMock(ServerConnection.class);
		lifetimeManager = mock(LifetimeManager.class);
		pingManager = new PingManagerImpl(timeProvider, serverConnection, lifetimeManager);
	}

	@Test
	public void simpleTest() {
		assertEquals(-1, pingManager.getLatency());
		pingManager.sendPingRequest();
		verifyOnce().on(serverConnection.getReceiver().ping()).ping(FIRST_PING_ID);
		timeProvider.advanceTime(123);
		pingManager.pong(timeProvider.now(), FIRST_PING_ID);
		assertEquals(123, pingManager.getLatency());
	}


	@Test(expected = IllegalStateException.class)
	public void simpleTest2() {
		assertEquals(-1, pingManager.getLatency());
		pingManager.sendPingRequest();
		verifyOnce().on(serverConnection.getReceiver().ping()).ping(FIRST_PING_ID);
		timeProvider.advanceTime(123);
		pingManager.pong(timeProvider.now(), FIRST_PING_ID + 1);
	}

}
