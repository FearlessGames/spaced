package se.spaced.server.persistence.dao.impl.hibernate;

import com.google.common.collect.Lists;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.junit.Before;
import org.junit.Test;
import se.ardortech.math.SpacedVector3;
import se.fearless.common.time.SystemTimeProvider;
import se.fearless.common.time.TimeProvider;
import se.fearless.common.uuid.UUID;
import se.fearless.common.uuid.UUIDFactory;
import se.fearless.common.uuid.UUIDFactoryImpl;
import se.mockachino.annotations.Mock;
import se.spaced.messages.protocol.s2c.S2CProtocol;
import se.spaced.server.mob.brains.CompositeMobBrain;
import se.spaced.server.mob.brains.MobBrain;
import se.spaced.server.mob.brains.PatrollingMobBrain;
import se.spaced.server.mob.brains.RoamingBrain;
import se.spaced.server.mob.brains.templates.BrainTemplate;
import se.spaced.server.mob.brains.templates.CompositeBrainTemplate;
import se.spaced.server.mob.brains.templates.PatrollingBrainTemplate;
import se.spaced.server.mob.brains.templates.RoamingBrainTemplate;
import se.spaced.server.model.action.ActionScheduler;
import se.spaced.server.model.combat.SpellCombatService;
import se.spaced.server.model.entity.VisibilityService;
import se.spaced.server.model.spawn.BrainParameterProviderAdapter;
import se.spaced.server.model.spawn.area.SpawnArea;
import se.spaced.server.net.broadcast.SmrtBroadcaster;
import se.spaced.server.net.broadcast.SmrtBroadcasterImpl;
import se.spaced.server.persistence.dao.interfaces.BrainTemplateDao;
import se.spaced.server.persistence.dao.interfaces.CreatureTypeDao;
import se.spaced.server.persistence.migrator.Migrator;
import se.spaced.server.persistence.migrator.MockCreatureTypePopulator;
import se.spaced.server.persistence.migrator.MockFactionPopulator;
import se.spaced.server.persistence.migrator.MockSpellPopulator;
import se.spaced.shared.world.area.Path;

import java.util.Collection;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.*;
import static se.mockachino.Mockachino.mock;
import static se.mockachino.Mockachino.setupMocks;


public class BrainTemplateHibernateTest extends PersistentTestBase {
	CreatureTypeDao creatureTypeDao;
	private BrainTemplateDao brainTemplateDao;
	private TimeProvider timeProvider = new SystemTimeProvider();
	private SpellCombatService spellCombatService;

	@Mock
	private VisibilityService visibilityService;
	private List<SpacedVector3> mysteriousPath;


	public BrainTemplateHibernateTest() {
		dbType = DbType.H2;
	}

