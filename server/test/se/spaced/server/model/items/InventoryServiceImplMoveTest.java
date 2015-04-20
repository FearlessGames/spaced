package se.spaced.server.model.items;

import org.hibernate.Transaction;
import org.junit.Before;
import org.junit.Test;
import se.fearlessgames.common.mock.MockUtil;
import se.spaced.server.model.PersistedCreatureType;
import se.spaced.server.model.PersistedFaction;
import se.spaced.server.model.Player;
import se.spaced.server.model.PlayerType;
import se.spaced.server.model.currency.PersistedCurrency;
import se.spaced.server.net.broadcast.SmrtBroadcasterImpl;
import se.spaced.server.persistence.dao.impl.hibernate.PersistentTestBase;
import se.spaced.server.player.PlayerService;
import se.spaced.server.player.PlayerServiceImpl;
import se.spaced.shared.model.Gender;
import se.spaced.shared.model.items.ItemType;

import static org.junit.Assert.assertEquals;

public class InventoryServiceImplMoveTest extends PersistentTestBase {
	private InventoryServiceImpl inventoryService;
	private Player adam;
	private Player eve;

	private ServerItemTemplate glovesTemplate;
	private ServerItemTemplate mumsTemplate;
	private ServerItemTemplate slurmsTemplate;

	private PersistedInventory bank;
	private PersistedInventory bag;
	private PersistedInventory megaBag;

	private Player currentPlayer;


	@Before
	public void setUp() throws Exception {
		Transaction tx = sessionFactory.getCurrentSession().beginTransaction();
		sessionFactory.getCurrentSession().save(PersistedCurrency.NONE);
		tx.commit();

		inventoryService = new InventoryServiceImpl(daoFactory.getInventoryDao(), MockUtil.deepMock(SmrtBroadcasterImpl.class));
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

		eve = playerService.createPlayerCharacter("Eve", Gender.FEMALE, creatureType, faction, PlayerType.REGULAR);
		eve = playerService.getPlayer("Eve");

		bank = inventoryService.createInventory(adam, 1, InventoryType.BANK);
		bag = inventoryService.createInventory(adam, 1, InventoryType.BAG);
		megaBag = inventoryService.createInventory(eve, 2, InventoryType.BAG);

		currentPlayer = adam;

	}

	private ServerItem createAndPersistItem(ServerItemTemplate itemTemplate) {
		ServerItem item = itemTemplate.create();
		item.setOwner(currentPlayer);
		daoFactory.getItemDao().persist(item);
		return item;
	}

	private void addItems(int quantity, ServerItemTemplate template, PersistedInventory inventory, int pos)
			throws InventoryFullException, InventoryOutOfBoundsException {
		for (int i = 0; i < quantity; i++) {
			inventoryService.add(inventory, createAndPersistItem(template), pos);
		}
	}

	private void loadInventories() {
		bank = inventoryService.getInventory(adam, InventoryType.BANK);
		bag = inventoryService.getInventory(adam, InventoryType.BAG);
		megaBag = inventoryService.getInventory(eve, InventoryType.BAG);
	}


	@Test(expected = InventoryFullException.class)
	public void movePartialStackToInventoryThatsFullSameItems() throws Exception {
		addItems(3, mumsTemplate, bank, 1);
		addItems(1, glovesTemplate, bank, 1);

		inventoryService.move(bank, 1, 2, bag, 1);

	}

	@Test
	public void movePartialStackToInventoryThatsFullOfOtherItems() throws Exception {
		addItems(3, slurmsTemplate, bank, 1);
		addItems(1, slurmsTemplate, bag, 1);

		inventoryService.move(bank, 1, 2, bag, 1);


		loadInventories();

		assertEquals(3, bag.getItemMap().get(1).size());
		assertEquals(1, bank.getItemMap().get(1).size());

	}


	@Test
	public void movePartialStackToInventoryThatWillFillUp() throws Exception {
		addItems(5, slurmsTemplate, bank, 1);
		addItems(8, slurmsTemplate, bag, 1);

		inventoryService.move(bank, 1, 5, bag, 1);

		loadInventories();

		assertEquals(10, bag.getItemMap().get(1).size());
		assertEquals(3, bank.getItemMap().get(1).size());

	}


	@Test
	public void swapItemsBetweenInventories() throws Exception {
		addItems(5, slurmsTemplate, bank, 1);
		addItems(2, mumsTemplate, bag, 1);


		inventoryService.move(bank, 1, bag, 1);


		loadInventories();

		assertEquals(2, bank.getItemMap().get(1).size());
		assertEquals(5, bag.getItemMap().get(1).size());

		assertEquals(mumsTemplate.getPk(), bank.getItemTemplateOnPosition(1).getPk());
		assertEquals(slurmsTemplate.getPk(), bag.getItemTemplateOnPosition(1).getPk());
	}

	@Test
	public void swapItemsInSameInventories() throws Exception {
		currentPlayer = eve;

		addItems(5, slurmsTemplate, megaBag, 1);
		addItems(2, mumsTemplate, megaBag, 2);
		loadInventories();

		assertEquals(5, megaBag.getItemMap().get(1).size());
		assertEquals(2, megaBag.getItemMap().get(2).size());

		assertEquals(slurmsTemplate.getPk(), megaBag.getItemTemplateOnPosition(1).getPk());
		assertEquals(mumsTemplate.getPk(), megaBag.getItemTemplateOnPosition(2).getPk());

		inventoryService.move(megaBag, 1, megaBag, 2);


		loadInventories();

		assertEquals(2, megaBag.getItemMap().get(1).size());
		assertEquals(5, megaBag.getItemMap().get(2).size());

		assertEquals(mumsTemplate.getPk(), megaBag.getItemTemplateOnPosition(1).getPk());
		assertEquals(slurmsTemplate.getPk(), megaBag.getItemTemplateOnPosition(2).getPk());


	}
}
