package se.spaced.server.model.spawn;

import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.fearless.common.time.TimeProvider;
import se.fearless.common.uuid.UUID;
import se.spaced.messages.protocol.s2c.S2CProtocol;
import se.spaced.server.mob.MobOrderExecutor;
import se.spaced.server.mob.brains.GMPuppeteerBrain;
import se.spaced.server.mob.brains.MobBrain;
import se.spaced.server.model.Mob;
import se.spaced.server.model.PersistedPositionalData;
import se.spaced.server.model.ServerEntity;
import se.spaced.server.model.action.ActionScheduler;
import se.spaced.server.model.entity.EntityService;
import se.spaced.server.model.spawn.area.SpawnArea;
import se.spaced.server.model.spawn.area.SpawnPoint;
import se.spaced.server.model.spawn.schedule.SpawnSchedule;
import se.spaced.server.net.broadcast.SmrtBroadcaster;
import se.spaced.shared.util.random.RandomProvider;

import java.util.Map;

public class MobSpawn implements MobLifecycle {
	private final Logger log = LoggerFactory.getLogger(getClass());

	private final MobTemplate mobTemplate;

	private final SpawnSchedule spawnSchedule;

	private final SpawnArea area;
	private final Map<ServerEntity, SpawnPoint> spawnPointMap = Maps.newConcurrentMap();

	// Dependencies
	private final ActionScheduler scheduler;
	private final SmrtBroadcaster<S2CProtocol> smrtBroadcaster;
	private final SpawnService spawnService;
	private final EntityService entityService;
	private final TimeProvider timeProvider;
	private final RandomProvider randomProvider;
	private final MobOrderExecutor mobOrderExecutor;
	private final BrainParameterProvider brainParameterProvider;

	public MobSpawn(
			MobTemplate mobTemplate,
			SpawnSchedule schedule,
			SmrtBroadcaster<S2CProtocol> smrtBroadcaster,
			SpawnService spawnService,
			EntityService entityService,
			ActionScheduler scheduler,
			TimeProvider timeProvider,
			RandomProvider randomProvider, SpawnArea area,
			BrainParameterProvider brainParameterProvider, MobOrderExecutor mobOrderExecutor) {
		this.mobTemplate = mobTemplate;
		this.spawnSchedule = schedule;
		this.smrtBroadcaster = smrtBroadcaster;
		this.spawnService = spawnService;
		this.entityService = entityService;
		this.scheduler = scheduler;
		this.timeProvider = timeProvider;
		this.area = area;
		this.randomProvider = randomProvider;
		this.mobOrderExecutor = mobOrderExecutor;
		this.brainParameterProvider = brainParameterProvider;
	}


	@Override
	public void removeEntity(Mob victim) {
		smrtBroadcaster.create().toAll().send().entity().entityDespawned(victim);

		SpawnPoint spawnPoint = spawnPointMap.remove(victim);
		spawnPoint.removeSpawn();
		spawnService.unregisterMob(this, victim);
		entityService.removeEntity(victim);
		spawnSchedule.resetLastSpawnTime(timeProvider.now());

	}

	public void tick(long executionTime) {
		spawnSchedule.tick(this, executionTime);
	}

	public int getMobCount() {
		return area.getSpawnCount();
	}

	@Override
	public Mob doSpawn() {
		return createEntity();
	}

	private Mob createEntity() {

		UUID newEntityId = entityService.getNewEntityId();
		Mob mob = mobTemplate.createMob(timeProvider, newEntityId, randomProvider);

		SpawnPoint nextSpawnPoint = area.getNextSpawnPoint();
		nextSpawnPoint.addSpawn();
		spawnPointMap.put(mob, nextSpawnPoint);
		mob.setPositionalData(new PersistedPositionalData(nextSpawnPoint.getPosition(), nextSpawnPoint.getRotation()));

		MobBrain brain = createBrain(mob, nextSpawnPoint.getOrigin());

		mob.setEntityInteractionCapabilities(brain.getInteractionCapabilities());

		S2CProtocol smrtReceiver = brain.getSmrtReceiver();

		entityService.addEntity(mob, smrtReceiver);

		log.debug("Created a new entity: {}  - {} of entities ", mob, area.getSpawnCount());
		spawnService.registerMob(this, mob, brain);
		return mob;
	}

	private MobBrain createBrain(Mob mob, SpawnArea origin) {
		MobBrain mobBrain = mobTemplate.getBrainTemplate().createBrain(mob, origin, brainParameterProvider);

		return new GMPuppeteerBrain(mobBrain, mobOrderExecutor);
	}


	public void start(long now) {
		spawnSchedule.tick(this, now);
	}

	@Override
	public void notifyMobDeath(long now, Mob victim) {
		scheduler.add(new DespawnAction(this, victim, SpawnPattern.DEFAULT_DECAY_TIME, now, scheduler));
	}
}
