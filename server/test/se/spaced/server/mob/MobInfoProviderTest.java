package se.spaced.server.mob;

import org.junit.Before;
import org.junit.Test;
import se.ardortech.math.SpacedRotation;
import se.ardortech.math.SpacedVector3;
import se.fearlessgames.common.util.uuid.UUID;
import se.spaced.server.ScenarioTestBase;
import se.spaced.server.model.Mob;
import se.spaced.server.model.spawn.MobTemplate;
import se.spaced.server.model.spell.ServerSpell;
import se.spaced.shared.model.AnimationState;
import se.spaced.shared.playback.MovementPoint;

import static org.junit.Assert.assertEquals;

public class MobInfoProviderTest extends ScenarioTestBase {
	private MobInfoProvider infoProvider;

	@Before
	public void setUp() throws Exception {
		infoProvider = new MobInfoProvider();
	}


	@Test
	public void spellRangeCheckTooClose() {
		Mob mob = new MobTemplate.Builder(uuidFactory.randomUUID(), "mob").build().createMob(timeProvider,
				new UUID(1, 2),
				randomProvider);
		Mob target = new MobTemplate.Builder(uuidFactory.randomUUID(), "mob").build().createMob(timeProvider,
				new UUID(2, 3), randomProvider);
		visibilityService.entityAdded(mob);
		visibilityService.entityAdded(target);
		ServerSpell spell = new ServerSpell.Builder("Bam").ranges(10, 20).build();
		movementService.moveAndRotateEntity(mob,
				new MovementPoint<AnimationState>(timeProvider.now(), AnimationState.IDLE, new SpacedVector3(3, 0, 1), SpacedRotation.IDENTITY));
		movementService.moveAndRotateEntity(target,
				new MovementPoint<AnimationState>(timeProvider.now(), AnimationState.IDLE, new SpacedVector3(5, 1, 2), SpacedRotation.IDENTITY));

		SpellRange inRange = infoProvider.isInRangeForSpell(mob, target, spell);
		assertEquals(SpellRange.TOO_CLOSE, inRange);
	}

	@Test
	public void spellRangeCheckInRange() {
		Mob mob = new MobTemplate.Builder(uuidFactory.randomUUID(), "mob").build().createMob(timeProvider, new UUID(1, 2),
				randomProvider);
		Mob target = new MobTemplate.Builder(uuidFactory.randomUUID(), "mob").build().createMob(timeProvider,
				new UUID(2, 3),
				randomProvider);
		visibilityService.entityAdded(mob);
		visibilityService.entityAdded(target);
		ServerSpell spell = new ServerSpell.Builder("Bam").ranges(10, 20).build();
		movementService.moveAndRotateEntity(mob,
				new MovementPoint<AnimationState>(timeProvider.now(), AnimationState.IDLE, new SpacedVector3(3, 0, 1), SpacedRotation.IDENTITY));
		movementService.moveAndRotateEntity(target,
				new MovementPoint<AnimationState>(timeProvider.now(), AnimationState.IDLE, new SpacedVector3(15, 3, 2), SpacedRotation.IDENTITY));

		SpellRange inRange = infoProvider.isInRangeForSpell(mob, target, spell);
		assertEquals(SpellRange.IN_RANGE, inRange);
	}

	@Test
	public void spellRangeCheckFarFarAway() {
		Mob mob = new MobTemplate.Builder(uuidFactory.randomUUID(), "mob").build().createMob(timeProvider, new UUID(1, 2),
				randomProvider);
		Mob target = new MobTemplate.Builder(uuidFactory.randomUUID(), "mob").build().createMob(timeProvider,
				new UUID(2, 3),
				randomProvider);
		visibilityService.entityAdded(mob);
		visibilityService.entityAdded(target);
		ServerSpell spell = new ServerSpell.Builder("Bam").ranges(10, 20).build();
		movementService.moveAndRotateEntity(mob,
				new MovementPoint<AnimationState>(timeProvider.now(), AnimationState.IDLE, new SpacedVector3(3, 0, 1), SpacedRotation.IDENTITY));
		movementService.moveAndRotateEntity(target,
				new MovementPoint<AnimationState>(timeProvider.now(), AnimationState.IDLE, new SpacedVector3(1000, 3, 2), SpacedRotation.IDENTITY));

		SpellRange inRange = infoProvider.isInRangeForSpell(mob, target, spell);
		assertEquals(SpellRange.TOO_FAR_AWAY, inRange);
	}

}
