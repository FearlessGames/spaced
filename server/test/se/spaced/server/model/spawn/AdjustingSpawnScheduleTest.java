package se.spaced.server.model.spawn;

import org.junit.Test;
import se.ardortech.math.SpacedRotation;
import se.ardortech.math.SpacedVector3;
import se.mockachino.*;
import se.spaced.server.ScenarioTestBase;
import se.spaced.server.loot.LootAwardService;
import se.spaced.server.mob.MobOrderExecutor;
import se.spaced.server.model.Mob;
import se.spaced.server.model.PersistedCreatureType;
import se.spaced.server.model.ServerEntity;
import se.spaced.server.model.spawn.area.SinglePointSpawnArea;
import se.spaced.server.model.spawn.area.SpawnArea;
import se.spaced.server.model.spawn.schedule.SpawnSchedule;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;
import static se.mockachino.Mockachino.*;



public class AdjustingSpawnScheduleTest extends ScenarioTestBase {

	@Test
	public void testZeroToOnePattern() {
		MobSpawn mobSpawn = mock(MobSpawn.class);
		SpawnSchedule schedule = new SpawnSchedule(0, 1, 1000, actionScheduler);
		final AtomicInteger counter = new AtomicInteger();
		stubAnswer(new CallHandler() {
			@Override
			public Object invoke(Object obj, MethodCall call) throws Throwable {
				return counter.get();
			}
		}).on(mobSpawn).getMobCount();

		stubAnswer(new CallHandler() {
			@Override
			public Object invoke(Object obj, MethodCall call) throws Throwable {
				counter.incrementAndGet();
				return null;
			}
		}).on(mobSpawn).doSpawn();

		schedule.tick(mobSpawn, timeProvider.now());
		verifyNever().on(mobSpawn).doSpawn();

		actionScheduler.tick(timeProvider.now());
		verifyExactly(0).on(mobSpawn).doSpawn();

		timeProvider.advanceTime(100000);
		actionScheduler.tick(timeProvider.now());
		verifyExactly(1).on(mobSpawn).doSpawn();
	}

	@Test
	public void testOneToOnePattern() {
		MobSpawn mobSpawn = mock(MobSpawn.class);
		SpawnSchedule schedule = new SpawnSchedule(1, 1, 1000, actionScheduler);
		final AtomicInteger counter = new AtomicInteger();
		stubAnswer(new CallHandler() {
			@Override
			public Object invoke(Object obj, MethodCall call) throws Throwable {
				return counter.get();
			}
		}).on(mobSpawn).getMobCount();

		stubAnswer(new CallHandler() {
			@Override
			public Object invoke(Object obj, MethodCall call) throws Throwable {
				counter.incrementAndGet();
				return null;
			}
		}).on(mobSpawn).doSpawn();

		schedule.tick(mobSpawn, timeProvider.now());
		verifyExactly(1).on(mobSpawn).doSpawn();

		actionScheduler.tick(timeProvider.now());
		verifyExactly(1).on(mobSpawn).doSpawn();

		timeProvider.advanceTime(100000);
		actionScheduler.tick(timeProvider.now());
		verifyExactly(1).on(mobSpawn).doSpawn();
	}

	@Test
	public void testZeroToOneWithRespawn() {
		SpawnSchedule schedule = new SpawnSchedule(0, 1, 1000, actionScheduler);
		MobTemplate mobTemplate = new MobTemplate.Builder(uuidFactory.combUUID(), "Foo").build();
		MobSpawn mobSpawn = new MobSpawn(mobTemplate, schedule, smrtBroadcaster, mock(
				SpawnService.class),
				entityService,
				actionScheduler,
				timeProvider,
				randomProvider,
				new SinglePointSpawnArea(SpacedVector3.ZERO, SpacedRotation.IDENTITY), mock(BrainParameterProvider.class),
				mock(MobOrderExecutor.class));


		schedule.tick(mobSpawn, timeProvider.now());
		timeProvider.advanceTime(1001);

		actionScheduler.tick(timeProvider.now());
		assertEquals(1, mobSpawn.getMobCount());
		Collection<ServerEntity> allEntities = entityService.getAllEntities(null);
		assertEquals(1, allEntities.size());

		mobSpawn.notifyMobDeath(timeProvider.now(), (Mob) allEntities.iterator().next());
		timeProvider.advanceTime(SpawnPattern.DEFAULT_DECAY_TIME + 1);
		actionScheduler.tick(timeProvider.now());
		assertEquals(0, mobSpawn.getMobCount());
	}

	@Test
	public void scenarioTest() {

		SpawnArea area = new SinglePointSpawnArea(SpacedVector3.ZERO, SpacedRotation.IDENTITY);

		MobTemplate template = new MobTemplate.Builder(uuidFactory.randomUUID(),
				"trogdor").creatureType(new PersistedCreatureType(uuidFactory.combUUID(), "dragon")).stamina(10).build();

		SpawnSchedule schedule = new SpawnSchedule(1, 1, 1000, actionScheduler);
		LootAwardService lootAwardService = mock(LootAwardService.class);
		SpawnService spawnService = new SpawnServiceImpl(smrtBroadcaster,
				timeProvider,
				spawnListenerDispatcher
		);

		MobSpawn spawn = new MobSpawn(template,
				schedule,
				smrtBroadcaster,
				spawnService,
				entityService,
				actionScheduler,
				timeProvider,
				randomProvider,
				area, new BrainParameterProviderAdapter(), mock(MobOrderExecutor.class));

		spawn.start(timeProvider.now());

		Collection<ServerEntity> allEntities = entityService.getAllEntities(null);
		assertEquals(1, allEntities.size());
		ServerEntity victim = allEntities.iterator().next();

		actionScheduler.tick(timeProvider.now());

		assertEquals(1, entityService.getAllEntities(null).size());

		timeProvider.advanceTime(10000);
		actionScheduler.tick(timeProvider.now());
		spawn.notifyMobDeath(timeProvider.now(), (Mob) victim);

		timeProvider.advanceTime(SpawnPattern.DEFAULT_DECAY_TIME);
		actionScheduler.tick(timeProvider.now());

		assertEquals(1, entityService.getAllEntities(null).size());


		// TODO: add verification of those messages in smrt

		//List<SpacedMessage> messages = broadcaster.getSentMessages();
		//assertEquals(messages.get(0).getClass(), EntityAppearedMessage.class);
		//assertEquals(messages.get(1).getClass(), DespawnMessage.class);
		//assertEquals(messages.get(2).getClass(), EntityAppearedMessage.class);
	}
}
