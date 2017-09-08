package se.spaced.server.player;

import com.google.common.collect.Lists;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.junit.Before;
import org.junit.Test;
import se.ardortech.math.SpacedVector3;
import se.fearless.common.uuid.UUIDFactory;
import se.fearless.common.uuid.UUIDFactoryImpl;
import se.spaced.messages.protocol.s2c.S2CProtocol;
import se.spaced.server.model.PersistedCreatureType;
import se.spaced.server.model.PersistedFaction;
import se.spaced.server.model.Player;
import se.spaced.server.model.PlayerType;
import se.spaced.server.model.action.ActionScheduler;
import se.spaced.server.model.combat.SpellCombatService;
import se.spaced.server.model.entity.AppearanceService;
import se.spaced.server.model.entity.EntityService;
import se.spaced.server.model.entity.VisibilityService;
import se.spaced.server.model.entity.VisibilityServiceImpl;
import se.spaced.server.net.broadcast.SmrtBroadcaster;
import se.spaced.server.net.broadcast.SmrtBroadcasterImpl;
import se.spaced.server.persistence.DuplicateObjectException;
import se.spaced.server.persistence.dao.impl.hibernate.PersistentTestBase;
import se.spaced.server.persistence.migrator.Migrator;
import se.spaced.server.persistence.migrator.MockCreatureTypePopulator;
import se.spaced.server.persistence.migrator.MockFactionPopulator;
import se.spaced.server.persistence.migrator.MockSpellPopulator;
import se.spaced.shared.model.Gender;
import se.spaced.shared.util.ListenerDispatcher;

import java.util.Collection;
import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static se.mockachino.Mockachino.mock;
import static se.mockachino.Mockachino.setupMocks;


public class PlayerServiceImplTest extends PersistentTestBase {
	private PlayerService playerService;
	private String name;
	private PersistedCreatureType creatureType;
	private PersistedFaction faction;
	private Player player;
	private SpellCombatService spellCombatService;

	private VisibilityService visibilityService;

	@Before
	public void setUp() throws Exception {
		setupMocks(this);

		spellCombatService = mock(SpellCombatService.class);
		SmrtBroadcaster<S2CProtocol> smrtBroadcaster = mock(SmrtBroadcasterImpl.class);
		visibilityService = new VisibilityServiceImpl(mock(EntityService.class), ListenerDispatcher.create(AppearanceService.class));
		ActionScheduler actionScheduler = mock(ActionScheduler.class);
		Collection<Migrator> migs = Lists.newArrayList();
		migs.add(new MockCreatureTypePopulator(transactionManager, daoFactory.getCreatureTypeDao()));
		migs.add(new MockFactionPopulator(transactionManager, daoFactory.getFactionDao()));
		UUIDFactory uuidFactory = new UUIDFactoryImpl(timeProvider, new Random());
		migs.add(new MockSpellPopulator(transactionManager,
				daoFactory.getSpellDao(),
				spellCombatService,
				actionScheduler,
				smrtBroadcaster,
				projectileIdCounter,
				null,
				uuidFactory));
		daoFactory.runMigrators(migs);

		playerService = transactionProxyWrapper.wrap(new PlayerServiceImpl(timeProvider,
				daoFactory.getPlayerDao(), transactionManager));
		name = "foobar";
		Transaction transaction = transactionManager.beginTransaction();
		creatureType = daoFactory.getCreatureTypeDao().findByName("humanoid");
		faction = daoFactory.getFactionDao().findByName("players");
		transaction.commit();
		player = playerService.createPlayerCharacter(name, Gender.NONE, creatureType, faction, PlayerType.REGULAR);

	}

	@Test
	public void playerCreation() throws Exception {
		assertNotNull("No player was created", player);
		assertEquals(name, player.getName());
		assertEquals(creatureType, player.getCreatureType());
	}


	@Test
	public void playerLookup() throws Exception {
		Player lookedUpPlayer = playerService.getPlayer(name);
		assertEquals(player, lookedUpPlayer);
	}

