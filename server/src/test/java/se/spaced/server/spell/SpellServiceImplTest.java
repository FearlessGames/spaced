package se.spaced.server.spell;

import com.google.common.collect.Lists;
import org.hibernate.Transaction;
import org.junit.Before;
import org.junit.Test;
import se.fearlessgames.common.util.MockTimeProvider;
import se.fearlessgames.common.util.SystemTimeProvider;
import se.fearlessgames.common.util.uuid.UUIDFactory;
import se.fearlessgames.common.util.uuid.UUIDFactoryImpl;
import se.mockachino.annotations.*;
import se.spaced.messages.protocol.s2c.S2CProtocol;
import se.spaced.server.model.PersistedCreatureType;
import se.spaced.server.model.PersistedFaction;
import se.spaced.server.model.Player;
import se.spaced.server.model.PlayerType;
import se.spaced.server.model.ServerEntity;
import se.spaced.server.model.action.ActionScheduler;
import se.spaced.server.model.combat.CombatRepository;
import se.spaced.server.model.combat.CombatRepositoryImpl;
import se.spaced.server.model.combat.SpellCombatService;
import se.spaced.server.model.entity.EntityService;
import se.spaced.server.model.entity.EntityServiceImpl;
import se.spaced.server.model.entity.EntityServiceListener;
import se.spaced.server.model.entity.VisibilityService;
import se.spaced.server.model.spawn.MobTemplate;
import se.spaced.server.model.spell.ServerSpell;
import se.spaced.server.net.broadcast.SmrtBroadcaster;
import se.spaced.server.net.broadcast.SmrtBroadcasterImpl;
import se.spaced.server.persistence.DuplicateObjectException;
import se.spaced.server.persistence.dao.impl.hibernate.PersistentTestBase;
import se.spaced.server.persistence.migrator.Migrator;
import se.spaced.server.persistence.migrator.MockCreatureTypePopulator;
import se.spaced.server.persistence.migrator.MockFactionPopulator;
import se.spaced.server.persistence.migrator.MockSpellPopulator;
import se.spaced.server.player.PlayerCreationException;
import se.spaced.server.player.PlayerService;
import se.spaced.server.player.PlayerServiceImpl;
import se.spaced.shared.model.Gender;
import se.spaced.shared.util.ListenerDispatcher;
import se.spaced.shared.util.random.RandomProvider;

import java.security.SecureRandom;
import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static se.mockachino.Mockachino.*;


public class SpellServiceImplTest extends PersistentTestBase {
	private final MockTimeProvider timeProvider = new MockTimeProvider();
	private final UUIDFactory uuidFactory = new UUIDFactoryImpl(timeProvider, new SecureRandom());

	private SpellService spellService;
	private PlayerService playerService;
	private PersistedCreatureType creatureType;
	private PersistedFaction faction;
	private SpellCombatService spellCombatService;
	private ActionScheduler actionScheduler;
	private SmrtBroadcaster<S2CProtocol> smrtBroadcaster;

	@Mock
	private VisibilityService visibilityService;
	@Mock
	private RandomProvider randomProvider;

	@Before
	public void setUp() {

		setupMocks(this);

		actionScheduler = new ActionScheduler();
		CombatRepository combatRepository = new CombatRepositoryImpl();
		EntityService entityService = new EntityServiceImpl(uuidFactory,
				ListenerDispatcher.create(EntityServiceListener.class));
		smrtBroadcaster = new SmrtBroadcasterImpl(entityService, combatRepository, visibilityService);
		spellService = transactionProxyWrapper.wrap(new SpellServiceImpl(transactionManager,
				new SystemTimeProvider(),
				daoFactory.getSpellDao(),
				daoFactory.getSpellBookDao()));
		playerService = transactionProxyWrapper.wrap(new PlayerServiceImpl(timeProvider,
				daoFactory.getPlayerDao(), transactionManager));


		spellCombatService = mock(SpellCombatService.class);
		Collection<Migrator> migs = Lists.newArrayList();
		migs.add(new MockCreatureTypePopulator(transactionManager, daoFactory.getCreatureTypeDao()));
		migs.add(new MockFactionPopulator(transactionManager, daoFactory.getFactionDao()));
		migs.add(new MockSpellPopulator(transactionManager,
				daoFactory.getSpellDao(),
				spellCombatService,
				actionScheduler,
				smrtBroadcaster,
				projectileIdCounter,
				null,
				uuidFactory));
		daoFactory.runMigrators(migs);

		Transaction transaction = transactionManager.beginTransaction();
		creatureType = daoFactory.getCreatureTypeDao().findByName("humanoid");
		faction = daoFactory.getFactionDao().findByName("players");
		transaction.commit();
	}

	@Test
	public void testGetSpellsForEntity() throws DuplicateObjectException, PlayerCreationException {
		Player entity = playerService.createPlayerCharacter("hiflyer", Gender.NONE, creatureType, faction,
				PlayerType.REGULAR);
		assertNotNull(entity.getPk());
		spellService.createSpellBookForEntity(entity);

		Transaction transaction = transactionManager.beginTransaction();
		ServerSpell spell = daoFactory.getSpellDao().findAll().iterator().next();
		assertNotNull(spell);
		transaction.commit();
		spellService.addSpellForEntity(entity, spell);

		Collection<ServerSpell> spellCollection = spellService.getSpellsForEntity(entity);
		assertEquals(1, spellCollection.size());
		ServerSpell lookedUpSpell = spellCollection.iterator().next();
		assertEquals(spell, lookedUpSpell);
		assertEquals(spell.getName(), lookedUpSpell.getName());
	}

	@Test
	public void testGetSpellsForMob() {
		MobTemplate template = new MobTemplate.Builder(uuidFactory.randomUUID(),
				"trogdor").creatureType(creatureType).faction(faction).build();
		ServerEntity mob = template.createMob(timeProvider, uuidFactory.randomUUID(), randomProvider);
		spellService.createSpellBookForEntity(mob);
		Collection<ServerSpell> spellCollection = spellService.getSpellsForEntity(mob);
		int countBefore = spellCollection.size();

		Transaction transaction = transactionManager.beginTransaction();
		ServerSpell spell = daoFactory.getSpellDao().findAll().iterator().next();
		assertNotNull(spell);
		transaction.commit();
		spellService.addSpellForEntity(mob, spell);

		spellCollection = spellService.getSpellsForEntity(mob);
		assertEquals(countBefore + 1, spellCollection.size());
		assertTrue(spellCollection.contains(spell));
	}

}
