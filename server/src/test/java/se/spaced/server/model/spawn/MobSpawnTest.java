package se.spaced.server.model.spawn;

import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;
import se.ardortech.math.SpacedRotation;
import se.ardortech.math.SpacedVector3;
import se.fearlessgames.common.util.MockTimeProvider;
import se.fearlessgames.common.util.TimeProvider;
import se.fearlessgames.common.util.uuid.UUIDFactory;
import se.fearlessgames.common.util.uuid.UUIDMockFactory;
import se.mockachino.matchers.*;
import se.mockachino.matchers.matcher.*;
import se.spaced.server.mob.MobOrderExecutor;
import se.spaced.server.mob.brains.GMPuppeteerBrain;
import se.spaced.server.mob.brains.MobBrain;
import se.spaced.server.mob.brains.RoamingBrain;
import se.spaced.server.mob.brains.templates.RoamingBrainTemplate;
import se.spaced.server.model.Mob;
import se.spaced.server.model.action.ActionScheduler;
import se.spaced.server.model.combat.CombatRepositoryImpl;
import se.spaced.server.model.entity.EntityServiceImpl;
import se.spaced.server.model.entity.EntityServiceListener;
import se.spaced.server.model.entity.VisibilityService;
import se.spaced.server.model.spawn.area.CompositeSpawnArea;
import se.spaced.server.model.spawn.area.SinglePointSpawnArea;
import se.spaced.server.model.spawn.area.SpawnArea;
import se.spaced.server.model.spawn.schedule.SpawnSchedule;
import se.spaced.server.net.broadcast.SmrtBroadcasterImpl;
import se.spaced.shared.util.ListenerDispatcher;
import se.spaced.shared.util.math.interval.IntervalInt;
import se.spaced.shared.util.random.RandomProvider;
import se.spaced.shared.util.random.RealRandomProvider;

import java.util.Random;

import static org.junit.Assert.assertEquals;
import static se.mockachino.Mockachino.*;
import static se.mockachino.matchers.Matchers.*;
import static se.mockachino.matchers.MatchersBase.mAny;

public class MobSpawnTest {

	private MobSpawn spawn;
	private SpawnArea area;
	private EntityServiceImpl entityService;
	private TimeProvider timeProvider;
	private UUIDFactory uuidFactory;
	private RandomProvider randomProvider;
	private SmrtBroadcasterImpl broadcaster;
	private MobTemplate.Builder mobTemplateBuilder;

	@Before
	public void setUp() throws Exception {
		uuidFactory = new UUIDMockFactory();
		mobTemplateBuilder = new MobTemplate.Builder(uuidFactory.randomUUID(), "Mob");
		timeProvider = new MockTimeProvider();
		randomProvider = new RealRandomProvider(new Random());
		area = new SinglePointSpawnArea(new SpacedVector3(10, 20, 30), new SpacedRotation(1, 2, 3, 4, true));

		entityService = new EntityServiceImpl(uuidFactory, ListenerDispatcher.create(EntityServiceListener.class));
		broadcaster = new SmrtBroadcasterImpl(entityService, new CombatRepositoryImpl(), mock(VisibilityService.class));
		MobTemplate mobTemplate = mobTemplateBuilder.build();
		spawn = new MobSpawn(mobTemplate, mock(SpawnSchedule.class), broadcaster, mock(SpawnService.class),
				entityService, new ActionScheduler(),
				timeProvider, randomProvider,
				area, new BrainParameterProviderAdapter(), mock(MobOrderExecutor.class));
	}

	@Test
	public void doSpawnIncreasesSpawnCount() throws Exception {

		assertEquals(0, spawn.getMobCount());
		spawn.doSpawn();

		assertEquals(1, spawn.getMobCount());
	}

	@Test
	public void doSpawnAddsToEntityService() throws Exception {
		assertEquals(0, entityService.getAllEntities(null).size());

		spawn.doSpawn();
		assertEquals(1, entityService.getAllEntities(null).size());
	}

	@Test
	public void spawnThenKill() throws Exception {
		Mob mob = spawn.doSpawn();

		spawn.removeEntity(mob);

		assertEquals(0, spawn.getMobCount());
	}

	@Test
	public void useSpawnOriginWhenNoRoamDefined() throws Exception {
		MobOrderExecutor mobOrderExecutor = mock(MobOrderExecutor.class);
		SpawnService spawnService = mock(SpawnService.class);
		mobTemplateBuilder.brainTemplate(new RoamingBrainTemplate(randomProvider, mobOrderExecutor));
		MobTemplate mobTemplate = mobTemplateBuilder.build();
		SpawnArea composite = new CompositeSpawnArea(new Random(), Lists.newArrayList(area));
		BrainParameterProvider brainParameterProvider = mock(BrainParameterProvider.class);
		when(brainParameterProvider.getRoamPausAtPoints()).thenReturn(new IntervalInt(0, 0));
		MobSpawn mobSpawn = new MobSpawn(mobTemplate, mock(SpawnSchedule.class), broadcaster, spawnService,
				entityService, new ActionScheduler(),
				timeProvider, randomProvider,
				composite, brainParameterProvider, mobOrderExecutor);
		Mob mob = mobSpawn.doSpawn();

		ArgumentCatcher<MobBrain> catcher = ArgumentCatcher.create(mAny(MobBrain.class));
		verifyOnce().on(spawnService).registerMob(Matchers.any(MobLifecycle.class), mob, match(catcher));

		GMPuppeteerBrain puppeteerBrain = (GMPuppeteerBrain) catcher.getValue();
		RoamingBrain brain = (RoamingBrain) puppeteerBrain.getDelegate();
		assertEquals(area, brain.getArea());


	}
}
