package se.spaced.server.mob;

import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;
import se.ardortech.math.SpacedRotation;
import se.ardortech.math.SpacedVector3;
import se.spaced.server.ScenarioTestBase;
import se.spaced.server.mob.brains.PatrollingMobBrain;
import se.spaced.server.model.Mob;
import se.spaced.server.model.PersistedCreatureType;
import se.spaced.server.model.PersistedPositionalData;
import se.spaced.server.model.movement.TransportationMode;
import se.spaced.server.model.spawn.MobTemplate;

import java.util.List;

import static se.mockachino.Mockachino.*;

public class PatrollingMobBrainTest extends ScenarioTestBase {
	private PatrollingMobBrain patrollingMobBrain;
	private Mob mob;
	private MobOrderExecutor orderExecutor;

	@Before
	public void setUp() {
		List<SpacedVector3> path = Lists.newArrayList(new SpacedVector3(0, 0, 0), new SpacedVector3(10, 10, 10));
		PersistedCreatureType creatureType = new PersistedCreatureType(uuidFactory.combUUID(), "humanoid");
		MobTemplate mobTemplate = new MobTemplate.Builder(uuidFactory.randomUUID(), "grunt").walkSpeed(3.0).runSpeed(5.0).build();
		mob = mobTemplate.createMob(timeProvider, uuidFactory.randomUUID(), randomProvider);
		orderExecutor = mock(MobOrderExecutor.class);
		patrollingMobBrain = new PatrollingMobBrain(mob, path, orderExecutor);
	}

	@Test
	public void testSimpleAct() {
		mob.setPositionalData(new PersistedPositionalData(SpacedVector3.ZERO, SpacedRotation.IDENTITY));
		patrollingMobBrain.act(0);
		verifyOnce().on(orderExecutor).moveTo(mob, new SpacedVector3(10, 10, 10), TransportationMode.WALK);
	}

	@Test
	public void testActInMultipleSteps() {
		mob.setPositionalData(new PersistedPositionalData(SpacedVector3.ZERO, SpacedRotation.IDENTITY));

		patrollingMobBrain.act(0);
		verifyOnce().on(orderExecutor).moveTo(mob, new SpacedVector3(10, 10, 10), TransportationMode.WALK);
		patrollingMobBrain.act(10);

		verifyExactly(2).on(orderExecutor).moveTo(mob, new SpacedVector3(10, 10, 10),TransportationMode.WALK);

		mob.setPositionalData(new PersistedPositionalData(new SpacedVector3(10, 10, 10), SpacedRotation.IDENTITY));

		patrollingMobBrain.act(20);
		verifyOnce().on(orderExecutor).moveTo(mob, SpacedVector3.ZERO, TransportationMode.WALK);

		mob.setPositionalData(new PersistedPositionalData(SpacedVector3.ZERO, SpacedRotation.IDENTITY));

		patrollingMobBrain.act(30);
		verifyExactly(3).on(orderExecutor).moveTo(mob, new SpacedVector3(10, 10, 10), TransportationMode.WALK);
	}
}
