package se.spaced.server.loot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.spaced.server.model.ServerEntity;
import se.spaced.server.model.currency.MoneyService;
import se.spaced.server.model.currency.PersistedMoney;
import se.spaced.server.model.entity.EntityService;
import se.spaced.server.model.items.Inventory;
import se.spaced.server.model.items.InventoryFullException;
import se.spaced.server.model.items.InventoryService;
import se.spaced.server.model.items.InventoryType;
import se.spaced.server.model.items.ItemService;
import se.spaced.server.model.items.ServerItem;
import se.spaced.server.model.items.ServerItemTemplate;
import se.spaced.server.persistence.dao.impl.hibernate.TransactionManager;
import se.spaced.server.persistence.util.transactions.AutoTransaction;

import java.util.Collection;

public class LootAwardServiceImpl implements LootAwardService {
	private final Logger log = LoggerFactory.getLogger(getClass());
	private final InventoryService inventoryService;
	private final TransactionManager transactionManager;
	private final ItemService itemService;
	private final MoneyService moneyService;
	private final EntityService entityService;

	public LootAwardServiceImpl(
			InventoryService inventoryService,
			TransactionManager transactionManager,
			ItemService itemService,
			MoneyService moneyService,
			EntityService entityService) {
		this.inventoryService = inventoryService;
		this.transactionManager = transactionManager;
		this.itemService = itemService;
		this.moneyService = moneyService;
		this.entityService = entityService;
	}

	boolean awardItem(ServerEntity entity, ServerItem item) {
		ServerItemTemplate template = item.getTemplate();
		transactionManager.rebuildFromDataBase(template);

		Inventory inventory = inventoryService.getInventory(entity, InventoryType.BAG);
		if (inventory != null) {
			if (inventory.isFull()) {
				return false;
			}

			itemService.persistItem(item, entity);

			try {
				inventoryService.add(inventory, item);
			} catch (InventoryFullException e) {
				item.setOwner(null);
				return false;
			}

			return inventory.contains(item);
		} else {
			log.info("Can't award loot without an inventory. {}", entity);
			return false;
		}
	}

	@Override
	@AutoTransaction
	public void awardLoot(ServerEntity receiver, Collection<Loot> loots) {
		for (Loot loot : loots) {
			ServerItemTemplate template = loot.getItemTemplate();
			if (template != null) {
				ServerItem item = template.create();
				boolean itemAwarded = awardItem(receiver, item);
				if (itemAwarded) {
					entityService.getSmrtReceiver(receiver).loot().receivedLoot(item);
				}
			}

			PersistedMoney money = loot.getMoney();
			if (!money.isZero()) {
				moneyService.awardMoney(receiver, money);
			}
		}
	}
}
