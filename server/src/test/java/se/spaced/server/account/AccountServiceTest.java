package se.spaced.server.account;

import com.google.common.collect.Lists;
import org.hibernate.Transaction;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import se.fearless.common.time.MockTimeProvider;
import se.fearless.common.time.TimeProvider;
import se.fearless.common.uuid.UUID;
import se.spaced.server.model.PersistedCreatureType;
import se.spaced.server.model.PersistedFaction;
import se.spaced.server.model.Player;
import se.spaced.server.model.PlayerType;
import se.spaced.server.persistence.DuplicateObjectException;
import se.spaced.server.persistence.ObjectNotFoundException;
import se.spaced.server.persistence.dao.impl.hibernate.PersistentTestBase;
import se.spaced.server.persistence.migrator.Migrator;
import se.spaced.server.persistence.migrator.MockCreatureTypePopulator;
import se.spaced.server.persistence.migrator.MockFactionPopulator;
import se.spaced.server.player.PlayerService;
import se.spaced.server.player.PlayerServiceImpl;
import se.spaced.shared.model.Gender;

import java.util.Collection;

import static org.junit.Assert.*;

public class AccountServiceTest extends PersistentTestBase {
	private AccountService accountService;
	private static final String user = "test0101";
	private static final String pwd = "foobar22";
	private static final String charName = "Charname";
	private PlayerService playerService;
	private TimeProvider timeProvider;
	private PersistedFaction faction;

	@Before
	public void setUp() throws Exception {
		timeProvider = new MockTimeProvider();
		Collection<Migrator> migs = Lists.newArrayList();
		migs.add(new MockCreatureTypePopulator(transactionManager, daoFactory.getCreatureTypeDao()));
		migs.add(new MockFactionPopulator(transactionManager, daoFactory.getFactionDao()));
		daoFactory.runMigrators(migs);

		accountService = transactionProxyWrapper.wrap(new AccountServiceImpl(daoFactory.getAccountDao()));

		playerService = transactionProxyWrapper.wrap(new PlayerServiceImpl(timeProvider,
				daoFactory.getPlayerDao(), transactionManager));

		accountService.deleteAll();

		faction = daoFactory.getFactionDao().findByName("players");

	}

	@After
	public void teardown() {
	}

	@Test
	public void createAndLoginAccount() throws Exception {
		UUID uuid = new UUID(3, 5);
		Account a = accountService.createAccount(uuid, AccountType.REGULAR);
		Account b = accountService.getAccount(uuid);

		assertNotNull("Didn't get account", b);
		assertEquals("Accounts don't match", a, b);
	}


	@Test
	public void loginAccountFail() throws Exception {
		accountService.createAccount(new UUID(3, 5), AccountType.REGULAR);
		try {
			accountService.getAccount(new UUID(5, 3));
			fail("Should get an exception");
		} catch (ObjectNotFoundException e) {
		}
	}


	@Test
	public void createAccountTwice() {
		Account a = null;
		try {
			a = accountService.createAccount(new UUID(3, 7), AccountType.REGULAR);
			assertNotNull("Didn't get account", a);
			accountService.createAccount(new UUID(3, 7), AccountType.REGULAR);
			fail("Didn't get exception when trying  to create duplicate account");
		} catch (DuplicateObjectException e) {
			assertNotNull(a);
		}
	}

	@Test
	public void getPlayersFromAccount() throws Exception {
		Account a = accountService.createAccount(new UUID(8, 4), AccountType.REGULAR);
		Transaction transaction = transactionManager.beginTransaction();
		PersistedCreatureType creatureType = daoFactory.getCreatureTypeDao().findByName("humanoid");
		transaction.commit();
		Player player = playerService.createPlayerCharacter(charName, Gender.NONE, creatureType, faction,
				PlayerType.REGULAR);
		accountService.bindCharacterToAccount(a, player);

		Account a2 = accountService.getAccount(new UUID(8, 4));
		assertTrue("Got no chars", a2.getPlayerCharacters().iterator().hasNext());
		assertEquals("Bad player name", charName, a2.getPlayerCharacters().iterator().next().getName());
	}

	@Test
	public void getAllAccounts() throws Exception {
		assertTrue(accountService.getAllAccounts().isEmpty());
		Transaction transaction = transactionManager.beginTransaction();
		PersistedCreatureType creatureType = daoFactory.getCreatureTypeDao().findByName("humanoid");
		transaction.commit();

		Account account = accountService.createAccount(new UUID(4, 7), AccountType.REGULAR);
		accountService.bindCharacterToAccount(account,
				playerService.createPlayerCharacter("testa", Gender.NONE, creatureType, faction, PlayerType.REGULAR));
		accountService.bindCharacterToAccount(account,
				playerService.createPlayerCharacter("testb", Gender.NONE, creatureType, faction, PlayerType.REGULAR));
		accountService.bindCharacterToAccount(account,
				playerService.createPlayerCharacter("testc", Gender.NONE, creatureType, faction, PlayerType.REGULAR));

		accountService.createAccount(new UUID(4, 0), AccountType.REGULAR);

		assertEquals(2, accountService.getAllAccounts().size());
	}


	@Test
	public void getNumberOfAccounts() throws Exception {
		assertEquals(0, accountService.getNumberOfAccounts());
		getAllAccounts();
		assertEquals(2, accountService.getNumberOfAccounts());
	}
}
