package se.spaced.client.net.messagelisteners;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.fearless.common.time.TimeProvider;
import se.fearless.common.uuid.UUID;
import se.spaced.client.ardor.ui.events.CombatGuiEvents;
import se.spaced.client.model.ClientEntity;
import se.spaced.client.model.ClientEntityProxy;
import se.spaced.client.model.PlaybackService;
import se.spaced.client.model.UserCharacter;
import se.spaced.client.model.control.ClientTeleporter;
import se.spaced.client.model.item.ItemTemplateJob;
import se.spaced.client.model.listener.ClientEntityListener;
import se.spaced.client.model.listener.EquipmentListener;
import se.spaced.messages.protocol.Entity;
import se.spaced.messages.protocol.ItemTemplate;
import se.spaced.messages.protocol.ItemTemplateData;
import se.spaced.messages.protocol.s2c.ServerEntityDataMessages;
import se.spaced.shared.activecache.ActiveCache;
import se.spaced.shared.activecache.Job;
import se.spaced.shared.events.EventHandler;
import se.spaced.shared.model.AnimationState;
import se.spaced.shared.model.PositionalData;
import se.spaced.shared.model.items.ContainerType;
import se.spaced.shared.model.stats.EntityStats;
import se.spaced.shared.network.protocol.codec.datatype.EntityData;
import se.spaced.shared.playback.MovementPoint;
import se.spaced.shared.util.ListenerDispatcher;

import java.util.Map;

@Singleton
public class ServerEntityDataMessagesImpl implements ServerEntityDataMessages {
	private final Logger log = LoggerFactory.getLogger(getClass());
	private final ListenerDispatcher<ClientEntityListener> entityDispatcher;
	private final ActiveCache<Entity, ClientEntity> entityCache;
	private final TimeProvider timeProvider;
	private final UserCharacter userCharacter;
	private final ClientTeleporter clientTeleporter;
	private final ActiveCache<ItemTemplate, ItemTemplateData> itemTemplateCache;
	private final ListenerDispatcher<EquipmentListener> equipmentDispatcher;
	private final EventHandler eventHandler;
	private final PlaybackService playbackService;

	@Inject
	public ServerEntityDataMessagesImpl(
			ListenerDispatcher<ClientEntityListener> entityDispatcher,
			ActiveCache<Entity, ClientEntity> entityCache, TimeProvider timeProvider,
			UserCharacter userCharacter,
			ClientTeleporter clientTeleporter,
			ActiveCache<ItemTemplate, ItemTemplateData> itemTemplateCache,
			ListenerDispatcher<EquipmentListener> equipmentDispatcher,
			EventHandler eventHandler,
			PlaybackService playbackService) {
		this.entityDispatcher = entityDispatcher;
		this.entityCache = entityCache;
		this.timeProvider = timeProvider;
		this.userCharacter = userCharacter;
		this.clientTeleporter = clientTeleporter;
		this.itemTemplateCache = itemTemplateCache;
		this.equipmentDispatcher = equipmentDispatcher;
		this.eventHandler = eventHandler;
		this.playbackService = playbackService;
	}

	@Override
	public void updateStats(final Entity entity, final EntityStats stats) {
		entityCache.runWhenReady(entity, new Job<ClientEntity>() {
			@Override
			public void run(ClientEntity value) {
				value.getBaseStats().update(stats);
				entityDispatcher.trigger().statsUpdated(value);
			}
		});
	}

	@Override
	public void entityAppeared(Entity entity, EntityData data, Map<ContainerType, ? extends ItemTemplate> items) {
		Preconditions.checkArgument(entity.getPk().equals(data.getId()));
		if (entityCache.isKnown(entity)) {
			log.warn(String.format("Got entityAppeared for %s when it's already known", entity));
			return;
		}
		final ClientEntity clientEntity = new ClientEntity(data, entityDispatcher);

		if (!data.getTarget().equals(UUID.ZERO)) {
			entityChangedTarget(clientEntity, new ClientEntityProxy(data.getTarget()));
		}

		MovementPoint<AnimationState> point = data.getMovementPoint(timeProvider.now());


		playbackService.add(clientEntity);

		entityCache.setValue(entity, clientEntity);

		for (final Map.Entry<ContainerType, ? extends ItemTemplate> entry : items.entrySet()) {
			final ItemTemplate itemTemplate = entry.getValue();
			itemTemplateCache.runWhenReady(itemTemplate, new ItemTemplateJob() {
				@Override
				public void run(ItemTemplateData value) {
					equipmentDispatcher.trigger().itemEquipped(clientEntity, value, entry.getKey());
				}
			});
		}
		clientEntity.activateMovementPlayer(point);
	}

	@Override
	public void entityDisappeared(Entity entity) {
		entityDespawned(entity);
	}

	@Override
	public void doRespawn(final PositionalData positionalData, final EntityStats entityStats) {
		clientTeleporter.setDestination(positionalData);
		ClientEntity player = userCharacter.getUserControlledEntity();
		player.getBaseStats().update(entityStats);
		player.setAlive(true);
	}

	@Override
	public void entityDespawned(Entity entity) {
		entityCache.delete(entity);
	}

	@Override
	public void entityChangedTarget(final Entity entity, final Entity target) {
		ClientEntity clientEntity = entityCache.getValue(entity);
		if (clientEntity == null) {
			return;
		}

		ClientEntity clientTarget = entityCache.getValue(target);
		if (clientTarget == null) {
			entityClearedTarget(clientEntity);
			return;
		}
		clientEntity.setTarget(clientTarget);
		eventHandler.fireAsynchEvent(CombatGuiEvents.UNIT_CHANGED_TARGET, clientEntity, clientTarget);
	}

	@Override
	public void entityClearedTarget(Entity entity) {
		ClientEntity clientEntity = entityCache.getValue(entity);
		if (clientEntity == null) {
			return;
		}
		if (clientEntity.getTarget() == null) {
			return;
		}
		clientEntity.setTarget(null);
		eventHandler.fireAsynchEvent(CombatGuiEvents.UNIT_CHANGED_TARGET, clientEntity, null);
	}


	@Override
	public void unknownEntityName(String name) {
		// TODO: display error message
	}

	@Override
	public void changedTarget(Entity newTarget) {
	}

	@Override
	public void clearedTarget() {
	}
}
