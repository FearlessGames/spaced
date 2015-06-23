package se.spaced.server.loot;

import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;
import se.fearless.common.mock.MockUtil;
import se.fearless.common.time.MockTimeProvider;
import se.fearless.common.uuid.UUID;
import se.fearless.common.uuid.UUIDMockFactory;
import se.mockachino.annotations.Mock;
import se.spaced.messages.protocol.s2c.S2CProtocol;
import se.spaced.server.model.Player;
import se.spaced.server.model.currency.MoneyService;
import se.spaced.server.model.currency.PersistedCurrency;
import se.spaced.server.model.currency.PersistedMoney;
import se.spaced.server.model.entity.EntityService;
import se.spaced.server.model.entity.EntityServiceImpl;
import se.spaced.server.model.entity.EntityServiceListener;
import se.spaced.server.model.items.*;
import se.spaced.server.model.player.PlayerMockFactory;
import se.spaced.server.persistence.dao.impl.hibernate.TransactionManager;
import se.spaced.shared.util.ListenerDispatcher;
import se.spaced.shared.util.random.RandomProvider;

import java.util.Collection;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static se.mockachino.Mockachino.*;

public class LootAwardServiceTest {
	@Mock
	private InventoryService inventoryService;
	@Mock
	private TransactionManager transactionManager;
	@Mock
	private RandomProvider randomProvider;
	@Mock
	private ItemService itemService;
	@Mock
	private MoneyService moneyService;

	@Mock
	private PersistedInventory inventory;
	@Mock
	private ServerItem item;
	@Mock
	private ServerItemTemplate itemTemplate;

	private LootAwardServiceImpl lootService;
	private Player player;
	private S2CProtocol receiver;

	@Before
	public void setup() {
		setupMocks(this);
		EntityService entityService = new EntityServiceImpl(new UUIDMockFactory(), ListenerDispatcher.create(EntityServiceListener.class));
		lootService = new LootAwardServiceImpl(inventoryService,
				transactionManager,
				itemService,
				moneyService, entityService);
		player = new PlayerMockFactory.Builder(new MockTimeProvider(), new UUIDMockFactory()).build().createPlayer("Player");
		receiver = MockUtil.deepMock(S2CProtocol.class);
		entityService.addEntity(player, receiver);

		stubReturn(inventory).on(inventoryService).getInventory(player, InventoryType.BAG);
		stubReturn(item).on(itemTemplate).create();
	}

	@Test
	public void awardsItem() throws Exception {
		stubReturn(true).on(inventory).contains(item);

		boolean awarded = lootService.awardItem(player, item);

		verifyOnce().on(itemService).persistItem(item, player);
		verifyOnce().on(inventoryService).add(inventory, item);
		assertTrue(awarded);
	}

	@Test
	public void failsAwardingFullInventory() throws Exception {
		stubReturn(true).on(inventory).isFull();

		boolean awarded = lootService.awardItem(player, item);

		assertFalse(awarded);
		verifyNever().on(itemService).persistItem(item, player);
		verifyNever().on(inventoryService).add(inventory, item);
	}

	@Test
	public void failsAwardingInventoryGotFull() throws Exception {
		stubThrow(new InventoryFullException()).on(inventoryService).add(inventory, item);

		boolean awarded = lootService.awardItem(player, item);

		assertFalse(awarded);
		verifyOnce().on(item).setOwner(null);
	}

	@Test
	public void distributesLoot() throws Exception {
		LootTemplate lootTemplate = new SingleItemLootTemplate(UUID.ZERO, "templateName", itemTemplate);

		when(inventory.contains(item)).thenReturn(true);
		lootService.awardLoot(player, lootTemplate.generateLoot(randomProvider));

		verifyOnce().on(itemService).persistItem(item, player);
		verifyOnce().on(inventoryService).add(inventory, item);
		verifyOnce().on(receiver.loot()).receivedLoot(item);
	}

	@Test
	public void distributesMoney() throws Exception {
		PersistedMoney money = new PersistedMoney(PersistedCurrency.NONE, 1000);
		Collection<Loot> loot = Lists.newArrayList(new Loot(itemTemplate, money));
		LootTemplate lootTemplate = mock(LootTemplate.class);
		stubReturn(loot).on(lootTemplate).generateLoot(randomProvider);

		lootService.awardLoot(player, lootTemplate.generateLoot(randomProvider));

		verifyOnce().on(moneyService).awardMoney(player, money);
	}
}
