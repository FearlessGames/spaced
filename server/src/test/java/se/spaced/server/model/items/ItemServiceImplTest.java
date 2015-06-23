package se.spaced.server.model.items;

import org.hibernate.Transaction;
import org.junit.Before;
import org.junit.Test;
import se.fearless.common.mock.MockUtil;
import se.fearless.common.uuid.UUID;
import se.mockachino.matchers.matcher.ArgumentCatcher;
import se.spaced.messages.protocol.SpacedItem;
import se.spaced.messages.protocol.s2c.S2CProtocol;
import se.spaced.server.ScenarioTestBase;
import se.spaced.server.model.ServerEntity;
import se.spaced.server.model.action.SpellListener;
import se.spaced.server.model.spell.ServerSpell;
import se.spaced.shared.model.items.ItemType;

import java.util.Collection;

import static org.junit.Assert.*;
import static se.mockachino.Mockachino.*;
import static se.mockachino.matchers.Matchers.*;
import static se.spaced.server.model.items.InventoryAssertUtils.assertItemCountInInventory;

public class ItemServiceImplTest extends ScenarioTestBase {

	@Before
	public void setup() {
		stubReturn(mock(Transaction.class)).on(transactionManager).beginTransaction();
		stubReturn(null).on(spellCombatService).startSpellCast(any(ServerEntity.class),
				any(ServerEntity.class),
				any(ServerSpell.class),
				anyLong(),
				any(SpellListener.class));
	}

	@Test
	public void getAllTemplates() {
		itemTemplateDao.persist(new ServerItemTemplate.Builder(uuidFactory.combUUID(),
				"Can of Slurm",
				ItemType.CONSUMABLE).build());
		Collection<ServerItemTemplate> templates = itemService.getAllTemplates();
		assertFalse(templates.isEmpty());
	}

	@Test
	public void testCreateItem() {
		ServerItemTemplate itemTemplate = itemTemplateDao.persist(new ServerItemTemplate.Builder(uuidFactory.combUUID(),
				"Can of Slurm", ItemType.CONSUMABLE).build());
		ServerItem serverItem = itemTemplate.create();
		assertEquals("Can of Slurm", serverItem.getName());

	}

	@Test
	public void testUseSimpleItem() throws Exception {
		ServerEntity player = mockWithId(ServerEntity.class);
		stubReturn(true).on(player).isAlive();
		ServerSpell spell = mock(ServerSpell.class);

		ServerItem item = mock(ServerItem.class);

		stubReturn(spell).on(item).getSpell();

		itemService.useItem(player, player, item);
		tick(0);
		verifyExactly(1).on(spellCombatService).startSpellCast(player, player, spell, timeProvider.now(), null);
	}


	@Test
	public void testConsumeItemNotConsumable() throws InventoryFullException {
		ServerEntity player = mockWithId(ServerEntity.class);
		stubReturn(true).on(player).isAlive();
		ServerSpell spell = mock(ServerSpell.class);

		S2CProtocol receiver = MockUtil.deepMock(S2CProtocol.class);
		stubReturn(receiver).on(entityService).getSmrtReceiver(player);

		ServerItem item = new ServerItem(new ServerItemTemplate.Builder(uuidFactory.combUUID(),
				"Can of Slurm",
				ItemType.HELMET).spell(spell).build());
		itemService.persistItem(item, player);

		Inventory inventory = inventoryService.createInventory(player, 10, InventoryType.BAG);

		inventoryService.add(inventory, item);

		itemService.useItem(player, player, item);
		tick(0);

		ArgumentCatcher<SpellListener> catcher = ArgumentCatcher.create(mAny(SpellListener.class));
		verifyExactly(1).on(spellCombatService).startSpellCast(player, player, spell, eq(timeProvider.now()), m(catcher));
		SpellListener listener = catcher.getValue();
		assertEquals(listener, null);
		assertTrue(inventory.contains(item));

		verifyNever().on(receiver.item()).itemRemoved(inventory, anyInt(), any(SpacedItem.class));
	}

