package se.spaced.server.persistence.dao.impl.hibernate;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.hibernate.Transaction;
import org.junit.Before;
import org.junit.Test;
import se.ardortech.math.SpacedRotation;
import se.ardortech.math.SpacedVector3;
import se.mockachino.annotations.Mock;
import se.spaced.messages.protocol.s2c.S2CProtocol;
import se.spaced.server.loot.MultiLootTemplate;
import se.spaced.server.mob.brains.templates.AttackingBrainTemplate;
import se.spaced.server.mob.brains.templates.BrainTemplate;
import se.spaced.server.model.action.ActionScheduler;
import se.spaced.server.model.combat.CombatRepository;
import se.spaced.server.model.combat.CombatRepositoryImpl;
import se.spaced.server.model.combat.EntityTargetService;
import se.spaced.server.model.combat.SpellCombatService;
import se.spaced.server.model.entity.EntityService;
import se.spaced.server.model.entity.VisibilityService;
import se.spaced.server.model.spawn.MobSpawnTemplate;
import se.spaced.server.model.spawn.MobTemplate;
import se.spaced.server.model.spawn.SpawnPatternTemplate;
import se.spaced.server.model.spawn.area.RandomSpaceSpawnArea;
import se.spaced.server.model.spawn.schedule.SpawnScheduleTemplate;
import se.spaced.server.model.spell.ServerSpell;
import se.spaced.server.net.broadcast.SmrtBroadcaster;
import se.spaced.server.net.broadcast.SmrtBroadcasterImpl;
import se.spaced.server.persistence.dao.interfaces.LootTemplateDao;
import se.spaced.server.persistence.migrator.DefaultMobTemplatePopulator;
import se.spaced.server.persistence.migrator.Migrator;
import se.spaced.server.persistence.migrator.MockCreatureTypePopulator;
import se.spaced.server.persistence.migrator.MockFactionPopulator;
import se.spaced.server.persistence.migrator.MockSpellPopulator;
import se.spaced.shared.util.random.RealRandomProvider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.assertNotNull;
import static se.mockachino.Mockachino.mock;
import static se.mockachino.Mockachino.setupMocks;


public class SpawnPatternTemplateDaoImplTest extends PersistentTestBase {
	private SpellCombatService spellCombatService;

	@Mock
	private VisibilityService visibilityService;

	@Before
	public void setUp() throws Exception {
		setupMocks(this);
		spellCombatService = mock(SpellCombatService.class);
		ActionScheduler actionScheduler = new ActionScheduler();
		CombatRepository combatRepository = new CombatRepositoryImpl();
		EntityTargetService targetService = mock(EntityTargetService.class);
		SmrtBroadcaster<S2CProtocol> smrtBroadcaster = new SmrtBroadcasterImpl(mock(EntityService.class),
				combatRepository,
				visibilityService);
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
		migs.add(new DefaultMobTemplatePopulator(transactionManager,
				daoFactory.getSpellDao(),
				daoFactory.getMobTemplateDao(),
				daoFactory.getFactionDao(),
				daoFactory.getCreatureTypeDao()
		));

		daoFactory.runMigrators(migs);

		Transaction transaction = transactionManager.beginTransaction();
		LootTemplateDao lootTemplateDao = daoFactory.getLootTemplateDao();
		lootTemplateDao.persist(new MultiLootTemplate(uuidFactory.combUUID(),
				Sets.newHashSet()));
		transaction.commit();
	}

	@Test
	public void testLoad() {
		Transaction tx = transactionManager.beginTransaction();
		Collection<MobSpawnTemplate> spawns = new ArrayList<MobSpawnTemplate>();
		MobTemplate mobTemplate = (MobTemplate) daoFactory.getMobTemplateDao().findAll().iterator().next();
		ServerSpell spell = daoFactory.getSpellDao().findAll().iterator().next();
		BrainTemplate brainTemplate = new AttackingBrainTemplate(null, null, null,
				uuidFactory.combUUID(),
				null);
		SpawnScheduleTemplate schedule = new SpawnScheduleTemplate(uuidFactory.combUUID(), 1, 10, 1000);
		spawns.add(new MobSpawnTemplate(uuidFactory.combUUID(), mobTemplate, schedule, null));
		SpawnPatternTemplate pattern = new SpawnPatternTemplate(uuidFactory.combUUID(),
				new RandomSpaceSpawnArea(uuidFactory.combUUID(),
						new SpacedVector3(100, 200, 300),
						400,
						500,
						600,
						SpacedRotation.IDENTITY,
						new RealRandomProvider(new Random())),
				spawns, "pattern");

		daoFactory.getSpawnPatternTemplateDao().persist(pattern);
		tx.commit();

		tx = transactionManager.beginTransaction();
		List<SpawnPatternTemplate> spawnPatternTemplateList = daoFactory.getSpawnPatternTemplateDao().findAll();
		assertNotNull(spawnPatternTemplateList);
		SpawnPatternTemplate spawnPatternTemplate = spawnPatternTemplateList.get(0);
		assertNotNull(spawnPatternTemplate);
		assertNotNull(spawnPatternTemplate.getArea());
		assertNotNull(spawnPatternTemplate.getArea().getNextSpawnPoint().getPosition());

		tx.commit();
	}

}
