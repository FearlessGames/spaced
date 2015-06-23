package se.spaced.server.persistence.dao.impl.hibernate;

import org.hibernate.Transaction;
import org.junit.Before;
import org.junit.Test;
import se.fearless.common.time.MockTimeProvider;
import se.fearless.common.uuid.UUID;
import se.fearless.common.uuid.UUIDFactory;
import se.fearless.common.uuid.UUIDFactoryImpl;
import se.mockachino.annotations.Mock;
import se.spaced.server.model.Mob;
import se.spaced.server.model.Player;
import se.spaced.server.model.currency.PersistedCurrency;
import se.spaced.server.model.items.*;
import se.spaced.server.model.player.PlayerMockFactory;
import se.spaced.server.model.spawn.MobTemplate;
import se.spaced.server.persistence.dao.interfaces.InventoryDao;
import se.spaced.server.persistence.dao.interfaces.ItemDao;
import se.spaced.server.persistence.dao.interfaces.PlayerDao;
import se.spaced.shared.model.items.ItemType;
import se.spaced.shared.util.random.RandomProvider;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static se.mockachino.Mockachino.setupMocks;
import static se.spaced.server.model.items.InventoryAssertUtils.assertItemCountInInventory;

public class InventoryDaoImplTest extends PersistentTestBase {

	private final MockTimeProvider timeProvider = new MockTimeProvider();
	private final UUIDFactory uuidFactory = new UUIDFactoryImpl(timeProvider, new SecureRandom());
	private InventoryDao inventoryDao;
	private ItemDao itemDao;
	private PlayerDao playerDao;
	private PlayerMockFactory playerMockFactory;
	@Mock
	private RandomProvider randomProvider;

	@Before
	public void setup() {
		setupMocks(this);
		Transaction tx = sessionFactory.getCurrentSession().beginTransaction();
		sessionFactory.getCurrentSession().save(PersistedCurrency.NONE);
		tx.commit();

		inventoryDao = new InventoryDaoImpl(sessionFactory);
		playerDao = new PlayerDaoImpl(sessionFactory);
		itemDao = new ItemDaoImpl(sessionFactory);
		playerMockFactory = new PlayerMockFactory.Builder(timeProvider, uuidFactory).build();
	}

	@Test
	public void testFindByPlayerWhenNoInventory() throws Exception {
		Transaction transaction = transactionManager.beginTransaction();

		Player player = playerMockFactory.createPlayer("Player");
		player.setPk(null);
		playerDao.persist(player);
		transaction.commit();

		transaction = transactionManager.beginTransaction();
		List<PersistedInventory> inventories = inventoryDao.findByOwner(player);
		assertEquals(inventories.size(), 0);
		transaction.commit();
	}

	@Test
	public void testFindByPlayer() throws Exception {
		Transaction transaction = transactionManager.beginTransaction();
		Player player = playerMockFactory.createPlayer("PlayerName");
		player.setPk(null);
		playerDao.persist(player);
		transaction.commit();
		transaction = transactionManager.beginTransaction();
		player = playerDao.findByName("PlayerName");
		PersistedInventory inventory = new PersistedInventory(player, 100, InventoryType.BAG);
		inventoryDao.persist(inventory);
		transaction.commit();

		transaction = transactionManager.beginTransaction();
		Inventory inventory2 = inventoryDao.findByOwner(player).get(0);
		assertEquals(inventory, inventory2);
		transaction.commit();
	}

	@Test
	public void testFindByMob() throws Exception {

		MobTemplate mobTemplate = new MobTemplate.Builder(uuidFactory.randomUUID(), "Foo").build();
		Mob mob = mobTemplate.createMob(timeProvider, uuidFactory.randomUUID(), randomProvider);

		Transaction transaction = transactionManager.beginTransaction();
		List<PersistedInventory> inventories = inventoryDao.findByOwner(mob);
		assertEquals(inventories.size(), 0);

		transaction.commit();
	}

	@Test
	public void testRemovalOfItemInInventory() throws Exception {
		Transaction transaction = transactionManager.beginTransaction();
		Player player = playerMockFactory.createPlayer("Player");
		player.setPk(null);
		playerDao.persist(player);

		ServerItemTemplate itemTemplate = new ServerItemTemplate.Builder(uuidFactory.combUUID(),
				"testItemTemplate",
				ItemType.GLOVES).build();
		transactionManager.getCurrentSession().persist(itemTemplate);

		transaction.commit();


		final int NR_OF_ITEMS = 4;

		List<ServerItem> items = new ArrayList<ServerItem>();
		Transaction tx = transactionManager.beginTransaction();
		for (int i = 0; i < NR_OF_ITEMS; i++) {

			ServerItem serverItem = new ServerItem(itemTemplate);
			serverItem.setOwner(player);
			itemDao.persist(serverItem);
			items.add(serverItem);
		}
		tx.commit();

		UUID itemId = items.get(0).getPk();


		tx = transactionManager.beginTransaction();
		PersistedInventory inventory = new PersistedInventory(player, 100, InventoryType.BAG);
		inventoryDao.persist(inventory);


		for (ServerItem item : items) {
			inventory.addItem(item);
		}

		inventoryDao.persist(inventory);
		tx.commit();


		tx = transactionManager.beginTransaction();
		for (ServerItem item : items) {
			assertNotNull(inventoryDao.findInventory(item));
		}
		tx.commit();


		tx = transactionManager.beginTransaction();
		ServerItem serverItem = itemDao.findByPk(itemId);
		inventory = inventoryDao.findInventory(serverItem);
		inventory.removeItem(serverItem);
		inventoryDao.persist(inventory);
		tx.commit();


		tx = transactionManager.beginTransaction();
		inventory = inventoryDao.findByOwner(player).get(0);
		assertItemCountInInventory(NR_OF_ITEMS - 1, inventory);

		assertNull(inventoryDao.findInventory(serverItem));

		tx.commit();


	}
}