	@Test
	public void testConsumeItem() throws InventoryFullException {
		ServerEntity player = mock(ServerEntity.class);
		stubReturn(true).on(player).isAlive();
		ServerSpell spell = mock(ServerSpell.class);

		S2CProtocol receiver = MockUtil.deepMock(S2CProtocol.class);
		stubReturn(receiver).on(entityService).getSmrtReceiver(player);

		ServerItemTemplate itemTemplate = new ServerItemTemplate.Builder(uuidFactory.combUUID(),
				"Can of Slurm",
				ItemType.CONSUMABLE).spell(spell).build();
		ServerItem item = new ServerItem(itemTemplate);

		itemService.persistItem(item, player);

		Inventory inventory = inventoryService.createInventory(player, 10, InventoryType.BAG);

		inventoryService.add(inventory, item);

		itemService.useItem(player, player, item);
		tick(0);

		ArgumentCatcher<SpellListener> catcher = ArgumentCatcher.create(mAny(SpellListener.class));
		verifyExactly(1).on(spellCombatService).startSpellCast(player, player, spell, eq(timeProvider.now()), m(catcher));
		SpellListener listener = catcher.getValue();
		listener.notifySpellCompleted();
		assertFalse(inventory.contains(item));
		verifyExactly(1).on(itemDao).delete(item);
		ArgumentCatcher<SpacedItem> argumentCatcher = ArgumentCatcher.create(mAny(SpacedItem.class));
		verifyExactly(1).on(receiver.item()).itemRemoved(inventory, anyInt(), m(argumentCatcher));

		assertEquals(itemTemplate.getPk(), argumentCatcher.getValue().getItemTemplate().getPk());
	}

	@Test
	public void testRemoveNullItem() {
		try {
			itemService.deleteItem(null);
		} catch (Exception e) {
			fail();
		}
	}


	@Test
	public void testUseItemOwnerIsDead() throws Exception {
		ServerEntity player = mock(ServerEntity.class);
		stubReturn(false).on(player).isAlive();
		final UUID uuid = UUID.ZERO;
		ServerItem item = new ServerItem(new ServerItemTemplate.Builder(uuidFactory.combUUID(),
				"Can of Slurm",
				ItemType.CONSUMABLE).build());
		item.setPk(uuid);

		itemService.useItem(player, player, item);
		tick(0);

		verifyNever().on(spellCombatService).startSpellCast(any(ServerEntity.class),
				any(ServerEntity.class),
				any(ServerSpell.class),
				anyLong(),
				any(SpellListener.class));
	}

	@Test
	public void testTransferItem() throws Exception {
		ServerEntity player = mockWithId(ServerEntity.class);
		ServerEntity other = mockWithId(ServerEntity.class);

		S2CProtocol playerReceiver = MockUtil.deepMock(S2CProtocol.class);
		stubReturn(playerReceiver).on(entityService).getSmrtReceiver(player);

		S2CProtocol otherReceiver = MockUtil.deepMock(S2CProtocol.class);
		stubReturn(otherReceiver).on(entityService).getSmrtReceiver(other);


		ServerItem item = new ServerItem(new ServerItemTemplate.Builder(uuidFactory.combUUID(),
				"Can of Slurm",
				ItemType.CONSUMABLE).build());
		itemService.persistItem(item, player);

		Inventory inventory = inventoryService.createInventory(player, 10, InventoryType.BAG);

		inventoryService.add(inventory, item);

		Inventory othersInventory = inventoryService.createInventory(other, 10, InventoryType.BAG);

		ExchangeResult exchangeResult = itemService.transferItem(player, other, item);

		assertEquals(ExchangeResult.SUCCESS, exchangeResult);

		assertItemCountInInventory(0, inventory);
		assertItemCountInInventory(1, othersInventory);

		assertEquals(item.getOwner(), other);
		assertTrue(othersInventory.contains(item));

		verifyOnce().on(otherReceiver.item()).itemAdded(othersInventory, anyInt(), item);

		verifyOnce().on(playerReceiver.item()).itemRemoved(inventory, anyInt(), item);
	}

	@Test
	public void testTransferItemWrongOwner() throws Exception {
		ServerEntity player = mockWithId(ServerEntity.class);
		ServerEntity other = mockWithId(ServerEntity.class);

		final UUID uuid = new UUID(1, 2);
		ServerItem item = new ServerItem(new ServerItemTemplate.Builder(uuidFactory.combUUID(),
				"Can of Slurm",
				ItemType.CONSUMABLE).build());
		item.setPk(uuid);

		Inventory inventory = inventoryService.createInventory(player, 10, InventoryType.BAG);

		Inventory othersInventory = inventoryService.createInventory(other, 10, InventoryType.BAG);

		ExchangeResult exchangeResult = itemService.transferItem(player, other, item);

		assertEquals(ExchangeResult.WRONG_OWNER, exchangeResult);

		assertItemCountInInventory(0, othersInventory);

		assertFalse(othersInventory.contains(item));
	}

