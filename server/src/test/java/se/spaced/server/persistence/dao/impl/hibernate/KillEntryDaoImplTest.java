package se.spaced.server.persistence.dao.impl.hibernate;

import org.hibernate.Transaction;
import org.junit.Before;
import org.junit.Test;
import se.fearless.common.time.MockTimeProvider;
import se.fearless.common.uuid.UUID;
import se.fearless.common.uuid.UUIDFactory;
import se.fearless.common.uuid.UUIDMockFactory;
import se.spaced.server.model.PersistedCreatureType;
import se.spaced.server.model.PersistedFaction;
import se.spaced.server.model.Player;
import se.spaced.server.model.player.PlayerMockFactory;
import se.spaced.server.model.spawn.EntityTemplate;
import se.spaced.server.model.spawn.MobTemplate;
import se.spaced.server.model.spawn.PlayerTemplate;
import se.spaced.server.persistence.dao.interfaces.EntityTemplateDao;
import se.spaced.server.persistence.dao.interfaces.KillEntryDao;
import se.spaced.server.persistence.dao.interfaces.PlayerDao;
import se.spaced.server.stats.KillEntry;
import se.spaced.server.stats.KillStat;

import java.util.List;

import static org.junit.Assert.*;

public class KillEntryDaoImplTest extends PersistentTestBase {

	private UUIDFactory uuidFactory;

	private KillEntryDao killEntryDao;
	private PlayerDao playerDao;
	private EntityTemplateDao entityTemplateDao;
	private EntityTemplate p1;
	private EntityTemplate p2;
	private MobTemplate m1;
	private MobTemplate m2;
	private KillEntry p1KillsM1;
	private KillEntry m2KillsM1;


	@Before
	public void setUp() throws Exception {
		uuidFactory = new UUIDMockFactory();

		killEntryDao = new KillEntryDaoImpl(sessionFactory);
		playerDao = new PlayerDaoImpl(sessionFactory);
		entityTemplateDao = new EntityTemplateHibernateDao(sessionFactory);
	}

	@Test
	public void testFindByParticipants() throws Exception {
		setupData();
		Transaction transaction = transactionManager.beginTransaction();

		KillEntry killEntry1 = killEntryDao.findByParticipants(p1, m1);
		assertEquals(p1KillsM1, killEntry1);

		KillEntry killEntry2 = killEntryDao.findByParticipants(m2, m1);
		assertEquals(m2KillsM1, killEntry2);

		KillEntry killEntry3 = killEntryDao.findByParticipants(m2, p1);
		assertNull(killEntry3);
		transaction.commit();
	}

	private void setupData() {
		Transaction transaction = transactionManager.beginTransaction();
		PersistedCreatureType creatureType = new PersistedCreatureType(uuidFactory.combUUID(), "Foo");
		PersistedFaction faction = new PersistedFaction(uuidFactory.combUUID(), "fac");
		MockTimeProvider timeProvider = new MockTimeProvider();
		PlayerMockFactory factory = new PlayerMockFactory.Builder(timeProvider,
				PlayerMockFactory.NULL_UUID_FACTORY).creatureType(creatureType).faction(faction).build();
		Player player1 = factory.createPlayer("p1");
		playerDao.persist(player1);
		p1 = player1.getTemplate();

		Player player2 = factory.createPlayer("p2");
		playerDao.persist(player2);
		p2 = player2.getTemplate();

		transaction.commit();
		transaction = transactionManager.beginTransaction();

		m1 = new MobTemplate.Builder(uuidFactory.combUUID(), "m1").
				creatureType(creatureType).faction(faction).build();
		entityTemplateDao.persist(m1);
		m2 = new MobTemplate.Builder(uuidFactory.combUUID(), "m2").
				creatureType(creatureType).faction(faction).build();
		entityTemplateDao.persist(m2);
		transaction.commit();

		transaction = transactionManager.beginTransaction();

		// Kill m1 10 times
		p1KillsM1 = new KillEntry(p1, m1);
		for (int i = 0; i < 10; i++) {
			p1KillsM1.increaseKillCount();
		}
		killEntryDao.persist(p1KillsM1);

		// Kill m2 once
		KillEntry entry = new KillEntry(p1, m2);
		entry.increaseKillCount();
		killEntryDao.persist(entry);

		// Kill m1 once
		m2KillsM1 = new KillEntry(m2, m1);
		m2KillsM1.increaseKillCount();
		killEntryDao.persist(m2KillsM1);

		// Kill m1 once
		entry = new KillEntry(p2, m1);
		entry.increaseKillCount();
		killEntryDao.persist(entry);


		// Kill p1 twice
		KillEntry mobKillsPlayerEntry = new KillEntry(m1, p1);
		mobKillsPlayerEntry.increaseKillCount();
		mobKillsPlayerEntry.increaseKillCount();
		killEntryDao.persist(mobKillsPlayerEntry);
		transaction.commit();
	}

