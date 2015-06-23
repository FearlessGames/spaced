package se.spaced.client.model.cooldown;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import se.spaced.client.net.smrt.ServerConnection;
import se.spaced.messages.protocol.Cooldown;
import se.spaced.shared.activecache.ActiveCacheImpl;
import se.spaced.shared.activecache.KeyRequestHandler;

@Singleton
public class ClientCooldownServiceImpl extends ActiveCacheImpl<Cooldown, ClientCooldown> implements ClientCooldownService {

	@Inject
	public ClientCooldownServiceImpl(final ServerConnection serverConnection) {
		super(new KeyRequestHandler<Cooldown>() {
			@Override
			public void requestKey(Cooldown key) {
				serverConnection.getReceiver().combat().requestCooldownData(key);
			}
		});
	}
}