	@Test
	public void testTransferItemOutOfSpace() throws Exception {
		ServerEntity player = mockWithId(ServerEntity.class);
		ServerEntity other = mockWithId(ServerEntity.class);


		ServerItem item = new ServerItem(new ServerItemTemplate.Builder(uuidFactory.combUUID(),
				"Can of Slurm",
				ItemType.CONSUMABLE).build());
		itemService.persistItem(item, player);

		Inventory inventory = inventoryService.createInventory(player, 10, InventoryType.BAG);

		inventoryService.add(inventory, item);

		Inventory othersInventory = inventoryService.createInventory(other, 0, InventoryType.BAG);

		ExchangeResult exchangeResult = itemService.transferItem(player, other, item);

		assertEquals(ExchangeResult.NOT_ENOUGH_SPACE, exchangeResult);

		assertEquals(player, item.getOwner());
		assertEquals(0, othersInventory.getItemMap().size());
		assertFalse(othersInventory.contains(item));
	}

	@Test
	public void testExchangeItems() throws Exception {
		ServerEntity player = mockWithId(ServerEntity.class);
		ServerEntity other = mockWithId(ServerEntity.class);

		S2CProtocol playerReceiver = MockUtil.deepMock(S2CProtocol.class);
		stubReturn(playerReceiver).on(entityService).getSmrtReceiver(player);

		S2CProtocol otherReceiver = MockUtil.deepMock(S2CProtocol.class);
		stubReturn(otherReceiver).on(entityService).getSmrtReceiver(other);

		ServerItem item1 = new ServerItem(new ServerItemTemplate.Builder(uuidFactory.combUUID(),
				"Can of Slurm",
				ItemType.CONSUMABLE).build());
		itemService.persistItem(item1, player);

		ServerItem item2 = new ServerItem(new ServerItemTemplate.Builder(uuidFactory.combUUID(),
				"Can of Slurm",
				ItemType.CONSUMABLE).build());
		itemService.persistItem(item2, other);

		Inventory inventory = inventoryService.createInventory(player, 10, InventoryType.BAG);
		inventoryService.add(inventory, item1);

		Inventory othersInventory = inventoryService.createInventory(other, 10, InventoryType.BAG);
		inventoryService.add(othersInventory, item2);

		ExchangeResult exchangeResult = itemService.exchangeItems(player, item1, other, item2);

		assertEquals(ExchangeResult.SUCCESS, exchangeResult);

		assertItemCountInInventory(1, inventory);
		assertItemCountInInventory(1, othersInventory);

		assertTrue(inventory.contains(item2));
		assertEquals(player, item2.getOwner());
		assertTrue(othersInventory.contains(item1));
		assertEquals(other, item1.getOwner());

		verifyOnce().on(playerReceiver.item()).itemAdded(inventory, anyInt(), item2);
		verifyOnce().on(otherReceiver.item()).itemAdded(othersInventory, anyInt(), item1);

		verifyOnce().on(playerReceiver.item()).itemRemoved(inventory, anyInt(), item1);
		verifyOnce().on(otherReceiver.item()).itemRemoved(othersInventory, anyInt(), item2);
	}

	@Test
	public void testExchangeItemsNoSpace() throws Exception {
		ServerEntity player = mockWithId(ServerEntity.class);
		ServerEntity other = mockWithId(ServerEntity.class);

		ServerItem item1 = new ServerItem(new ServerItemTemplate.Builder(uuidFactory.combUUID(),
				"Can of Slurm",
				ItemType.CONSUMABLE).build());
		itemService.persistItem(item1, player);

		ServerItem item2 = new ServerItem(new ServerItemTemplate.Builder(uuidFactory.combUUID(),
				"Can of Slurm",
				ItemType.CONSUMABLE).build());
		itemService.persistItem(item2, other);

		Inventory inventory = inventoryService.createInventory(player, 1, InventoryType.BAG);
		inventoryService.add(inventory, item1);

		Inventory othersInventory = inventoryService.createInventory(other, 1, InventoryType.BAG);
		inventoryService.add(othersInventory, item2);

		ExchangeResult exchangeResult = itemService.exchangeItems(player, item1, other, item2);

		assertEquals(ExchangeResult.SUCCESS, exchangeResult);

		assertEquals(1, inventory.getItemMap().size());
		assertEquals(1, othersInventory.getItemMap().size());
		assertTrue(inventory.contains(item2));
		assertEquals(player, item2.getOwner());
		assertTrue(othersInventory.contains(item1));
		assertEquals(other, item1.getOwner());
	}
}
