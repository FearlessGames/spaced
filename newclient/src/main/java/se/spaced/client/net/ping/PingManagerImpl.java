package se.spaced.client.net.ping;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.fearless.common.lifetime.LifetimeListener;
import se.fearless.common.lifetime.LifetimeManager;
import se.fearless.common.time.TimeProvider;
import se.spaced.client.net.smrt.ServerConnection;
import se.spaced.client.net.smrt.ServerConnectionListener;

@Singleton
public class PingManagerImpl implements PingManager, ServerConnectionListener {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private final ServerConnection serverConnection;
	private final LifetimeManager lifetimeManager;
	private final TimeProvider timeProvider;
	private static final long PING_INTERVAL = 5000L;
	private volatile boolean run;
	private long lastPing;
	private long lastPingTime = -1;
	private volatile int messageId;
	private PingManagerImpl.Pinger pinger;
	private boolean waitingForReply;

	@Inject
	public PingManagerImpl(TimeProvider timeProvider, ServerConnection serverConnection, LifetimeManager lifetimeManager) {
		this.timeProvider = timeProvider;
		this.serverConnection = serverConnection;
		this.lifetimeManager = lifetimeManager;
		lifetimeManager.addListener(new LifetimeListener() {
			@Override
			public void onStart() {
			}

			@Override
			public void onShutdown() {
				stopPinger();
			}
		});
	}

	@Override
	public void pong(final long receivedTimestamp, final int pingId) {
		if (pingId != messageId) {
			throw new IllegalStateException("Got pong with pingId: " + pingId + " but last sent messageId was: " + messageId);
		}
		lastPingTime = receivedTimestamp - lastPing;
		logger.debug("Received pong with latency:" + getLatency());
		waitingForReply = false;
	}

	@Override
	public long getLatency() {
		return lastPingTime;
	}

	@Override
	public synchronized void disconnected(final String message) {
		logger.debug("stopping ping thread");
		stopPinger();
		messageId = 0;
	}

	private void stopPinger() {
		run = false;
		if (pinger != null) {
			pinger.interrupt();
			try {
				pinger.join();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
	}

	@Override
	public synchronized void connectionSucceeded(final String host, final int port) {
		stopPinger();
		run = true;
		pinger = new Pinger();
		pinger.start();
		logger.debug("started ping thread");
	}

	@Override
	public void connectionFailed(final String errorMessage) {
		stopPinger();
	}

	@Override
	public void sendPingRequest() {
		if (!waitingForReply) {
			++messageId;
			serverConnection.getReceiver().ping().ping(messageId);
			lastPing = timeProvider.now();
			waitingForReply = true;
		}
	}

	private class Pinger extends Thread {

		private Pinger() {
			super("PingerThread");
		}

		@Override
		public void run() {
			while (run && !lifetimeManager.isDead()) {
				try {
					Thread.sleep(PING_INTERVAL);
				} catch (InterruptedException e) {
					logger.info("Pinger interrupted!");
					Thread.currentThread().interrupt();
				}
				sendPingRequest();

			}
			logger.info("exited ping thread");
		}
	}
}
