package se.spaced.server.model.items;

import org.hibernate.Transaction;
import org.junit.Before;
import org.junit.Test;
import se.fearless.common.mock.MockUtil;
import se.spaced.messages.protocol.s2c.S2CProtocol;
import se.spaced.server.model.PersistedCreatureType;
import se.spaced.server.model.PersistedFaction;
import se.spaced.server.model.Player;
import se.spaced.server.model.PlayerType;
import se.spaced.server.model.currency.PersistedCurrency;
import se.spaced.server.net.broadcast.SmrtBroadcaster;
import se.spaced.server.net.broadcast.SmrtBroadcasterImpl;
import se.spaced.server.persistence.dao.impl.hibernate.PersistentTestBase;
import se.spaced.server.player.PlayerService;
import se.spaced.server.player.PlayerServiceImpl;
import se.spaced.shared.model.Gender;
import se.spaced.shared.model.items.ItemType;

import static org.junit.Assert.*;
import static se.spaced.server.model.items.InventoryAssertUtils.assertItemCountInInventory;
import static se.spaced.server.model.items.InventoryAssertUtils.firstItem;

public class InventoryServiceImplTest extends PersistentTestBase {

	private SmrtBroadcaster<S2CProtocol> broadcaster;
	private InventoryServiceImpl inventoryService;
	private ServerItemTemplate glovesTemplate;
	private Player adam;
	private ServerItemTemplate mumsTemplate;
	private ServerItemTemplate slurmsTemplate;

	@Before
	public void setUp() throws Exception {
		Transaction tx = sessionFactory.getCurrentSession().beginTransaction();
		sessionFactory.getCurrentSession().save(PersistedCurrency.NONE);
		tx.commit();

		broadcaster = MockUtil.deepMock(SmrtBroadcasterImpl.class);
		inventoryService = new InventoryServiceImpl(daoFactory.getInventoryDao(), broadcaster);
		glovesTemplate = new ServerItemTemplate.Builder(uuidFactory.randomUUID(), "Gloves", ItemType.GLOVES).build();
		mumsTemplate = new ServerItemTemplate.Builder(uuidFactory.randomUUID(), "Mums", ItemType.CONSUMABLE).stackable(3).build();
		slurmsTemplate = new ServerItemTemplate.Builder(uuidFactory.randomUUID(), "Slurms", ItemType.CONSUMABLE).stackable(10).build();

		daoFactory.getItemTemplateDao().persist(glovesTemplate);
		daoFactory.getItemTemplateDao().persist(mumsTemplate);
		daoFactory.getItemTemplateDao().persist(slurmsTemplate);

		PlayerService playerService = transactionProxyWrapper.wrap(new PlayerServiceImpl(timeProvider,
				daoFactory.getPlayerDao(), transactionManager));
		Transaction transaction = transactionManager.beginTransaction();
		PersistedCreatureType creatureType = daoFactory.getCreatureTypeDao().findByName("humanoid");
		PersistedFaction faction = daoFactory.getFactionDao().findByName("players");
		transaction.commit();
		adam = playerService.createPlayerCharacter("Adam", Gender.NONE, creatureType, faction, PlayerType.REGULAR);
		adam = playerService.getPlayer("Adam");
	}

	@Test
	public void create() throws Exception {
		inventoryService.createInventory(adam, 10, InventoryType.BAG);

		Inventory inventory = inventoryService.getInventory(adam, InventoryType.BAG);
		assertEquals(adam, inventory.getOwner());
		assertEquals(10, inventory.getNrOfSlots());
		assertEquals(0, inventory.getItemMap().size());
		assertItemCountInInventory(0, inventory);

	}

	@Test(expected = RuntimeException.class)
	public void createWhenInventoryOfSameTypeAlreadyExists() throws Exception {
		inventoryService.createInventory(adam, 10, InventoryType.BAG);
		inventoryService.createInventory(adam, 10, InventoryType.BAG);
	}


	@Test
	public void addToPosition() throws Exception {
		Inventory inventory = inventoryService.createInventory(adam, 10, InventoryType.BAG);

		ServerItem item = createAndPersistItem(glovesTemplate);

		inventoryService.add(inventory, item, 2);

		assertTrue(inventory.contains(item));

		assertEquals(item, firstItem(inventory, 2));
	}


