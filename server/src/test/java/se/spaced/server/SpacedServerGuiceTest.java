package se.spaced.server;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.Before;
import org.junit.Test;
import se.spaced.server.guice.modules.PersistanceDaoModule;
import se.spaced.server.guice.modules.ResourcesModule;
import se.spaced.server.guice.modules.ServiceModule;
import se.spaced.server.guice.modules.SystemTimeModule;
import se.spaced.server.guice.modules.XStreamModule;

import static org.junit.Assert.assertNotNull;

public class SpacedServerGuiceTest {
	private SpacedServer spacedServer;

	@Before
	public void setUp() {
		Injector injector = Guice.createInjector(
				new CommandLineParser().getModule(),
				new ServiceModule(),
				new PersistanceDaoModule(),
				new SystemTimeModule(),
				new ResourcesModule(),
				new XStreamModule());
		spacedServer = injector.getInstance(SpacedServer.class);
	}

	@Test
	public void testSpacedServer() {
		assertNotNull(spacedServer);
	}
}
