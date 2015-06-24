package se.spaced.client.net.ping;

import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.fearless.common.time.TimeProvider;
import se.spaced.messages.protocol.s2c.ServerPingMessages;

public class PingManagerAdapter implements ServerPingMessages {

	private final PingManager pingManager;
	private final TimeProvider timeProvider;
	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Inject
	public PingManagerAdapter(PingManager pingManager, TimeProvider timeProvider) {
		this.pingManager = pingManager;
		this.timeProvider = timeProvider;
	}

	@Override
	public void pong(int id) {
		try {
			pingManager.pong(timeProvider.now(), id);
		} catch (IllegalStateException e) {
			logger.warn(e.getMessage());	
		}
	}
}
