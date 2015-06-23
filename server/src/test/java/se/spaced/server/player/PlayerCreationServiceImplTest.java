package se.spaced.server.player;

import org.junit.Before;
import org.junit.Test;
import se.fearless.common.uuid.UUID;
import se.spaced.server.ScenarioTestBase;
import se.spaced.server.account.Account;
import se.spaced.server.account.AccountService;
import se.spaced.server.account.AccountType;
import se.spaced.server.model.PersistedCreatureType;
import se.spaced.server.model.PersistedFaction;
import se.spaced.server.model.Player;
import se.spaced.server.model.PlayerType;
import se.spaced.server.model.items.*;
import se.spaced.server.model.spell.ServerSpell;
import se.spaced.shared.model.Gender;
import se.spaced.shared.model.items.ContainerType;
import se.spaced.shared.model.items.ItemType;
import se.spaced.shared.model.stats.EntityStats;

import java.util.Collection;
import java.util.Collections;

import static org.junit.Assert.*;
import static se.mockachino.Mockachino.*;
import static se.spaced.server.model.items.InventoryAssertUtils.assertItemCountInInventory;

public class PlayerCreationServiceImplTest extends ScenarioTestBase {

	private PlayerCreationService playerCreationService;
	private AccountService accountService;
	private PersistedCreatureType cyborg;
	private PersistedCreatureType humanoid;
	private PersistedFaction network;
	private PersistedFaction players;
	private static final double EPSILON = 1e-10;

	@Before
	public void setUp() throws Exception {
		accountService = mock(AccountService.class);
		playerCreationService = new PlayerCreationServiceImpl(transactionManager,
				spellService,
				inventoryService,
				spellDao,
				playerService,
				factionDao,
				creatureTypeDao,
				accountService,
				equipmentService,
				moneyService,
				itemService);
		cyborg = new PersistedCreatureType(uuidFactory.combUUID(), "cyborg");
		humanoid = new PersistedCreatureType(uuidFactory.combUUID(), "humanoid");
		creatureTypeDao.persist(cyborg);
		creatureTypeDao.persist(humanoid);
		network = new PersistedFaction(uuidFactory.combUUID(), "The Network");
		players = new PersistedFaction(uuidFactory.combUUID(), "players");

		factionDao.persist(network);
		factionDao.persist(players);

		itemTemplateDao.persist(new ServerItemTemplate.Builder(UUID.fromString(PlayerCreationServiceImpl.BLUE_JACKET_PK), "Blue Jacket", ItemType.SHIRT).build());
		itemTemplateDao.persist(new ServerItemTemplate.Builder(UUID.fromString(PlayerCreationServiceImpl.GREY_JACKET_PK),
				"Grey Jacket",
				ItemType.SHIRT).build());
		itemTemplateDao.persist(new ServerItemTemplate.Builder(UUID.fromString(PlayerCreationServiceImpl.KNIFE_PK),
				"Rusty Knife",
				ItemType.MAIN_HAND_ITEM,
				ItemType.OFF_HAND_ITEM).build());
		itemTemplateDao.persist(new ServerItemTemplate.Builder(UUID.fromString(PlayerCreationServiceImpl.GM_SURVIVAL_KIT_PK),
				"GM Survival kit",
				ItemType.JETPACK).build());
		itemTemplateDao.persist(new ServerItemTemplate.Builder(UUID.fromString(PlayerCreationServiceImpl.MK5_HELMET_PK), "Mk5 Helmet", ItemType.HELMET).build());
		itemTemplateDao.persist(new ServerItemTemplate.Builder(UUID.fromString(PlayerCreationServiceImpl.MK5_CHEST_PK), "Mk5 Chest", ItemType.SHIRT).build());
		itemTemplateDao.persist(new ServerItemTemplate.Builder(UUID.fromString(PlayerCreationServiceImpl.MK5_LEGS_PK), "Mk5 Legs", ItemType.TROUSERS).build());
		itemTemplateDao.persist(new ServerItemTemplate.Builder(UUID.fromString(PlayerCreationServiceImpl.MK5_FEET_PK), "Mk5 Boots", ItemType.SHOES).build());
		itemTemplateDao.persist(new ServerItemTemplate.Builder(UUID.fromString(PlayerCreationServiceImpl.PLASMA_PK), "Plasma", ItemType.MAIN_HAND_ITEM).build());
		itemTemplateDao.persist(new ServerItemTemplate.Builder(UUID.fromString(PlayerCreationServiceImpl.POPPLER_PK),
				"Bucket of Popplers",
				ItemType.CONSUMABLE).build());
		itemTemplateDao.persist(new ServerItemTemplate.Builder(UUID.fromString(PlayerCreationServiceImpl.LAZOR_PISTOL_PK),
				"Lazor Pistol",
				ItemType.MAIN_HAND_ITEM).build());
		itemTemplateDao.persist(new ServerItemTemplate.Builder(UUID.fromString(PlayerCreationServiceImpl.LEARN_LAZOR_PULSE_PK),
				"Learn Lazor pulse",
				ItemType.CONSUMABLE).build());
	}

