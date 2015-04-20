package se.spaced.client.net.messagelisteners;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Iterables;
import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.spaced.client.ardor.ui.events.ItemEvents;
import se.spaced.client.model.ClientEntity;
import se.spaced.client.model.InventoryProvider;
import se.spaced.client.model.item.ClientItem;
import se.spaced.client.model.item.ItemJob;
import se.spaced.client.model.item.ItemTemplateJob;
import se.spaced.client.model.listener.EquipmentListener;
import se.spaced.client.model.player.PlayerEntityProvider;
import se.spaced.client.model.player.PlayerEquipment;
import se.spaced.messages.protocol.Entity;
import se.spaced.messages.protocol.ItemTemplate;
import se.spaced.messages.protocol.ItemTemplateData;
import se.spaced.messages.protocol.SpacedItem;
import se.spaced.messages.protocol.s2c.ServerEquipmentMessages;
import se.spaced.shared.activecache.ActiveCache;
import se.spaced.shared.activecache.Job;
import se.spaced.shared.events.EventHandler;
import se.spaced.shared.model.items.ContainerType;
import se.spaced.shared.model.items.EquipFailure;
import se.spaced.shared.model.items.UnequipFailure;
import se.spaced.shared.util.ListenerDispatcher;

import java.util.Map;

public class ServerEquipmentMessagesImpl implements ServerEquipmentMessages {
	private final Logger log = LoggerFactory.getLogger(getClass());

	private final EventHandler eventHandler;
	private final InventoryProvider inventory;
	private final PlayerEntityProvider player;
	private final ListenerDispatcher<EquipmentListener> equipmentDispatcher;
	private final PlayerEquipment playerEquipment;

	private final ActiveCache<SpacedItem, ClientItem> itemCache;
	private final ActiveCache<ItemTemplate, ItemTemplateData> itemTemplateCache;
	private final ActiveCache<Entity, ClientEntity> entityCache;

	@Inject
	public ServerEquipmentMessagesImpl(
			EventHandler eventHandler,
			InventoryProvider inventory,
			PlayerEntityProvider player,
			ActiveCache<SpacedItem, ClientItem> itemCache,
			ActiveCache<ItemTemplate, ItemTemplateData> itemTemplateCache,
			ActiveCache<Entity, ClientEntity> entityCache,
			ListenerDispatcher<EquipmentListener> equipmentDispatcher,
			PlayerEquipment playerEquipment) {
		this.eventHandler = eventHandler;
		this.inventory = inventory;
		this.player = player;
		this.itemCache = itemCache;
		this.itemTemplateCache = itemTemplateCache;
		this.entityCache = entityCache;
		this.equipmentDispatcher = equipmentDispatcher;
		this.playerEquipment = playerEquipment;
	}

	@Override
	public void equippedItem(final SpacedItem spacedItem, final ContainerType container) {
		log.debug("equippedItem {} on {}", spacedItem, container);
		itemCache.runWhenReady(spacedItem, new ItemJob() {
			@Override
			public void run(final ClientItem item) {
				log.debug("equippedItem, after item template is looked up");

				ClientEntity player = ServerEquipmentMessagesImpl.this.player.get();

				playerEquipment.equip(item, container);
				ImmutableMultimap<Integer, ? extends SpacedItem> items = inventory.getPlayerInventory().getItemMap();
				Optional<? extends Map.Entry<Integer, ? extends SpacedItem>> optional = Iterables.tryFind(items.entries(),
						new Predicate<Map.Entry<Integer, ? extends SpacedItem>>() {
							@Override
							public boolean apply(Map.Entry<Integer, ? extends SpacedItem> entry) {
								return entry.getValue().equals(item);
							}
						});
				if (optional.isPresent()) {
					Integer position = optional.get().getKey();
					inventory.getPlayerInventory().removeItem(item, position);
				}
				eventHandler.fireAsynchEvent(ItemEvents.EQUIPPED_ITEM, player, item, container);
			}
		});
	}

	@Override
	public void unequippedItem(final SpacedItem item, final ContainerType container) {
		itemCache.runWhenReady(item, new ItemJob() {
			@Override
			public void run(ClientItem clientItem) {
				ClientEntity player = ServerEquipmentMessagesImpl.this.player.get();
				playerEquipment.unequip(container, clientItem);
				eventHandler.fireAsynchEvent(ItemEvents.UNEQUIPPED_ITEM, player, clientItem, container);
			}
		});
	}

	@Override
	public void entityEquippedItem(final Entity entity, ItemTemplate itemTemplate, final ContainerType container) {
		itemTemplateCache.runWhenReady(itemTemplate, new ItemTemplateJob() {
			@Override
			public void run(final ItemTemplateData data) {
				entityCache.runWhenReady(entity, new Job<ClientEntity>() {
					@Override
					public void run(ClientEntity value) {
						equipmentDispatcher.trigger().itemEquipped(value, data, container);
					}
				});
			}
		});

	}

	@Override
	public void entityUnequippedItem(Entity entity, final ContainerType type) {
		entityCache.runWhenReady(entity, new Job<ClientEntity>() {
			@Override
			public void run(ClientEntity value) {
				equipmentDispatcher.trigger().itemUnequipped(value, type);
			}
		});
	}

	@Override
	public void failedToUnequipItem(final SpacedItem item, final UnequipFailure reason) {
		itemCache.runWhenReady(item, new ItemJob() {
			@Override
			public void run(ClientItem data) {
				ClientEntity player = ServerEquipmentMessagesImpl.this.player.get();
				eventHandler.fireAsynchEvent(ItemEvents.UNEQUIP_ITEM_FAILED, player, data, reason);
			}
		});
	}

	@Override
	public void failedToEquipItem(final SpacedItem item, final EquipFailure reason) {
		itemCache.runWhenReady(item, new ItemJob() {
			@Override
			public void run(ClientItem data) {
				ClientEntity player = ServerEquipmentMessagesImpl.this.player.get();
				eventHandler.fireAsynchEvent(ItemEvents.EQUIP_ITEM_FAILED, player, data, reason);
			}
		});
	}
}