	@Test
	public void testDeathsForVictim() throws Exception {
		setupData();

		Transaction transaction = transactionManager.beginTransaction();
		assertEquals(2, killEntryDao.deathsForVictim(p1));
		assertEquals(12, killEntryDao.deathsForVictim(m1));

		assertEquals(0, killEntryDao.deathsForVictim(p2));

		assertEquals(0, killEntryDao.deathsForVictim(new PlayerTemplate(UUID.ZERO, "null")));

		transaction.commit();
	}

	@Test
	public void testToplistOfKilled() {
		setupData();
		Transaction transaction = transactionManager.beginTransaction();

		// Looking up the #2 and #3 of the most killed
		List<KillStat> killStat = killEntryDao.findTopKilled(1, 2);
		assertNotNull(killStat);
		assertEquals(2, killStat.size());

		assertEquals(p1, killStat.get(0).getEntity());
		assertEquals(2, killStat.get(0).getKillCount());

		assertEquals(m2, killStat.get(1).getEntity());
		assertEquals(1, killStat.get(1).getKillCount());

		transaction.commit();
	}

	@Test
	public void testToplistOfKillers() {
		setupData();
		Transaction transaction = transactionManager.beginTransaction();

		// Looking up the #2 and #3 of the most killed
		List<KillStat> killStat = killEntryDao.findTopKillers(1, 2);
		assertNotNull(killStat);
		assertEquals(2, killStat.size());

		assertEquals(m1, killStat.get(0).getEntity());
		assertEquals(2, killStat.get(0).getKillCount());

		assertEquals(p2, killStat.get(1).getEntity());
		assertEquals(1, killStat.get(1).getKillCount());

		transaction.commit();
	}

	@Test
	public void testTopKilledByEntity() throws Exception {
		setupData();
		Transaction transaction = transactionManager.beginTransaction();
		List<KillStat> killStat = killEntryDao.findTopKilledByEntity(p1, 0, 10);

		assertNotNull(killStat);
		assertEquals(2, killStat.size());

		assertEquals(m1, killStat.get(0).getEntity());
		assertEquals(10, killStat.get(0).getKillCount());

		assertEquals(m2, killStat.get(1).getEntity());
		assertEquals(1, killStat.get(1).getKillCount());

		transaction.commit();
	}

	@Test
	public void testTopEntityKilledBy() throws Exception {
		setupData();
		Transaction transaction = transactionManager.beginTransaction();
		List<KillStat> killStat = killEntryDao.findTopEntityKilledBy(m1, 0, 10);

		assertNotNull(killStat);
		assertEquals(3, killStat.size());

		assertEquals(p1, killStat.get(0).getEntity());
		assertEquals(10, killStat.get(0).getKillCount());

		assertEquals(p2, killStat.get(1).getEntity());
		assertEquals(1, killStat.get(1).getKillCount());

		assertEquals(m2, killStat.get(2).getEntity());
		assertEquals(1, killStat.get(2).getKillCount());

		transaction.commit();
	}

}