	@Test(expected = InventoryFullException.class)
	public void addToPositionWhenInventoryIsFull() throws InventoryFullException {
		Inventory inventory = inventoryService.createInventory(adam, 0, InventoryType.BAG);

		ServerItem item = createAndPersistItem(glovesTemplate);

		inventoryService.add(inventory, item);


	}

	private ServerItem createAndPersistItem(ServerItemTemplate itemTemplate) {
		ServerItem item = itemTemplate.create();
		item.setOwner(adam);
		daoFactory.getItemDao().persist(item);
		return item;
	}

	@Test(expected = InventoryOutOfBoundsException.class)
	public void addToPositionOutsideRange1() throws Exception {
		Inventory inventory = inventoryService.createInventory(adam, 10, InventoryType.BAG);

		ServerItem item = glovesTemplate.create();

		inventoryService.add(inventory, item, -1);
	}

	@Test(expected = InventoryOutOfBoundsException.class)
	public void addToPositionOutsideRange2() throws Exception {
		Inventory inventory = inventoryService.createInventory(adam, 10, InventoryType.BAG);

		ServerItem item = glovesTemplate.create();

		inventoryService.add(inventory, item, 11);
	}

	@Test(expected = InventoryOutOfBoundsException.class)
	public void addToPositionOutsideRange3() throws Exception {
		Inventory inventory = inventoryService.createInventory(adam, 10, InventoryType.BAG);

		ServerItem item = glovesTemplate.create();

		inventoryService.add(inventory, item, 0);
	}

	@Test
	public void testRemoveExisting() throws InventoryOutOfBoundsException, InventoryFullException {
		Inventory inventory = inventoryService.createInventory(adam, 10, InventoryType.BAG);

		ServerItem item = createAndPersistItem(glovesTemplate);
		inventoryService.add(inventory, item, 2);
		assertEquals(item, firstItem(inventory, 2));


		inventoryService.removeIfExists(item);

		Inventory removed = inventoryService.getInventory(adam, InventoryType.BAG);
		assertItemCountInInventory(0, removed);
	}


	@Test(expected = RuntimeException.class)
	public void testAddAlreadyAddedItemToOtherInventory() throws InventoryOutOfBoundsException, InventoryFullException {
		Inventory bag = inventoryService.createInventory(adam, 10, InventoryType.BAG);
		Inventory bank = inventoryService.createInventory(adam, 10, InventoryType.BANK);

		ServerItem item = createAndPersistItem(glovesTemplate);
		inventoryService.add(bag, item, 2);
		assertEquals(item, firstItem(bag, 2));


		inventoryService.add(bank, item, 2);
	}

	@Test
	public void moveItems() throws InventoryOutOfBoundsException, InventoryFullException {
		Inventory bag = inventoryService.createInventory(adam, 10, InventoryType.BAG);
		ServerItem item = createAndPersistItem(glovesTemplate);

		inventoryService.add(bag, item, 2);
		assertEquals(item, firstItem(bag, 2));


		inventoryService.move(bag, 2, bag, 4);

		assertEquals(item, firstItem(bag, 4));

		assertTrue(stackIsEmpty(bag, 2));

	}

	private boolean stackIsEmpty(Inventory inventory, int pos) {
		return inventory.getItemMap().get(pos).isEmpty();
	}

	@Test
	public void moveBetweanInventories() throws InventoryOutOfBoundsException, InventoryFullException {
		Inventory bag = inventoryService.createInventory(adam, 10, InventoryType.BAG);
		Inventory bank = inventoryService.createInventory(adam, 10, InventoryType.BANK);

		ServerItem item = createAndPersistItem(glovesTemplate);
		inventoryService.add(bag, item, 2);

		bag = inventoryService.getInventory(adam, InventoryType.BAG);

		assertEquals(item, firstItem(bag, 2));


		inventoryService.move(bag, 2, bank, 4);

		bag = inventoryService.getInventory(adam, InventoryType.BAG);
		bank = inventoryService.getInventory(adam, InventoryType.BANK);


		assertTrue(stackIsEmpty(bag, 2));

		assertEquals(item, firstItem(bank, 4));
	}

