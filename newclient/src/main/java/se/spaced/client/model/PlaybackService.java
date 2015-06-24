package se.spaced.client.model;

import se.fearless.common.time.TimeProvider;
import se.spaced.messages.protocol.Entity;
import se.spaced.shared.activecache.ActiveCache;
import se.spaced.shared.activecache.CacheUpdateListener;
import se.spaced.shared.model.AnimationState;
import se.spaced.shared.playback.BufferedMovementPlayer;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.HashSet;
import java.util.Set;

@Singleton
public class PlaybackService implements CacheUpdateListener<Entity, ClientEntity> {
	private final Set<ClientEntity> clientEntities = new HashSet<ClientEntity>();
	private final TimeProvider timeProvider;

	@Inject
	public PlaybackService(ActiveCache<Entity, ClientEntity> entityCache, TimeProvider timeProvider) {
		this.timeProvider = timeProvider;
		entityCache.addListener(this);
	}

	public synchronized void update() {
		for (ClientEntity clientEntity : clientEntities) {
			BufferedMovementPlayer<AnimationState> player = clientEntity.getMovementPlayer();
			if (player != null) {
				player.step(timeProvider.now());
			}
		}
	}

	public synchronized void add(ClientEntity clientEntity) {
		clientEntities.add(clientEntity);
	}

	public synchronized void remove(ClientEntity clientEntity) {
		clientEntities.remove(clientEntity);
	}

	@Override
	public void updatedValue(Entity key, ClientEntity oldValue, ClientEntity value) {
	}

	@Override
	public void deletedValue(Entity key, ClientEntity oldValue) {
		remove(oldValue);
	}

	@Override
	public void addedValue(Entity key, ClientEntity value) {
	}
}