	@Before
	public void setUp() throws Exception {
		setupMocks(this);

		spellCombatService = mock(SpellCombatService.class);
		ActionScheduler actionScheduler = mock(ActionScheduler.class);
		SmrtBroadcaster<S2CProtocol> smrtBroadcaster = mock(SmrtBroadcasterImpl.class);

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
				null, uuidFactory));

		daoFactory.runMigrators(migs);
		creatureTypeDao = daoFactory.getCreatureTypeDao();
		brainTemplateDao = daoFactory.getBrainTemplateDao();


		Transaction transaction = transactionManager.beginTransaction();

		transaction.commit();


		mysteriousPath = Lists.newArrayList(SpacedVector3.ZERO,
				new SpacedVector3(20, 0, 0),
				new SpacedVector3(20, 0, 20),
				new SpacedVector3(0, 0, 20));
	}

	@Override
	protected void addAnnotatedClasses(Configuration config) {
		config.addAnnotatedClass(BrainTemplate.class);
	}


	@Test
	public void savePatrollingBrainTemplate() {
		Transaction transaction = transactionManager.beginTransaction();
		PatrollingBrainTemplate patrollingBrainTemplate = PatrollingBrainTemplate.create(null,
				uuidFactory.combUUID()
		);
		brainTemplateDao.persist(patrollingBrainTemplate);
		transaction.commit();
	}

	@Test
	public void saveRoamingBrainTemplate() {
		Transaction transaction = transactionManager.beginTransaction();

		RoamingBrainTemplate roamingBrainTemplate = RoamingBrainTemplate.create(null,
				uuidFactory.combUUID());
		brainTemplateDao.persist(roamingBrainTemplate);
		transaction.commit();
	}

	@Test
	public void loadPatrollingBrainTemplate() {
		savePatrollingBrainTemplate();
		Transaction transaction = transactionManager.beginTransaction();
		List<BrainTemplate> brainList = brainTemplateDao.findAll();
		assertEquals(1, brainList.size());


		MobBrain mobBrain = brainList.iterator().next().createBrain(null,
				mock(SpawnArea.class),
				new BrainParameterProviderAdapter() {
					@Override
					public Path getPatrolPath() {
						return new Path(mysteriousPath);
					}
				});
		assertTrue("mobBrain is not of class PatrollingMobBrain", mobBrain instanceof PatrollingMobBrain);

		PatrollingMobBrain patrollingMobBrain = (PatrollingMobBrain) mobBrain;
		assertNotNull("Path is null", patrollingMobBrain.getPatrolPathIterator());
		assertTrue("Path is empty", patrollingMobBrain.getPatrolPathIterator().hasNext());
		transaction.commit();
	}

	@Test
	public void loadRoamingBrainTemplate() {
		saveRoamingBrainTemplate();
		Transaction transaction = transactionManager.beginTransaction();

		List<BrainTemplate> brainList = brainTemplateDao.findAll();
		assertEquals(1, brainList.size());

		MobBrain mobBrain = brainList.iterator().next().createBrain(null,
				mock(SpawnArea.class),
				new BrainParameterProviderAdapter());
		assertTrue("mobBrain is not of class RoamingBrain", mobBrain instanceof RoamingBrain);

		RoamingBrain roamingBrain = (RoamingBrain) mobBrain;
		assertNotNull("Area is null", roamingBrain.getArea());
		assertNotNull("Time intervall is null", roamingBrain.getTimePauseAtPoints());

		transaction.commit();
	}


	private UUID saveCompositeBrain() {
		Transaction transaction = transactionManager.beginTransaction();
		List<SpacedVector3> mysteriousPath = Lists.newArrayList(SpacedVector3.ZERO,
				new SpacedVector3(20, 0, 0),
				new SpacedVector3(20, 0, 20),
				new SpacedVector3(0, 0, 20));
		BrainTemplate patrollingBrain = PatrollingBrainTemplate.create(null,
				uuidFactory.combUUID()
		);
		BrainTemplate roamingBrain = RoamingBrainTemplate.create(
				null, uuidFactory.combUUID());
		BrainTemplate compositeBrain = CompositeBrainTemplate.create(uuidFactory.combUUID(),
				patrollingBrain,
				roamingBrain);

		brainTemplateDao.persist(compositeBrain);
		transaction.commit();
		return compositeBrain.getPk();
	}


	@Test
	public void testLoadCompositeMobBrain() {
		UUID compositeBrainUUID = saveCompositeBrain();

		Transaction transaction = transactionManager.beginTransaction();

		BrainTemplate template = brainTemplateDao.findByPk(compositeBrainUUID);

		CompositeBrainTemplate compositeBrainTemplate = (CompositeBrainTemplate) template;

		MobBrain mobBrain = compositeBrainTemplate.createBrain(null,
				mock(SpawnArea.class),
				new BrainParameterProviderAdapter() {
					@Override
					public Path getPatrolPath() {
						return new Path(mysteriousPath);
					}
				});
		assertTrue("mobBrain is not of class CompositeMobBrain", mobBrain instanceof CompositeMobBrain);
		CompositeMobBrain compositeMobBrain = (CompositeMobBrain) mobBrain;

		MobBrain brain = compositeMobBrain.getBrains().get(0);
		assertTrue(brain instanceof PatrollingMobBrain);
		PatrollingMobBrain patrollingMobBrain = (PatrollingMobBrain) brain;
		assertNotNull("Path is null", patrollingMobBrain.getPatrolPathIterator());
		assertTrue("Path is empty", patrollingMobBrain.getPatrolPathIterator().hasNext());


		brain = compositeMobBrain.getBrains().get(1);
		assertTrue(brain instanceof RoamingBrain);
		RoamingBrain roamingBrain = (RoamingBrain) brain;
		assertNotNull("Area is null", roamingBrain.getArea());
		assertNotNull("Time intervall is null", roamingBrain.getTimePauseAtPoints());

		transaction.commit();
	}


}
