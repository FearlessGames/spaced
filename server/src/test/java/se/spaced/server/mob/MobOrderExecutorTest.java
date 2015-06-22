package se.spaced.server.mob;

import org.junit.Test;
import se.ardortech.math.SpacedVector3;
import se.fearlessgames.common.util.uuid.UUID;
import se.spaced.server.ScenarioTestBase;
import se.spaced.server.model.Mob;
import se.spaced.server.model.spawn.MobTemplate;

import static org.junit.Assert.assertEquals;

public class MobOrderExecutorTest extends ScenarioTestBase {

	@Test
	public void testMovement() {
		Mob mob = new MobTemplate.Builder(uuidFactory.randomUUID(), "mob").walkSpeed(3.0).runSpeed(5.0).build().createMob(timeProvider, new UUID(1, 2),
				randomProvider);
		visibilityService.entityAdded(mob);
		mobOrderExecutor.walkTo(mob, new SpacedVector3(100, 0, 0));
		assertEquals(new SpacedVector3(0, 0, 0), mob.getPosition());
		tick(500);
		assertEquals(new SpacedVector3(1.5, 0, 0), mob.getPosition());
		tick(500);
		assertEquals(new SpacedVector3(3.0, 0, 0), mob.getPosition());
	}

	@Test
	public void testFastMovement() {
		Mob mob = new MobTemplate.Builder(uuidFactory.randomUUID(), "mob").walkSpeed(3.0).runSpeed(50.0).build().createMob(timeProvider, new UUID(1, 2),
				randomProvider);
		visibilityService.entityAdded(mob);
		mobOrderExecutor.runTo(mob, new SpacedVector3(100, 0, 0));
		assertEquals(new SpacedVector3(0, 0, 0), mob.getPosition());
		tick(100);
		assertEquals(new SpacedVector3(5.0, 0, 0), mob.getPosition());
		tick(100);
		assertEquals(new SpacedVector3(10.0, 0, 0), mob.getPosition());

		tick(1000);
		assertEquals(new SpacedVector3(60.0, 0, 0), mob.getPosition());
		tick(1000);
		assertEquals(new SpacedVector3(100.0, 0, 0), mob.getPosition());
	}

	@Test
	public void testMovementWithStop() {
		Mob mob = new MobTemplate.Builder(uuidFactory.randomUUID(), "mob").walkSpeed(3.0).runSpeed(5.0).build().createMob(timeProvider, new UUID(1, 2),
				randomProvider);
		visibilityService.entityAdded(mob);
		mobOrderExecutor.walkTo(mob, new SpacedVector3(100, 0, 0));
		assertEquals(new SpacedVector3(0, 0, 0), mob.getPosition());
		tick(500);
		assertEquals(new SpacedVector3(1.5, 0, 0), mob.getPosition());
		mobOrderExecutor.stopWalking(mob);
		tick(500);
		assertEquals(new SpacedVector3(1.5, 0, 0), mob.getPosition());
	}

}
