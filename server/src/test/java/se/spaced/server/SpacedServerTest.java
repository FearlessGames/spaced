package se.spaced.server;

import org.junit.Before;
import org.junit.Test;
import se.fearless.common.lifetime.LifetimeManager;
import se.fearless.common.time.TimeProvider;
import se.spaced.server.model.action.ActionScheduler;

import static se.mockachino.Mockachino.*;
import static se.mockachino.matchers.Matchers.any;
import static se.mockachino.matchers.Matchers.anyLong;

public class SpacedServerTest {
	private SpacedServer spacedServer;
	private ActionScheduler scheduler;
	private TimeProvider timeProvider;
	private LifetimeManager lifetimeManager;
	private ServerNotifier serverNotifier;

	@Before
	public void setUp() {
		scheduler = mock(ActionScheduler.class);
		lifetimeManager = mock(LifetimeManager.class);
		timeProvider = mock(TimeProvider.class);
		serverNotifier = mock(ServerNotifier.class);
		spacedServer = new SpacedServer(scheduler, timeProvider, lifetimeManager, serverNotifier);
	}

	@Test
	public void testExceptionInRun() {
		stubThrow(new RuntimeException("Something blew up")).on(scheduler).tick(anyLong());

		spacedServer.run();
		verifyOnce().on(serverNotifier).notifyServerCrash(any(RuntimeException.class));
		verifyOnce().on(lifetimeManager).shutdown();
	}
}