	@Test
	public void createPlayer() throws Exception {

		Account account = mock(Account.class);
		Player alice = playerCreationService.createPlayer(account,
				"Alice",
				Gender.FEMALE,
				cyborg,
				network,
				35,
				Collections.<ServerSpell>emptyList(),
				PlayerType.REGULAR);

		verifyOnce().on(accountService).bindCharacterToAccount(account, alice);

		assertNotNull(walletDao.findByOwner(alice));
		Inventory inventory = inventoryService.getInventory(alice, InventoryType.BAG);
		assertNotNull(inventory);
		assertEquals(35, inventory.getNrOfSlots());
		assertItemCountInInventory(12, inventory);


		Collection<ServerSpell> spells = spellService.getSpellsForEntity(alice);
		assertTrue(spells.isEmpty());

		EquippedItems equippedItems = equipmentService.getEquippedItems(alice);
		assertEquals(2, equippedItems.getEquippedItems().size());

		ServerItem chest = equippedItems.get(ContainerType.CHEST);
		assertNotNull(chest);
		assertTrue(chest.getName().contains("Jacket"));

		ServerItem knife = equippedItems.get(ContainerType.MAIN_HAND);
		assertNotNull(knife);
		assertEquals(PlayerCreationServiceImpl.KNIFE_PK, knife.getItemTemplate().getPk().toString());

		assertEquals(EntityStats.IN_COMBAT_COOLRATE, alice.getBaseStats().getCoolRate().getValue(), EPSILON);

	}

	@Test
	public void gmGetsGoodGear() throws PlayerCreationException {
		Account account = mock(Account.class);
		when(account.getType()).thenReturn(AccountType.GM);
		Player alice = playerCreationService.createPlayer(account,
				"Alice",
				Gender.FEMALE,
				cyborg,
				network,
				35,
				Collections.<ServerSpell>emptyList(),
				PlayerType.GM);

		EquippedItems equippedItems = equipmentService.getEquippedItems(alice);
		assertEquals(6, equippedItems.getEquippedItems().size());

		ServerItem back = equippedItems.get(ContainerType.BACK);
		assertNotNull(back);
		assertEquals(PlayerCreationServiceImpl.GM_SURVIVAL_KIT_PK, back.getTemplate().getPk().toString());

		ServerItem helmet = equippedItems.get(ContainerType.HEAD);
		assertNotNull(helmet);
		assertEquals(PlayerCreationServiceImpl.MK5_HELMET_PK, helmet.getTemplate().getPk().toString());

		ServerItem chest = equippedItems.get(ContainerType.CHEST);
		assertNotNull(chest);
		assertEquals(PlayerCreationServiceImpl.MK5_CHEST_PK, chest.getTemplate().getPk().toString());

		ServerItem legs = equippedItems.get(ContainerType.LEGS);
		assertNotNull(legs);
		assertEquals(PlayerCreationServiceImpl.MK5_LEGS_PK, legs.getTemplate().getPk().toString());

		ServerItem feet = equippedItems.get(ContainerType.FEET);
		assertNotNull(feet);
		assertEquals(PlayerCreationServiceImpl.MK5_FEET_PK, feet.getTemplate().getPk().toString());

		ServerItem plasma = equippedItems.get(ContainerType.MAIN_HAND);
		assertNotNull(plasma);
		assertEquals(PlayerCreationServiceImpl.PLASMA_PK, plasma.getTemplate().getPk().toString());
	}
}