	@Test
	public void createPlayerWithNameClash() {
		try {
			playerService.createPlayerCharacter(name, Gender.NONE, creatureType, faction, PlayerType.REGULAR);
			fail("Was able to create a player with a clashing name");
		} catch (DuplicateObjectException e) {
			// Expected scenario
		} catch (PlayerCreationException e) {
			fail("Not here :(");
		}
	}

	@Test
	public void playerNameWithDifferentCaseIsStillDuplicate() {
		try {
			playerService.createPlayerCharacter("Name", Gender.NONE, creatureType, faction, PlayerType.REGULAR);
			playerService.createPlayerCharacter("name", Gender.NONE, creatureType, faction, PlayerType.REGULAR);
			fail("Was able to create a player with different first letter case.");
		} catch (DuplicateObjectException e) {
			// Expected scenario
		} catch (PlayerCreationException e) {
			fail("Not here :(");
		}
	}

	@Test
	public void createPlayerWithTooShortName() {
		try {
			playerService.createPlayerCharacter("A", Gender.NONE, creatureType, faction, PlayerType.REGULAR);
			fail("Was able to create a player with too short name");
		} catch (DuplicateObjectException e) {
			fail("Not here :(");
		} catch (PlayerCreationException e) {
			//expected 
		}
	}

	@Test
	public void createPlayerWithMaxLegalLengthName() {
		try {
			playerService.createPlayerCharacter("Superlongnameislongy", Gender.NONE, creatureType, faction,
					PlayerType.REGULAR)	;
		} catch (DuplicateObjectException e) {
			fail("Not here :(");
		} catch (PlayerCreationException e) {
			fail("Not here :(");
		}
	}

	@Test
	public void createPlayerWithTooLongName() {
		try {
			playerService.createPlayerCharacter("Superlongnameislongyy", Gender.NONE, creatureType, faction,
					PlayerType.REGULAR);
			fail("Was able to create a player with a clashing name");
		} catch (DuplicateObjectException e) {
			fail("Not here :(");
		} catch (PlayerCreationException e) {
			//expected
		}
	}

	@Test
	public void createPlayerWithIllegalCharInName() {
		try {
			playerService.createPlayerCharacter("33-",
					Gender.NONE,
					creatureType,
					faction,
					PlayerType.REGULAR); //Illegal char
			fail("Was able to create a player with a clashing name");
		} catch (DuplicateObjectException e) {
			fail("Not here :(");
		} catch (PlayerCreationException e) {
			//expected
		}
	}

	@Test
	public void createPlayerNameWithSpace() {
		try {
			playerService.createPlayerCharacter("Ab c", Gender.NONE, creatureType, faction, PlayerType.REGULAR);
			fail("Was able to create a player name with space.");
		} catch (DuplicateObjectException e) {
			fail("Not here :(");
		} catch (PlayerCreationException e) {
			//expected
		}
	}

	@Test
	public void createPlayerNameWithNumber() {
		try {
			playerService.createPlayerCharacter("Ab3", Gender.NONE, creatureType, faction, PlayerType.REGULAR);
			fail("Was able to create a player name with space.");
		} catch (DuplicateObjectException e) {
			fail("Not here :(");
		} catch (PlayerCreationException e) {
			//expected
		}
	}

	@Test
	public void createPlayerNameWithAlternatingCase() {
		try {
			playerService.createPlayerCharacter("AbC", Gender.NONE, creatureType, faction, PlayerType.REGULAR);
			fail("Was able to create a player name with alternating case.");
		} catch (DuplicateObjectException e) {
			fail("Not here :(");
		} catch (PlayerCreationException e) {
			//expected
		}
	}

	@Test
	public void createGMPlayer() throws PlayerCreationException {
		Player gm = playerService.createPlayerCharacter("Playergm", Gender.NONE, creatureType, faction, PlayerType.GM);
		assertTrue(gm.isGm());
	}

	@Test
	public void updatePlayer() {
		player.getPositionalData().setPosition(player.getPosition().add(new SpacedVector3(0, 1, 0)));
		playerService.updatePlayer(player);
	}

	@Test
	public void getPlayer() {

		Player newPlayer = playerService.getPlayer(player.getName());
		assertEquals(newPlayer.getPk(), player.getPk());

	}

	@Override
	protected void addAnnotatedClasses(Configuration config) {

	}
}
