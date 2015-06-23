package se.spaced.server.mob;

import org.junit.Before;
import org.junit.Test;
import se.fearless.common.lifetime.LifetimeManager;
import se.fearless.common.lifetime.LifetimeManagerImpl;
import se.fearless.common.time.MockTimeProvider;
import se.fearless.common.uuid.UUID;
import se.spaced.server.ScenarioTestBase;
import se.spaced.server.mob.brains.MobBrain;
import se.spaced.server.model.Mob;
import se.spaced.server.model.spawn.MobTemplate;
import se.spaced.server.model.spawn.SpawnListener;
import se.spaced.shared.util.ListenerDispatcher;

import static se.mockachino.Mockachino.*;
import static se.mockachino.matchers.Matchers.anyLong;

public class MobControllerTest extends ScenarioTestBase {
	private MobController mobController;
	private MockTimeProvider time;

	@Before
	public void setup() {
		MobOrderExecutor executor = mock(MobOrderExecutor.class);
		time = new MockTimeProvider();
		ListenerDispatcher<SpawnListener> spawnListener = ListenerDispatcher.create(SpawnListener.class);
		LifetimeManager lifetimeManager = new LifetimeManagerImpl();
		mobController = new MobController(time, executor, spawnListener, lifetimeManager);
	}

	@Test
	public void brainsGetUpdates() {
		MobBrain mockBrain = mock(MobBrain.class);
		Mob mobA = new MobTemplate.Builder(uuidFactory.randomUUID(), "MobA").build().createMob(time, new UUID(123L, 234L),
				randomProvider);
		stubReturn(mobA).on(mockBrain).getMob();

		mobController.entitySpawned(mobA, mockBrain);
		mobController.step(0, 0);
		verifyOnce().on(mockBrain).act(anyLong());
	}

	@Test
	public void brainsAreRemoved() {
		MobBrain mockBrainA = mock(MobBrain.class);
		MobBrain mockBrainB = mock(MobBrain.class);
		Mob mobA = new MobTemplate.Builder(uuidFactory.randomUUID(), "MobA").build().createMob(time, new UUID(123L, 234L),
				randomProvider);

		stubReturn(mobA).on(mockBrainA).getMob();

		Mob mobB = new MobTemplate.Builder(uuidFactory.randomUUID(), "MobB").build().createMob(time, new UUID(123L, 345L),
				randomProvider);
		stubReturn(mobB).on(mockBrainB).getMob();

		mobController.entitySpawned(mobA, mockBrainA);
		mobController.entitySpawned(mobB, mockBrainB);

		mobController.step(0, 0);

		verifyOnce().on(mockBrainA).act(anyLong());
		verifyOnce().on(mockBrainB).act(anyLong());

		mobController.entityDespawned(mobA);
		getData(mockBrainA).resetCalls();
		getData(mockBrainB).resetCalls();

		mobController.step(0, 100);
		verifyNever().on(mockBrainA).act(anyLong());
		verifyOnce().on(mockBrainB).act(anyLong());
	}
}