	@Test
	public void moveACompleteStackBetweenPositions() throws Exception {
		Inventory inventory = inventoryService.createInventory(adam, 3, InventoryType.BAG);
		ServerItem item1 = createAndPersistItem(mumsTemplate);
		ServerItem item2 = createAndPersistItem(mumsTemplate);
		inventoryService.add(inventory, item1, 2);
		inventoryService.add(inventory, item2, 2);

		inventory = inventoryService.getInventory(adam, InventoryType.BAG);

		assertItemCountInInventory(2, inventory);
		assertEquals(2, inventory.getItemMap().get(2).size());
		assertEquals(0, inventory.getItemMap().get(1).size());

		inventoryService.move(inventory, 2, inventory, 1);

		inventory = inventoryService.getInventory(adam, InventoryType.BAG);

		assertItemCountInInventory(2, inventory);
		assertEquals(2, inventory.getItemMap().get(1).size());
		assertEquals(0, inventory.getItemMap().get(2).size());

	}

	@Test
	public void moveACompleteStackBetweenInventories() throws Exception {
		Inventory bankInventory = inventoryService.createInventory(adam, 3, InventoryType.BANK);
		Inventory bagInventory = inventoryService.createInventory(adam, 3, InventoryType.BAG);

		ServerItem item1 = createAndPersistItem(mumsTemplate);
		ServerItem item2 = createAndPersistItem(mumsTemplate);
		inventoryService.add(bagInventory, item1, 2);
		inventoryService.add(bagInventory, item2, 2);

		bagInventory = inventoryService.getInventory(adam, InventoryType.BAG);

		assertItemCountInInventory(2, bagInventory);
		assertEquals(2, bagInventory.getItemMap().get(2).size());
		assertEquals(0, bagInventory.getItemMap().get(1).size());

		inventoryService.move(bagInventory, 2, bankInventory, 1);

		bagInventory = inventoryService.getInventory(adam, InventoryType.BAG);
		bankInventory = inventoryService.getInventory(adam, InventoryType.BANK);

		assertItemCountInInventory(0, bagInventory);
		assertItemCountInInventory(2, bankInventory);
		assertEquals(2, bankInventory.getItemMap().get(1).size());
		assertEquals(0, bagInventory.getItemMap().get(2).size());

	}

	@Test
	public void addStackableItemToStackWithNoRoomInStackButRoomInInventory() throws Exception {
		Inventory inventory = inventoryService.createInventory(adam, 4, InventoryType.BAG);

		inventoryService.add(inventory, createAndPersistItem(glovesTemplate), 1);

		for (int i = 0; i < 3; i++) {
			inventoryService.add(inventory, createAndPersistItem(mumsTemplate), 3);
		}


		inventory = inventoryService.getInventory(adam, InventoryType.BAG);
		assertItemCountInInventory(4, inventory);
		assertEquals(1, inventory.getItemMap().get(1).size());
		assertEquals(3, inventory.getItemMap().get(3).size());


		inventoryService.add(inventory, createAndPersistItem(mumsTemplate), 3);

		inventory = inventoryService.getInventory(adam, InventoryType.BAG);
		assertItemCountInInventory(5, inventory);
		assertEquals(3, inventory.getItemMap().get(3).size());
		assertEquals(1, inventory.getItemMap().get(2).size());

	}

	@Test(expected = InventoryFullException.class)
	public void addStackableItemToStackWithNoRoomInStackAndNoRoomInInventory() throws Exception {
		Inventory inventory = inventoryService.createInventory(adam, 1, InventoryType.BAG);

		for (int i = 0; i < 3; i++) {
			inventoryService.add(inventory, createAndPersistItem(mumsTemplate), 1);
		}


		inventory = inventoryService.getInventory(adam, InventoryType.BAG);
		assertItemCountInInventory(3, inventory);
		assertEquals(3, inventory.getItemMap().get(1).size());


		inventoryService.add(inventory, createAndPersistItem(mumsTemplate), 1);

		fail("inventoryService.add should have thrown exception when adding to full inventory");


	}

