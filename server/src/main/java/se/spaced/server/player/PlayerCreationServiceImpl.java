package se.spaced.server.player;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import org.hibernate.Transaction;
import se.fearlessgames.common.collections.Collections3;
import se.fearlessgames.common.util.uuid.UUID;
import se.spaced.server.account.Account;
import se.spaced.server.account.AccountService;
import se.spaced.server.account.AccountType;
import se.spaced.server.model.PersistedCreatureType;
import se.spaced.server.model.PersistedFaction;
import se.spaced.server.model.Player;
import se.spaced.server.model.PlayerType;
import se.spaced.server.model.currency.MoneyService;
import se.spaced.server.model.items.EquipmentService;
import se.spaced.server.model.items.Inventory;
import se.spaced.server.model.items.InventoryFullException;
import se.spaced.server.model.items.InventoryService;
import se.spaced.server.model.items.InventoryType;
import se.spaced.server.model.items.ItemService;
import se.spaced.server.model.items.ServerItem;
import se.spaced.server.model.items.ServerItemTemplate;
import se.spaced.server.model.spell.ServerSpell;
import se.spaced.server.persistence.DuplicateObjectException;
import se.spaced.server.persistence.dao.impl.hibernate.TransactionManager;
import se.spaced.server.persistence.dao.interfaces.CreatureTypeDao;
import se.spaced.server.persistence.dao.interfaces.FactionDao;
import se.spaced.server.persistence.dao.interfaces.SpellDao;
import se.spaced.server.spell.SpellService;
import se.spaced.shared.model.Gender;
import se.spaced.shared.model.items.ContainerType;
import se.spaced.shared.model.player.PlayerCreationFailure;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

public class PlayerCreationServiceImpl implements PlayerCreationService {

	static final String GM_SURVIVAL_KIT_PK = "bee21fe4-0543-46e1-9a52-9f50d65ee89b";
	static final String MK5_HELMET_PK = "7d676843-f31a-44ee-bc59-9daa4a013841";
	static final String MK5_CHEST_PK = "48aeeb54-416d-4400-a572-9e87a072bd5b";
	static final String MK5_LEGS_PK = "9e8c76a0-fad6-49d3-b47c-9daa4a013841";
	static final String MK5_FEET_PK = "03b6bafe-cd9b-480d-9a23-9daa4a013841";
	static final String PLASMA_PK = "23279d17-b9bb-45c8-a57d-9e88a0a49886";
	static final String BLUE_JACKET_PK = "9f4b75e4-650e-4e2b-ab62-9e89a20896a1";
	static final String GREY_JACKET_PK = "6e760c6e-27f8-402f-b75a-9e88a1ffee86";
	static final String KNIFE_PK = "2257a591-476c-42bb-a1f9-9f5dea921de3";
	static final String LAZOR_PISTOL_PK = "48fadcdf-1166-410a-a0d8-9daa4a01384f";
	static final String LEARN_LAZOR_PULSE_PK = "28d8ec66-05a2-42bd-b5e4-a099d323cbd2";
	static final String POPPLER_PK = "d44cc246-9724-40cc-a0ba-9daa4a01383c";

	private final PlayerService playerService;
	private final AccountService accountService;
	private final SpellService spellService;
	private final InventoryService inventoryService;
	private final SpellDao spellDao;
	private final FactionDao factionDao;
	private final CreatureTypeDao creatureTypeDao;
	private final TransactionManager transactionManager;
	private final EquipmentService equipmentService;
	private final MoneyService moneyService;
	private final ItemService itemService;

	@Inject
	public PlayerCreationServiceImpl(
			TransactionManager transactionManager, SpellService spellService,
			InventoryService inventoryService,
			SpellDao spellDao,
			PlayerService playerService,
			FactionDao factionDao,
			CreatureTypeDao creatureTypeDao,
			AccountService accountService,
			EquipmentService equipmentService,
			MoneyService moneyService, ItemService itemService) {
		this.transactionManager = transactionManager;
		this.spellService = spellService;
		this.inventoryService = inventoryService;
		this.spellDao = spellDao;
		this.playerService = playerService;
		this.factionDao = factionDao;
		this.creatureTypeDao = creatureTypeDao;
		this.accountService = accountService;
		this.equipmentService = equipmentService;
		this.moneyService = moneyService;
		this.itemService = itemService;
	}

