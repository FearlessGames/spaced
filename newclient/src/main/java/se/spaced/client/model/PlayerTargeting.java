package se.spaced.client.model;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import se.fearlessgames.common.util.uuid.UUID;
import se.spaced.client.model.listener.ClientEntityListener;
import se.spaced.client.model.player.PlayerEntityProvider;
import se.spaced.client.model.player.PlayerTargetingListener;
import se.spaced.client.model.player.TargetInfo;
import se.spaced.client.net.smrt.ServerConnection;
import se.spaced.messages.protocol.Entity;
import se.spaced.shared.activecache.ActiveCache;
import se.spaced.shared.activecache.CacheUpdateListener;
import se.spaced.shared.model.AnimationState;
import se.spaced.shared.util.ListenerDispatcher;

/**
 * Models the targeting and hovering features of the player
 */
@Singleton
public class PlayerTargeting implements ClientEntityListener, CacheUpdateListener<Entity, ClientEntity> {
	private final PlayerEntityProvider playerProvider;
	private final ListenerDispatcher<PlayerTargetingListener> dispatcher;
	private final RelationResolver relationResolver;
	private final ServerConnection serverConnection;
	private final ActiveCache<Entity, ClientEntity> entityCache;
	private ClientEntity hover;

	@Inject
	public PlayerTargeting(
			PlayerEntityProvider pp,
			ListenerDispatcher<PlayerTargetingListener> d,
			RelationResolver rr,
			ServerConnection sc,
			ActiveCache<Entity, ClientEntity> entityCache) {
		playerProvider = pp;
		dispatcher = d;
		relationResolver = rr;
		serverConnection = sc;
		this.entityCache = entityCache;
		entityCache.addListener(this);
	}

	public void setTarget(final UUID entityUuid) {
		ClientEntity entity = getEntity(entityUuid);
		setTarget(entity);
	}

	public void setTarget(final ClientEntity entity) {
		if (entity != null && entity.equals(playerProvider.get().getTarget())) {
			return;
		}

		serverConnection.getReceiver().entity().setTarget(entity);
		playerProvider.get().setTarget(entity);
		dispatcher.trigger().newTarget(newTargetInfo(entity));
	}

	public void clearTarget() {
		serverConnection.getReceiver().entity().clearTarget();
		playerProvider.get().setTarget(null);
		dispatcher.trigger().targetCleared();
	}

	public ClientEntity getTarget() {
		return playerProvider.get().getTarget();
	}

	public void setHover(final UUID entityUuid) {
		ClientEntity entity = getEntity(entityUuid);
		if (entity != null && entity.equals(hover)) {
			return;
		}

		hover = entity;
		dispatcher.trigger().newHover(newTargetInfo(hover));
	}

	public void clearHover() {
		hover = null;
		dispatcher.trigger().hoverCleared();
	}

	// EntityDirectoryListener
	public Entity getHover() {
		return hover;
	}

	@Override
	public void appearanceDataUpdated(ClientEntity entity) {
		// Ignored, implemented elsewhere
	}

	@Override
	public void statsUpdated(ClientEntity spacedEntity) {
		// Ignored, implemented elsewhere
	}

	@Override
	public void died(final ClientEntity entity) {
		if (entity.equals(playerProvider.get().getTarget())) {
			dispatcher.trigger().newTarget(newTargetInfo(entity));
		}
	}

	@Override
	public void respawned(final ClientEntity clientEntity) {
		// Ignored, implemented elsewhere
	}

	@Override
	public void animationStateChanged(ClientEntity spacedEntity, AnimationState animationState) {
	}

	@Override
	public void positionalDataChanged(ClientEntity clientEntity) {
	}

	private TargetInfo newTargetInfo(final ClientEntity entity) {
		final Relation relation = relationResolver.resolveRelation(playerProvider.get(), entity);
		return new TargetInfo(entity.getPk(), relation, false, entity.isAlive(), entity);
	}

	private ClientEntity getEntity(UUID playerId) {
		return entityCache.getValue(new ClientEntityProxy(playerId));
	}

	@Override
	public void updatedValue(Entity key, ClientEntity oldValue, ClientEntity value) {
	}

	@Override
	public void deletedValue(Entity key, ClientEntity oldValue) {
		final ClientEntity currentTarget = getTarget();
		if (currentTarget != null && currentTarget.equals(oldValue)) {
			playerProvider.get().setTarget(null);
			dispatcher.trigger().targetCleared();
		}
	}

	@Override
	public void addedValue(Entity key, ClientEntity value) {
	}
}
