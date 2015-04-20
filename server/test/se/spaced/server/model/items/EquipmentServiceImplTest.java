package se.spaced.server.model.items;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import se.ardortech.math.SpacedVector3;
import se.fearlessgames.common.mock.MockUtil;
import se.mockachino.*;
import se.spaced.messages.protocol.s2c.S2CProtocol;
import se.spaced.server.ScenarioTestBase;
import se.spaced.server.model.PersistedAppearanceData;
import se.spaced.server.model.Player;
import se.spaced.server.model.ServerEntity;
import se.spaced.server.model.aura.KeyAura;
import se.spaced.server.model.aura.ModStatAura;
import se.spaced.server.model.aura.ServerAura;
import se.spaced.server.model.player.PlayerMockFactory;
import se.spaced.server.model.spawn.area.SinglePointSpawnArea;
import se.spaced.server.model.spawn.area.SpawnArea;
import se.spaced.server.persistence.dao.impl.hibernate.GraveyardTemplate;
import se.spaced.shared.model.aura.ModStat;
import se.spaced.shared.model.items.ContainerType;
import se.spaced.shared.model.items.EquipFailure;
import se.spaced.shared.model.items.ItemType;
import se.spaced.shared.model.items.UnequipFailure;
import se.spaced.shared.model.stats.Operator;
import se.spaced.shared.model.stats.StatType;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static se.mockachino.Mockachino.*;
import static se.mockachino.matchers.Matchers.*;

public class EquipmentServiceImplTest extends ScenarioTestBase {
	private ServerEntity entity;
	private ServerItem pants;
	private ServerItem shorts;
	private S2CProtocol observerReceiver;
	private S2CProtocol entityReceiver;
	private ServerItem shirt;
	private ServerItem jumpsuit;
	private ServerItemTemplate pantsTemplate;
	private Player observer;
	private Inventory inventory;
	private static final double EPSILON = 1e-10;

	@Before
	public void setup() {
		entityReceiver = MockUtil.deepMock(S2CProtocol.class);
		observerReceiver = MockUtil.deepMock(S2CProtocol.class);
		PlayerMockFactory playerFactory = new PlayerMockFactory.Builder(timeProvider, uuidFactory).build();
		entity = playerFactory.createPlayer("Kalle");

		observer = playerFactory.createPlayer("Olle");

		entityService.addEntity(entity, entityReceiver);
		entityService.addEntity(observer, observerReceiver);
		pantsTemplate = new ServerItemTemplate.Builder(uuidFactory.combUUID(), "Pants", ItemType.TROUSERS).
				appearance(new PersistedAppearanceData("pants", "pantsIcon")).
				build();
		pants = pantsTemplate.create();
		pants.setPk(uuidFactory.randomUUID());
		pants.setOwner(entity);
		shorts = pantsTemplate.create();
		shorts.setPk(uuidFactory.randomUUID());
		shorts.setOwner(entity);

		ServerItemTemplate shirtTemplate = new ServerItemTemplate.Builder(uuidFactory.combUUID(),
				"Shirt",
				ItemType.SHIRT).build();
		shirt = shirtTemplate.create();
		shirt.setPk(uuidFactory.randomUUID());
		shirt.setOwner(entity);
		ServerItemTemplate jumpsuitTemplate = new ServerItemTemplate.Builder(uuidFactory.combUUID(),
				"Jumpsuit",
				ItemType.JUMPSUIT).build();
		jumpsuit = jumpsuitTemplate.create();
		jumpsuit.setOwner(entity);
		jumpsuit.setPk(uuidFactory.randomUUID());
		inventory = inventoryService.createInventory(entity, 3, InventoryType.BAG);
		try {
			inventoryService.add(inventory, pants);
		} catch (InventoryFullException e) {
		}
		graveyardTemplateDao.persist(new GraveyardTemplate(uuidFactory.randomUUID(), "Graveyard", MockUtil.deepMock(SpawnArea.class)));
	}

	@Test
	public void putOnYourPants() {
		ServerItem naked = equipmentService.getEquippedItem(entity, ContainerType.LEGS);
		assertEquals(null, naked);
		equipmentService.equipItem(entity, ContainerType.LEGS, pants);
		ServerItem legItem = equipmentService.getEquippedItem(entity, ContainerType.LEGS);
		assertEquals(pants, legItem);
		verifyOnce().on(entityReceiver.equipment()).equippedItem(pants, ContainerType.LEGS);

		verifyOnce().on(observerReceiver.equipment()).entityEquippedItem(entity, legItem.getTemplate(), ContainerType.LEGS);

		Inventory inventory = inventoryService.getInventory(entity, InventoryType.BAG);
		assertFalse(inventory.contains(pants));
	}

