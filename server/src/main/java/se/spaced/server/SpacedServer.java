package se.spaced.server;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.fearless.common.lifetime.LifetimeManager;
import se.fearless.common.time.TimeProvider;
import se.spaced.server.model.action.ActionScheduler;

@Singleton
public class SpacedServer implements Runnable {
	final Logger logger = LoggerFactory.getLogger(getClass());
	private static final long SERVER_SLEEP = 5L;

	private final ActionScheduler scheduler;
	private final TimeProvider timeProvider;
	private final LifetimeManager lifetimeManager;
	private final ServerNotifier serverNotifier;

	@Inject
	public SpacedServer(ActionScheduler scheduler, TimeProvider timeProvider, LifetimeManager lifetimeManager, ServerNotifier serverNotifier) {
		this.scheduler = scheduler;
		this.timeProvider = timeProvider;
		this.lifetimeManager = lifetimeManager;
		this.serverNotifier = serverNotifier;
		logger.info("Created new SpacedServer");
	}


	@Override
	public void run() {
		try {
			lifetimeManager.start();
			try {
				lifetimeManager.waitForStart();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
			logger.info("Starting main loop");

			while (!lifetimeManager.isDead()) {
				scheduler.tick(timeProvider.now());
				try {
					timeProvider.sleep(SERVER_SLEEP);
				} catch (InterruptedException e) {
					logger.error(e.getMessage());
				}
			}
		} catch (RuntimeException e) {
			logger.error("Exception in main thread - shutting down server", e);
			serverNotifier.notifyServerCrash(e);
			lifetimeManager.shutdown();
		} finally {
			logger.info("Shutting down main loop!");
		}
	}
}
