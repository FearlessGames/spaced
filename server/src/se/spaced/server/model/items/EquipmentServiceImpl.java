package se.spaced.server.model.items;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.fearlessgames.common.util.TimeProvider;
import se.spaced.messages.protocol.s2c.S2CProtocol;
import se.spaced.server.model.ServerEntity;
import se.spaced.server.model.aura.AuraService;
import se.spaced.server.model.aura.ServerAura;
import se.spaced.server.model.entity.EntityService;
import se.spaced.server.model.entity.VisibilityService;
import se.spaced.server.net.broadcast.SmrtBroadcaster;
import se.spaced.server.persistence.dao.interfaces.EquipmentDao;
import se.spaced.server.persistence.util.transactions.AutoTransaction;
import se.spaced.shared.activecache.Job;
import se.spaced.shared.model.items.ContainerType;
import se.spaced.shared.model.items.EquipFailure;
import se.spaced.shared.model.items.UnequipFailure;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class EquipmentServiceImpl implements EquipmentService {
	private final Logger log = LoggerFactory.getLogger(getClass());
	private final EquipmentDao equipmentDao;
	private final InventoryService inventoryService;
	private final SmrtBroadcaster<S2CProtocol> broadcaster;
	private final VisibilityService visibilityService;
	private final AuraService auraService;
	private final TimeProvider timeProvider;
	private final EntityService entityService;

	public EquipmentServiceImpl(
			EquipmentDao equipmentDao,
			InventoryService inventoryService,
			SmrtBroadcaster<S2CProtocol> broadcaster,
			VisibilityService visibilityService,
			AuraService auraService,
			TimeProvider timeProvider,
			EntityService entityService) {
		this.equipmentDao = equipmentDao;
		this.inventoryService = inventoryService;
		this.broadcaster = broadcaster;
		this.visibilityService = visibilityService;
		this.auraService = auraService;
		this.timeProvider = timeProvider;
		this.entityService = entityService;
	}

	@Override
	@AutoTransaction
	public void equipItem(final ServerEntity entity, final ContainerType type, final ServerItem serverItem) {
		Inventory inventory = inventoryService.getInventory(entity, InventoryType.BAG);
		final ServerItemTemplate itemTemplate = (ServerItemTemplate) serverItem.getItemTemplate();

		if (inventory == null) {
			return;
		}
		if (!inventory.contains(serverItem)) {
			broadcaster.create().to(entity).send().equipment().failedToEquipItem(serverItem, EquipFailure.ITEM_NOT_FOUND);
			return;
		}
		if (!itemTemplate.goesInSlot(type)) {
			broadcaster.create().to(entity).send().equipment().failedToEquipItem(serverItem, EquipFailure.WRONG_SLOT);
			return;
		}
		EquippedItems equippedItems = equipmentDao.findByOwner(entity);
		final ServerItem oldItem = equippedItems.get(type);
		equippedItems.put(serverItem, type);
		inventoryService.removeIfExists(serverItem);
		if (oldItem != null) {
			removeAuras(entity, oldItem);

			try {
				inventoryService.add(inventory, oldItem);
			} catch (InventoryFullException e) {
				log.error("inventory was full after equip, race problem that the user got a new item while unequiping?", e);
			}

			broadcaster.create().to(entity).send().equipment().unequippedItem(oldItem, type);
		}

		broadcaster.create().to(entity).send().equipment().equippedItem(serverItem, type);

		visibilityService.invokeForNearby(entity, new Job<Collection<ServerEntity>>() {
			@Override
			public void run(Collection<ServerEntity> value) {
				if (oldItem != null) {
					broadcaster.create().to(value).exclude(entity).send().equipment().entityUnequippedItem(entity, type);
				}
				broadcaster.create().to(value).exclude(entity).send().equipment().entityEquippedItem(entity,
						itemTemplate,
						type);
			}
		});
		if (entityService.isLoggedIn(entity)) {
			addAuras(entity, itemTemplate);
		}
	}

	private void addAuras(ServerEntity entity, ServerItemTemplate template) {
		Set<ServerAura> auras = template.getEquipAuras();
		long now = timeProvider.now();
		for (ServerAura aura : auras) {
			auraService.apply(entity, entity, aura, now);
		}
	}

	@Override
	@AutoTransaction
	public void unequipItem(final ServerEntity entity, final ContainerType type) {

		Inventory inventory = inventoryService.getInventory(entity, InventoryType.BAG);
		if (inventory == null) {
			return;
		}
		EquippedItems items = equipmentDao.findByOwner(entity);
		ServerItem oldItem = items.get(type);

		if (oldItem == null) {
			log.debug("Tried to unequip from slot with no item.");
			return;
		}

		if (inventory.isFull()) {
			broadcaster.create().to(entity).send().equipment().failedToUnequipItem(oldItem, UnequipFailure.INVENTORY_FULL);
			return;
		}

		removeAuras(entity, oldItem);


		items.remove(type);

		try {
			inventoryService.add(inventory, oldItem);
		} catch (InventoryFullException e) {
			throw new RuntimeException("Inventory was full after unequip, might be race problem!", e);
		}

		broadcaster.create().to(entity).send().equipment().unequippedItem(oldItem, type);
		visibilityService.invokeForNearby(entity, new Job<Collection<ServerEntity>>() {
			@Override
			public void run(Collection<ServerEntity> value) {
				broadcaster.create().to(value).exclude(entity).send().equipment().entityUnequippedItem(entity, type);
			}
		});
	}

	private void removeAuras(ServerEntity entity, ServerItem oldItem) {
		Set<ServerAura> auras = oldItem.getTemplate().getEquipAuras();
		for (ServerAura aura : auras) {
			auraService.removeInstance(entity, aura);
		}
	}

	@Override
	@AutoTransaction
	public ServerItem getEquippedItem(ServerEntity entity, ContainerType type) {
		return equipmentDao.findByOwner(entity).get(type);
	}

	@Override
	@AutoTransaction
	public void applyAuras(ServerEntity entity) {
		EquippedItems equippedItems = getEquippedItems(entity);
		Map<ContainerType, ServerItem> items = equippedItems.getEquippedItems();
		for (Map.Entry<ContainerType, ServerItem> entry : items.entrySet()) {
			ServerItem item = entry.getValue();
			addAuras(entity, item.getTemplate());
		}
	}

	@Override
	@AutoTransaction
	public EquippedItems getEquippedItems(ServerEntity entity) {
		return equipmentDao.findByOwner(entity);
	}

	@Override
	@AutoTransaction
	public void createEquippedItemsForEntity(ServerEntity entity) {
		EquippedItems equippedItems = new EquippedItems(entity);
		equipmentDao.persist(equippedItems);
	}


}
