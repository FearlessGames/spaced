package se.spaced.server.model.spawn;

import org.junit.Before;
import org.junit.Test;
import se.ardortech.math.SpacedRotation;
import se.ardortech.math.SpacedVector3;
import se.fearless.common.uuid.UUID;
import se.spaced.server.ScenarioTestBase;
import se.spaced.server.mob.MobOrderExecutor;
import se.spaced.server.model.spawn.area.SinglePointSpawnArea;
import se.spaced.server.model.spawn.schedule.SpawnSchedule;

import static se.mockachino.Mockachino.*;
import static se.mockachino.matchers.Matchers.any;


public class SpawnServiceImplTest extends ScenarioTestBase {

	private MobTemplate mobTemplate;

	@Before
	public void setUp() {
		mobTemplate = spy(new MobTemplate.Builder(uuidFactory.randomUUID(), "Foo").build());
	}

	@Test
	public void simpleSpawn() {
		MobSpawn spawn = new MobSpawn(mobTemplate,
				new SpawnSchedule(0, 1, 50000, actionScheduler),
				smrtBroadcaster,
				spawnService,
				entityService,
				actionScheduler,
				timeProvider,
				randomProvider,
				new SinglePointSpawnArea(new SpacedVector3(10, 20, 30), SpacedRotation.IDENTITY),
				mock(BrainParameterProvider.class), mock(MobOrderExecutor.class));
		spawn.start(timeProvider.now());

		tick(51 * 1000);
		verifyOnce().on(mobTemplate).createMob(timeProvider, any(UUID.class), randomProvider);
	}

}
