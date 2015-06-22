package se.spaced.server.model.items;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.fearlessgames.common.util.TimeProvider;
import se.fearlessgames.common.util.uuid.UUID;
import se.fearlessgames.common.util.uuid.UUIDFactory;
import se.spaced.messages.protocol.s2c.S2CProtocol;
import se.spaced.server.model.ServerEntity;
import se.spaced.server.model.action.ActionScheduler;
import se.spaced.server.model.combat.SpellCombatService;
import se.spaced.server.net.broadcast.SmrtBroadcaster;
import se.spaced.server.persistence.dao.interfaces.ItemDao;
import se.spaced.server.persistence.dao.interfaces.ItemTemplateDao;
import se.spaced.server.persistence.util.transactions.AutoTransaction;
import se.spaced.server.persistence.util.transactions.RequireTransaction;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ItemServiceImpl implements ItemService {
	private final Logger log = LoggerFactory.getLogger(getClass());

	private final ItemTemplateDao itemTemplateDao;
	private final ItemDao itemDao;
	private final SpellCombatService spellCombatService;
	private final TimeProvider timeProvider;
	private final InventoryService inventoryService;
	private final UUIDFactory uuidFactory;

	private final SmrtBroadcaster<S2CProtocol> broadcaster;
	private final ActionScheduler scheduler;
	private final Map<UUID, ServerItem> virtualItems = new ConcurrentHashMap<UUID, ServerItem>();

	public ItemServiceImpl(
			ItemTemplateDao itemTemplateDao,
			ItemDao itemDao,
			SpellCombatService spellCombatService,
			TimeProvider timeProvider,
			SmrtBroadcaster<S2CProtocol> broadcaster,
			ActionScheduler scheduler, InventoryService inventoryService, UUIDFactory uuidFactory) {
		this.itemTemplateDao = itemTemplateDao;
		this.itemDao = itemDao;
		this.spellCombatService = spellCombatService;
		this.timeProvider = timeProvider;
		this.broadcaster = broadcaster;
		this.scheduler = scheduler;
		this.inventoryService = inventoryService;
		this.uuidFactory = uuidFactory;
	}

	@Override
	@AutoTransaction
	public void persistItem(ServerItem item, ServerEntity owner) {

		if (item.getOwner() == null) {
			item.setOwner(owner);
		}

		if (!owner.equals(item.getOwner())) {
			throw new RuntimeException(String.format(
					"Trying to persist an item (%s) with %s as owner but that item already has an owner %s",
					item.toString(),
					item.getOwner().toString(),
					owner.toString()));
		}
		if (item.getPk() != null) {
			if (virtualItems.remove(item.getPk()) != null) {
				itemDao.persistVirtualItem(item);
			} else {
				itemDao.persist(item);
			}
		} else {
			itemDao.persist(item);
		}
	}

	@Override
	@AutoTransaction
	public Collection<ServerItemTemplate> getAllTemplates() {
		return itemTemplateDao.findAll();
	}


	@Override
	@AutoTransaction
	public ServerItemTemplate getTemplateByName(String name) {
		return itemTemplateDao.findByName(name);
	}

	@Override
	@AutoTransaction
	public void addTemplate(ServerItemTemplate itemTemplate) {
		itemTemplateDao.persist(itemTemplate);
	}

	@Override
	@AutoTransaction
	public ServerItem getItemByPk(UUID pk) {
		return virtualItems.containsKey(pk) ? virtualItems.get(pk) : itemDao.findByPk(pk);
	}

	@Override
	public void useItem(ServerEntity entity, ServerEntity target, final ServerItem item) {
		scheduler.add(new UseItemAction(this, item, spellCombatService, entity, target, timeProvider.now()));
	}

	@Override
	@AutoTransaction
	public void deleteItem(ServerItem serverItem) {
		if (serverItem == null) {
			log.warn("Trying to delete a null server item");
			return;
		}
		if (virtualItems.containsKey(serverItem.getPk())) {
			virtualItems.remove(serverItem.getPk());
		} else {
			ServerItem item = itemDao.findByPk(serverItem.getPk());
			inventoryService.removeIfExists(item);
			itemDao.delete(item);
		}
	}

	@Override
	public ServerItemTemplate getTemplateByPk(UUID pk) {
		return itemTemplateDao.findByPk(pk);
	}

	@Override
	@RequireTransaction
	public ExchangeResult transferItem(ServerEntity from, ServerEntity to, ServerItem item) {
		if (virtualItems.containsKey(item.getPk())) {
			return ExchangeResult.VIRTUAL_ITEM;
		}

		PersistedInventory fromInventory = inventoryService.getInventory(from, InventoryType.BAG);
		PersistedInventory toInventory = inventoryService.getInventory(to, InventoryType.BAG);

		item = fromInventory.getItem(item.getPk());

		if (item == null) {
			return ExchangeResult.WRONG_OWNER;
		}

		if (!toInventory.acceptsItem(item)) {
			return ExchangeResult.NOT_ENOUGH_SPACE;
		}

		inventoryService.removeIfExists(item);
		item.setOwner(to);
		try {
			inventoryService.add(toInventory, item);
		} catch (InventoryFullException e) {
			throw new RuntimeException("Inventory was full even after it was checked, probebly race problem!", e);
		}

		return ExchangeResult.SUCCESS;
	}

	@Override
	@RequireTransaction
	public ExchangeResult exchangeItems(ServerEntity part1, ServerItem item1, ServerEntity part2, ServerItem item2) {
		if (virtualItems.containsKey(item1.getPk()) || virtualItems.containsKey(item2.getPk())) {
			return ExchangeResult.VIRTUAL_ITEM;
		}

		PersistedInventory inventory1 = inventoryService.getInventory(part1, InventoryType.BAG);
		PersistedInventory inventory2 = inventoryService.getInventory(part2, InventoryType.BAG);

		item1 = inventory1.getItem(item1.getPk());
		item2 = inventory2.getItem(item2.getPk());

		if (item1 == null || item2 == null) {
			return ExchangeResult.WRONG_OWNER;
		}

		inventoryService.removeIfExists(item1);
		inventoryService.removeIfExists(item2);

		item1.setOwner(part2);
		item2.setOwner(part1);
		try {
			inventoryService.add(inventory1, item2);
			inventoryService.add(inventory2, item1);
		} catch (InventoryFullException e) {
			log.error("Inventory was full at exchange, probebly race probelem!", e);
			return ExchangeResult.NOT_ENOUGH_SPACE;
		}

		return ExchangeResult.SUCCESS;
	}

	@Override
	@AutoTransaction
	public boolean isOwner(ServerEntity entity, ServerItem serverItem) {
		return itemDao.isOwner(entity, serverItem);
	}

	@Override
	public UUID addVirtualItem(ServerItem serverItem) {
		UUID uuid = uuidFactory.randomUUID();
		serverItem.setPk(uuid);
		virtualItems.put(uuid, serverItem);
		return uuid;
	}

	@Override
	public void removeVirtualItem(UUID uuid) {
		virtualItems.remove(uuid);
	}

	@Override
	public boolean isVirtualItem(UUID pk) {
		return virtualItems.containsKey(pk);
	}
}