	@Test
	public void takeOffYourPants() {
		equipmentService.equipItem(entity, ContainerType.LEGS, pants);
		equipmentService.unequipItem(entity, ContainerType.LEGS);
		verifyOnce().on(observerReceiver.equipment()).entityUnequippedItem(entity, ContainerType.LEGS);
		Inventory inventory = inventoryService.getInventory(entity, InventoryType.BAG);
		assertTrue(inventory.contains(pants));
		ServerItem naked = equipmentService.getEquippedItem(entity, ContainerType.LEGS);
		assertNull(naked);
	}

	@Test
	public void pantsNotInInventoryCantBeEquipped() {
		equipmentService.equipItem(entity, ContainerType.LEGS, shorts);

		ServerItem legItem = equipmentService.getEquippedItem(entity, ContainerType.LEGS);
		assertEquals(null, legItem);
		verifyNever().on(entityReceiver.equipment()).equippedItem(pants, ContainerType.LEGS);
		verifyOnce().on(entityReceiver.equipment()).failedToEquipItem(shorts, EquipFailure.ITEM_NOT_FOUND);
	}

	@Test
	public void puttingOnPantsMovesPreviousLegItemToInventory() throws InventoryFullException {
		equipmentService.equipItem(entity, ContainerType.LEGS, pants);

		Mockachino.getData(entityReceiver.equipment()).resetCalls();
		Mockachino.getData(observerReceiver.equipment()).resetCalls();

		Inventory inventory = inventoryService.getInventory(entity, InventoryType.BAG);
		inventoryService.add(inventory, shorts);


		equipmentService.equipItem(entity, ContainerType.LEGS, shorts);
		assertEquals(shorts, equipmentService.getEquippedItem(entity, ContainerType.LEGS));
		inventory = inventoryService.getInventory(entity, InventoryType.BAG);
		assertTrue(inventory.contains(pants));
		verifyOnce().on(entityReceiver.equipment()).unequippedItem(pants, ContainerType.LEGS);
		verifyOnce().on(observerReceiver.equipment()).entityUnequippedItem(entity, ContainerType.LEGS);
		verifyOnce().on(entityReceiver.equipment()).equippedItem(shorts, ContainerType.LEGS);
		verifyOnce().on(observerReceiver.equipment()).entityEquippedItem(entity, shorts.getTemplate(), ContainerType.LEGS);
	}


	@Test
	public void cantEquipPantsOnChest() {
		equipmentService.equipItem(entity, ContainerType.CHEST, pants);
		ServerItem chestItem = equipmentService.getEquippedItem(entity, ContainerType.CHEST);
		assertEquals(null, chestItem);
		verifyNever().on(entityReceiver.equipment()).equippedItem(pants, ContainerType.CHEST);
		verifyOnce().on(entityReceiver.equipment()).failedToEquipItem(pants, EquipFailure.WRONG_SLOT);
	}

	@Test
	public void tryingToUnequipEmptySlotDoesntThrowNPE() {
		equipmentService.unequipItem(entity, ContainerType.CHEST);
	}

	@Test
	public void tryingToUnequipWithFullBagsFails() throws InventoryFullException {
		equipmentService.equipItem(entity, ContainerType.LEGS, pants);
		Inventory inventory = inventoryService.getInventory(entity, InventoryType.BAG);
		inventoryService.add(inventory, shorts);
		inventoryService.add(inventory, jumpsuit);
		inventoryService.add(inventory, shirt);

		equipmentService.unequipItem(entity, ContainerType.LEGS);

		verifyOnce().on(entityReceiver.equipment()).failedToUnequipItem(pants, UnequipFailure.INVENTORY_FULL);
	}

	@Test
	@Ignore
	public void puttingOnJumpsuitMovesPreviousLegAndChestItemToInventory() throws InventoryFullException {
		Inventory inventory = inventoryService.getInventory(entity, InventoryType.BAG);
		inventoryService.add(inventory, shirt);
		inventoryService.add(inventory, jumpsuit);

		equipmentService.equipItem(entity, ContainerType.CHEST, shirt);
		equipmentService.equipItem(entity, ContainerType.LEGS, pants);


		equipmentService.equipItem(entity, ContainerType.CHEST, jumpsuit);
		assertEquals(jumpsuit, equipmentService.getEquippedItem(entity, ContainerType.LEGS));
		inventory = inventoryService.getInventory(entity, InventoryType.BAG);
		assertTrue(inventory.contains(pants));
		assertTrue(inventory.contains(shirt));
	}