	@Test
	public void movePartialStackInSameInventory() throws Exception {
		Inventory inventory = inventoryService.createInventory(adam, 2, InventoryType.BAG);
		for (int i = 0; i < 8; i++) {
			inventoryService.add(inventory, createAndPersistItem(slurmsTemplate), 1);
		}

		inventory = inventoryService.getInventory(adam, InventoryType.BAG);
		assertItemCountInInventory(8, inventory);
		assertEquals(8, inventory.getItemMap().get(1).size());

		inventoryService.move(inventory, 1, 4, inventory, 2);

		inventory = inventoryService.getInventory(adam, InventoryType.BAG);
		assertItemCountInInventory(8, inventory);
		assertEquals(4, inventory.getItemMap().get(1).size());
		assertEquals(4, inventory.getItemMap().get(2).size());

	}

	@Test
	public void movePartialStackBetweenInventories() throws Exception {
		Inventory bankInventory = inventoryService.createInventory(adam, 2, InventoryType.BANK);
		Inventory bagInventory = inventoryService.createInventory(adam, 2, InventoryType.BAG);
		for (int i = 0; i < 8; i++) {
			inventoryService.add(bagInventory, createAndPersistItem(slurmsTemplate), 1);
		}

		bagInventory = inventoryService.getInventory(adam, InventoryType.BAG);
		assertItemCountInInventory(8, bagInventory);
		assertEquals(8, bagInventory.getItemMap().get(1).size());

		inventoryService.move(bagInventory, 1, 4, bankInventory, 1);

		bagInventory = inventoryService.getInventory(adam, InventoryType.BAG);
		bankInventory = inventoryService.getInventory(adam, InventoryType.BANK);

		assertItemCountInInventory(4, bankInventory);
		assertItemCountInInventory(4, bagInventory);

		assertEquals(4, bagInventory.getItemMap().get(1).size());
		assertEquals(4, bankInventory.getItemMap().get(1).size());

	}

	@Test
	public void addStackableItemToStackWithRoom() throws Exception {
		Inventory inventory = inventoryService.createInventory(adam, 3, InventoryType.BAG);


		ServerItem item1 = createAndPersistItem(mumsTemplate);
		ServerItem item2 = createAndPersistItem(mumsTemplate);

		inventoryService.add(inventory, item1);
		inventoryService.add(inventory, item2);

		assertEquals(2, inventory.getItemMap().get(1).size());
	}

	@Test
	public void addStackableItemsWithNoExplicitPosition() throws Exception {
		Inventory inventory = inventoryService.createInventory(adam, 10, InventoryType.BAG);

		for (int i = 0; i < 7; i++) {
			inventoryService.add(inventory, createAndPersistItem(mumsTemplate));
		}

		inventory = inventoryService.getInventory(adam, InventoryType.BAG);

		assertItemCountInInventory(7, inventory);
		assertEquals(3, inventory.getItemMap().get(1).size());
		assertEquals(3, inventory.getItemMap().get(2).size());
		assertEquals(1, inventory.getItemMap().get(3).size());


	}

	@Test
	public void firstRemoveItemsThenAddOtherItems() throws Exception {
		Inventory inventory = inventoryService.createInventory(adam, 10, InventoryType.BAG);

		ServerItem glove1 = createAndPersistItem(glovesTemplate);
		inventoryService.add(inventory, glove1);
		ServerItem glove2 = createAndPersistItem(glovesTemplate);
		inventoryService.add(inventory, glove2);

		inventory = inventoryService.getInventory(adam, InventoryType.BAG);
		assertItemCountInInventory(2, inventory);
		assertEquals(1, inventory.getItemMap().get(1).size());
		assertEquals(1, inventory.getItemMap().get(2).size());

		inventoryService.removeIfExists(glove1);
		inventoryService.removeIfExists(glove2);

		inventory = inventoryService.getInventory(adam, InventoryType.BAG);
		assertItemCountInInventory(0, inventory);
		assertEquals(0, inventory.getItemMap().get(1).size());
		assertEquals(0, inventory.getItemMap().get(2).size());


		for (int i = 0; i < 7; i++) {
			inventoryService.add(inventory, createAndPersistItem(mumsTemplate));
		}

		inventory = inventoryService.getInventory(adam, InventoryType.BAG);

		assertItemCountInInventory(7, inventory);
		assertEquals(3, inventory.getItemMap().get(1).size());
		assertEquals(3, inventory.getItemMap().get(2).size());
		assertEquals(1, inventory.getItemMap().get(3).size());


	}
}




