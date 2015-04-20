package se.spaced.client.net.messagelisteners;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.spaced.client.model.ClientEntity;
import se.spaced.client.net.smrt.ServerConnection;
import se.spaced.messages.protocol.Entity;
import se.spaced.shared.activecache.ActiveCacheImpl;
import se.spaced.shared.activecache.Job;
import se.spaced.shared.activecache.KeyRequestHandler;

public class EntityCacheImpl extends ActiveCacheImpl<Entity, ClientEntity> {
	private static final Logger log = LoggerFactory.getLogger(EntityCacheImpl.class);
	public EntityCacheImpl(final ServerConnection serverConnection) {
		super(new KeyRequestHandler<Entity>() {
			private final Logger log = LoggerFactory.getLogger(KeyRequestHandler.class);
			@Override
			public void requestKey(Entity key) {
				log.info("whoRequest " + key);
				serverConnection.getReceiver().entity().whoRequest(key);
			}
		});
	}

	@Override
	public void setValue(Entity key, ClientEntity value) {
		log.debug(String.format("EntityCache.setValue %s #%d", value, pendingJobs.size()));
		super.setValue(key, value);
	}

	@Override
	public void runWhenReady(Entity key, Job<ClientEntity> clientEntityJob) {

		if (log.isDebugEnabled() && !isKnown(key)) {
			StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
			StringBuilder builder = new StringBuilder(200);
			for (int i = 3; i < 6; i++) {
				builder.append(stackTrace[i]).append("\n");
			}
			log.debug(String.format("EntityCache.runWhenReady %s \n%s", key, builder.toString()));
		}
		super.runWhenReady(key, clientEntityJob);
	}
}
