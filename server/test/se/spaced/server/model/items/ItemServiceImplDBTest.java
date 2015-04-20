package se.spaced.server.model.items;

import org.hibernate.Transaction;
import org.junit.Before;
import org.junit.Test;
import se.fearlessgames.common.mock.MockUtil;
import se.spaced.messages.protocol.s2c.S2CProtocol;
import se.spaced.server.model.Player;
import se.spaced.server.model.action.ActionScheduler;
import se.spaced.server.model.combat.SpellCombatService;
import se.spaced.server.model.currency.PersistedCurrency;
import se.spaced.server.model.player.PlayerMockFactory;
import se.spaced.server.net.broadcast.SmrtBroadcaster;
import se.spaced.server.net.broadcast.SmrtBroadcasterImpl;
import se.spaced.server.persistence.dao.impl.hibernate.PersistentTestBase;
import se.spaced.server.persistence.dao.interfaces.InventoryDao;
import se.spaced.server.persistence.dao.interfaces.ItemDao;
import se.spaced.server.persistence.dao.interfaces.ItemTemplateDao;
import se.spaced.server.persistence.dao.interfaces.PlayerDao;
import se.spaced.shared.model.items.ItemType;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static se.mockachino.Mockachino.*;

public class ItemServiceImplDBTest extends PersistentTestBase {

	private ItemService itemService;
	private Player player;
	private ServerItemTemplate itemTemplate;
	private Inventory inventory;
	private InventoryService inventoryService;

	@Before
	public void setup() throws InventoryFullException {
		Transaction tx = sessionFactory.getCurrentSession().beginTransaction();
		sessionFactory.getCurrentSession().save(PersistedCurrency.NONE);
		tx.commit();

		InventoryDao inventoryDao = daoFactory.getInventoryDao();
		PlayerDao playerDao = daoFactory.getPlayerDao();
		ItemDao itemDao = daoFactory.getItemDao();
		ItemTemplateDao itemTemplateDao = daoFactory.getItemTemplateDao();

		SpellCombatService spellCombatService = mock(SpellCombatService.class);
		SmrtBroadcaster<S2CProtocol> broadcaster = MockUtil.deepMock(SmrtBroadcasterImpl.class);
		ActionScheduler actionScheduler = mock(ActionScheduler.class);

		inventoryService = transactionProxyWrapper.wrap(new InventoryServiceImpl(inventoryDao, broadcaster));

		itemService = transactionProxyWrapper.wrap(new ItemServiceImpl(itemTemplateDao,
				itemDao, spellCombatService, timeProvider,
				broadcaster, actionScheduler, inventoryService, uuidFactory));


		PlayerMockFactory factory = new PlayerMockFactory.Builder(timeProvider, uuidFactory).build();
		player = factory.createPlayer("Kalle");
		player.setPk(null);

		playerDao.persist(player);

		tx = sessionFactory.getCurrentSession().beginTransaction();

		itemTemplate = new ServerItemTemplate.Builder(uuidFactory.combUUID(),
				"testItemTemplate",
				ItemType.GLOVES).stackable(2).build();
		itemTemplateDao.persist(itemTemplate);


		ServerItem item = itemTemplate.create();
		itemService.persistItem(item, player);
		tx.commit();


		inventory = inventoryService.createInventory(player, 10, InventoryType.BAG);
		inventoryService.add(inventory, item);


		inventory = inventoryService.getInventory(player, InventoryType.BAG);

	}

	@Test
	public void testDeleteItem() throws Exception {
		ServerItem item = itemTemplate.create();
		itemService.persistItem(item, player);
		inventoryService.add(inventory, item);


		inventory = inventoryService.getInventory(player, InventoryType.BAG);
		assertEquals(2, inventory.getItemMap().get(1).size());


		itemService.deleteItem(item);

		inventory = inventoryService.getInventory(player, InventoryType.BAG);
		assertEquals(1, inventory.getItemMap().get(1).size());

		assertNull(itemService.getItemByPk(item.getPk()));
	}
}
