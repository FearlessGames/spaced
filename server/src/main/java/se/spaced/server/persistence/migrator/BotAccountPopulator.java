package se.spaced.server.persistence.migrator;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.fearlessgames.common.util.Digester;
import se.fearlessgames.common.util.uuid.UUID;
import se.spaced.server.account.Account;
import se.spaced.server.account.AccountService;
import se.spaced.server.account.AccountType;
import se.spaced.server.model.PersistedCreatureType;
import se.spaced.server.model.PersistedFaction;
import se.spaced.server.model.Player;
import se.spaced.server.model.PlayerType;
import se.spaced.server.model.spell.ServerSpell;
import se.spaced.server.persistence.DuplicateObjectException;
import se.spaced.server.persistence.dao.impl.hibernate.TransactionManager;
import se.spaced.server.persistence.dao.interfaces.AccountDao;
import se.spaced.server.persistence.dao.interfaces.CreatureTypeDao;
import se.spaced.server.persistence.dao.interfaces.FactionDao;
import se.spaced.server.persistence.dao.interfaces.SpellDao;
import se.spaced.server.player.PlayerCreationException;
import se.spaced.server.player.PlayerService;
import se.spaced.server.spell.SpellService;
import se.spaced.shared.model.Gender;

import java.util.Collection;

public class BotAccountPopulator implements Migrator {

	public static final String BOT_NAME = "anna";
	public static final int NUMBER_OF_BOTS = 200;

	private final TransactionManager transactionManager;
	private final AccountService accountService;
	private final PlayerService playerService;
	private final SpellService spellService;
	private final AccountDao accountDao;
	private final FactionDao factionDao;
	private final SpellDao spellDao;
	private final CreatureTypeDao creatureTypeDao;
	private final Digester digester = new Digester("beefcake");

	static final Logger log = LoggerFactory.getLogger(BotAccountPopulator.class);

	@Inject
	public BotAccountPopulator(
			TransactionManager transactionManager, AccountService accountService,
			PlayerService playerService, SpellService spellService,
			AccountDao accountDao, FactionDao factionDao, SpellDao spellDao, CreatureTypeDao creatureTypeDao) {
		this.transactionManager = transactionManager;
		this.accountService = accountService;
		this.playerService = playerService;
		this.spellService = spellService;
		this.accountDao = accountDao;
		this.factionDao = factionDao;
		this.spellDao = spellDao;
		this.creatureTypeDao = creatureTypeDao;
	}

	@Override
	public void execute() {
		byte[] bytes = digester.md5("DummyAccount");
		UUID dummyUuid = UUID.nameUUIDFromBytes(bytes);
		try {
			Transaction transaction = transactionManager.beginTransaction();
			Account botAccount = accountDao.findByPk(dummyUuid);
			if (botAccount != null) {
				log.info("Bot Accounts already persisted");
				transaction.commit();
				return;
			}

			PersistedFaction faction = factionDao.findByName("bots");

			log.info("Starting to persist bot accounts");
			PersistedCreatureType creatureType = creatureTypeDao.findByName("humanoid");


			Collection<ServerSpell> spellCollection = Lists.newArrayList();
			spellCollection.add(spellDao.findByName("Lazor blast"));
			spellCollection.add(spellDao.findByName("Strike"));
			spellCollection.add(spellDao.findByName("Overcharge"));
			spellCollection.add(spellDao.findByName("Plasma ball"));
			spellCollection.add(spellDao.findByName("Volatile combustion"));
			spellCollection.add(spellDao.findByName("Disrupt"));
			spellCollection.add(spellDao.findByName("Recharge"));

			transaction.commit();


			Account dummy = accountService.createAccount(dummyUuid, AccountType.REGULAR);
			accountService.bindCharacterToAccount(dummy,
					createPlayerHelper(playerService, "Dummyuser", creatureType, spellCollection, faction));

			addBots(creatureType, spellCollection, faction);
			log.info("Done adding all bot accounts");
		} catch (DuplicateObjectException e) {
			throw new RuntimeException(e);
		}
	}


	private void addBots(
			PersistedCreatureType creatureType,
			Collection<ServerSpell> spellCollection,
			PersistedFaction faction) throws DuplicateObjectException {
		for (int i = 1; i <= NUMBER_OF_BOTS; i++) {
			String s = toBaseAZ(i);
			String name = BOT_NAME + s;

			byte[] bytes = digester.md5(name);
			Account testAccount = accountService.createAccount(UUID.nameUUIDFromBytes(bytes), AccountType.REGULAR);
			accountService.bindCharacterToAccount(
					testAccount, createPlayerHelper(playerService, name, creatureType, spellCollection, faction));
		}
	}

	private String toBaseAZ(int n) {
		String tmp = Integer.toString(n, 26).toLowerCase();
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < tmp.length(); i++) {
			char c = tmp.charAt(i);
			if (c >= '0' && c <= '9') {
				c = (char) (c + 'a' - '0');
			} else if (c >= 'a' && c <= 'z') {
				c = (char) (c + 10);
			}
			buffer.append(c);
		}
		return buffer.toString();
	}

	private void addSpellsForPlayer(Player player, Collection<ServerSpell> spellCollection) {
		for (ServerSpell spell : spellCollection) {
			spellService.addSpellForEntity(player, spell);
		}
		playerService.updatePlayer(player);
	}

	private Player createPlayerHelper(
			PlayerService playerService, String name, PersistedCreatureType creatureType,
			Collection<ServerSpell> spellCollection, PersistedFaction faction) {
		try {
			Player player = playerService.createPlayerCharacter(name, Gender.NONE, creatureType, faction,
					PlayerType.REGULAR);
			playerService.updatePlayer(player);
			spellService.createSpellBookForEntity(player);
			addSpellsForPlayer(player, spellCollection);
			return player;
		} catch (DuplicateObjectException | PlayerCreationException e) {
			throw new RuntimeException(e);
		}
	}

}