	@Override
	public Player createPlayer(
			Account account, String name, Gender gender, PersistedCreatureType creatureType, PersistedFaction faction,
			int playerInventorySize,
			Collection<ServerSpell> spells, PlayerType type) throws DuplicateObjectException, PlayerCreationException {

		if (type == PlayerType.GM && account.getType() != AccountType.GM) {
			throw new PlayerCreationException("Not allowed to create a GM char on a non GM account",
					PlayerCreationFailure.UNATHORIZED_PLAYER_TYPE);
		}

		Player player = playerService.createPlayerCharacter(name, gender, creatureType, faction, type);

		spellService.createSpellBookForEntity(player);
		equipmentService.createEquippedItemsForEntity(player);
		Inventory inventory = inventoryService.createInventory(player, playerInventorySize, InventoryType.BAG);
		switch (type) {

			case REGULAR:
				addStarterGear(player, inventory);
				break;
			case GM:
				addGmGear(player, inventory);
				break;
		}
		moneyService.createWallet(player);

		for (ServerSpell spell : spells) {
			spellService.addSpellForEntity(player, spell);
		}

		playerService.updatePlayer(player);
		accountService.bindCharacterToAccount(account, player);

		return player;
	}

	private void addGmGear(Player player, Inventory inventory) {
		ServerItemTemplate survivalKit = itemService.getTemplateByPk(UUID.fromString(
				GM_SURVIVAL_KIT_PK));
		addItem(player, inventory, survivalKit.create(), ContainerType.BACK);

		ServerItemTemplate helmet = itemService.getTemplateByPk(UUID.fromString(
				MK5_HELMET_PK));
		addItem(player, inventory, helmet.create(), ContainerType.HEAD);

		ServerItemTemplate chest = itemService.getTemplateByPk(UUID.fromString(
				MK5_CHEST_PK));
		addItem(player, inventory, chest.create(), ContainerType.CHEST);

		ServerItemTemplate legs = itemService.getTemplateByPk(UUID.fromString(
				MK5_LEGS_PK));
		addItem(player, inventory, legs.create(), ContainerType.LEGS);

		ServerItemTemplate feet = itemService.getTemplateByPk(UUID.fromString(
				MK5_FEET_PK));
		addItem(player, inventory, feet.create(), ContainerType.FEET);

		ServerItemTemplate plasma = itemService.getTemplateByPk(UUID.fromString(
				PLASMA_PK));
		addItem(player, inventory, plasma.create(), ContainerType.MAIN_HAND);

	}

	private void addStarterGear(Player player, Inventory inventory) {
		ArrayList<ServerItemTemplate> jackets = Lists.newArrayList(
				itemService.getTemplateByPk(UUID.fromString(BLUE_JACKET_PK)),
				itemService.getTemplateByPk(UUID.fromString(GREY_JACKET_PK))
		);
		ServerItem item = Collections3.getRandomElement(jackets, new Random()).create();

		ServerItemTemplate knife = itemService.getTemplateByPk(UUID.fromString(
				KNIFE_PK));
		addItem(player, inventory, item, ContainerType.CHEST);
		addItem(player, inventory, knife.create(), ContainerType.MAIN_HAND);

		ServerItemTemplate learnLazorPulse = itemService.getTemplateByPk(UUID.fromString(LEARN_LAZOR_PULSE_PK));
		addToInventory(player, inventory, learnLazorPulse.create());

		ServerItemTemplate lazorPistol = itemService.getTemplateByPk(UUID.fromString(LAZOR_PISTOL_PK));
		addToInventory(player, inventory, lazorPistol.create());

		ServerItemTemplate popplers = itemService.getTemplateByPk(UUID.fromString(POPPLER_PK));
		for (int i = 0; i < 10; i++) {
			addToInventory(player, inventory, popplers.create());
		}
	}

	private void addItem(Player player, Inventory inventory, ServerItem item, ContainerType containerType) {
		addToInventory(player, inventory, item);
		equipmentService.equipItem(player, containerType, item);
	}

	private void addToInventory(Player player, Inventory inventory, ServerItem item) {
		inventory = inventoryService.getInventory(player, InventoryType.BAG);
		itemService.persistItem(item, player);
		try {
			inventoryService.add(inventory, item);
		} catch (InventoryFullException e) {
			throw new RuntimeException("Newly created inventory was full", e);
		}
	}

	@Override
	public Player createDefaultPlayer(
			Account account,
			String name,
			Gender gender,
			PlayerType type) throws DuplicateObjectException, PlayerCreationException {
		Transaction transaction = transactionManager.beginTransaction();
		Collection<ServerSpell> spells = new ArrayList<ServerSpell>();
		spells.add(spellDao.findByName("Strike"));

		PersistedCreatureType creatureType = creatureTypeDao.findByName("humanoid");
		PersistedFaction faction = factionDao.findByName("players");

		transaction.commit();

		return createPlayer(account, name, gender, creatureType, faction, 24, spells, type);
	}
}
