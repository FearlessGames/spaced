package se.spaced.client.net.remoteservices;

import org.junit.Ignore;
import org.junit.Test;
import se.fearlessgames.common.lifetime.LifetimeManager;
import se.mockachino.annotations.*;
import se.spaced.client.net.GameServer;
import se.spaced.shared.events.EventHandler;

import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.List;

import static se.mockachino.Mockachino.*;
import static se.mockachino.matchers.Matchers.*;

@Ignore
public class ServerInfoWSCImplTest {
	/**
	 * This test requires a running webserver. Toggle ignore to run
	 *
	 * @throws MalformedURLException
	 */

	@Mock
	private EventHandler eventHandler;

	@Test
	public void test() throws MalformedURLException {
		setupMocks(this);
		final List<GameServer> gameServers = Arrays.asList(new GameServer("server", "localhost"));
		LifetimeManager lifetimeManager = mock(LifetimeManager.class);
		ServerInfoWSCImpl client = new ServerInfoWSCImpl(gameServers, eventHandler, lifetimeManager);
		client.fetchServerInfo();
		verifyOnce().on(eventHandler).fireAsynchEvent(any(String.class), any(Object.class));

	}

}