	@Test
	public void testEquippedItemWithAura() throws InventoryFullException {
		ServerAura aura = new ModStatAura("Titanium alloy chest armor",
				"",
				0,
				false,
				0,
				false,
				new ModStat(10.0, StatType.STAMINA, Operator.ADD));
		aura.setPk(uuidFactory.randomUUID());
		ServerItemTemplate shirtTemplate = new ServerItemTemplate.Builder(uuidFactory.combUUID(),
				"Shirt",
				ItemType.SHIRT).addAura(aura).build();
		ServerItem auraShirt = createAndAddToInventory(shirtTemplate);

		double preValue = entity.getBaseStats().getMaxHealth().getValue();
		equipmentService.equipItem(entity, ContainerType.CHEST, auraShirt);
		double postValue = entity.getBaseStats().getMaxHealth().getValue();

		assertEquals(preValue + 70.0, postValue, 0.001);

		equipmentService.unequipItem(entity, ContainerType.CHEST);

		assertEquals(preValue, entity.getBaseStats().getMaxHealth().getValue(), 0.001);

	}

	@Test
	public void testEquippedItemWithAuraPostRespawn() throws InventoryFullException {
		when(graveyardService.getClosestGraveyard(any(SpacedVector3.class))).thenReturn(new GraveyardTemplate(uuidFactory.randomUUID(),
				"Foo",
				new SinglePointSpawnArea(entity.getPosition(), entity.getRotation())));
		ServerAura aura = new ModStatAura("Titanium alloy chest armor",
				"",
				0,
				false,
				0,
				false,
				new ModStat(10.0, StatType.STAMINA, Operator.ADD));
		aura.setPk(uuidFactory.randomUUID());
		ServerItemTemplate shirtTemplate = new ServerItemTemplate.Builder(uuidFactory.combUUID(),
				"Shirt",
				ItemType.SHIRT).addAura(aura).build();
		ServerItem auraShirt = createAndAddToInventory(shirtTemplate);

		equipmentService.equipItem(entity, ContainerType.CHEST, auraShirt);
		double hp = entity.getBaseStats().getMaxHealth().getValue();

		deathService.kill(entity);

		deathService.respawn(entity);

		double hpPostRespawn = entity.getBaseStats().getMaxHealth().getValue();

		assertEquals(hp, hpPostRespawn, EPSILON);
	}


	@Test
	public void testEquipItemsWithSameAuraThenRemoveOne() throws InventoryFullException {
		ServerAura aura = new KeyAura("guitar-equipped",
				"",
				0,
				false,
				false);
		aura.setPk(uuidFactory.randomUUID());
		ServerItemTemplate stratocasterTemplate = new ServerItemTemplate.Builder(uuidFactory.combUUID(),
				"Stratocaster",
				ItemType.MAIN_HAND_ITEM, ItemType.OFF_HAND_ITEM).addAura(aura).build();
		ServerItem stratocaster = createAndAddToInventory(stratocasterTemplate);

		ServerItemTemplate lesPaulTemplate = new ServerItemTemplate.Builder(uuidFactory.combUUID(),
				"Les Paul",
				ItemType.MAIN_HAND_ITEM, ItemType.OFF_HAND_ITEM).addAura(aura).build();

		ServerItem lesPaul = createAndAddToInventory(lesPaulTemplate);

		equipmentService.equipItem(entity, ContainerType.MAIN_HAND, stratocaster);
		assertTrue(auraService.hasAura(entity, aura));

		tick(1);
		equipmentService.equipItem(entity, ContainerType.OFF_HAND, lesPaul);
		assertTrue(auraService.hasAura(entity, aura));

		equipmentService.unequipItem(entity, ContainerType.MAIN_HAND);

		assertTrue(auraService.hasAura(entity, aura));

		equipmentService.unequipItem(entity, ContainerType.OFF_HAND);
		assertFalse(auraService.hasAura(entity, aura));
	}

	private ServerItem createAndAddToInventory(ServerItemTemplate template) throws InventoryFullException {
		ServerItem stratocaster = template.create();
		stratocaster.setPk(uuidFactory.randomUUID());
		stratocaster.setOwner(entity);
		inventoryService.add(inventory, stratocaster);
		return stratocaster;
	}

}
