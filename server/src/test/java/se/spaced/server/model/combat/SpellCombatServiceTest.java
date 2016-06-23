package se.spaced.server.model.combat;

import com.google.common.collect.Sets;
import org.junit.Test;
import se.ardortech.math.SpacedRotation;
import se.ardortech.math.SpacedVector3;
import se.fearless.common.stats.ModStat;
import se.fearless.common.stats.Operator;
import se.spaced.messages.protocol.Entity;
import se.spaced.server.model.cooldown.CooldownSetTemplate;
import se.spaced.server.model.spell.ServerSpell;
import se.spaced.server.model.spell.effect.*;
import se.spaced.shared.model.AnimationState;
import se.spaced.shared.model.MagicSchool;
import se.spaced.shared.model.TargetingType;
import se.spaced.shared.model.stats.EntityStats;
import se.spaced.shared.model.stats.SpacedStatType;
import se.spaced.shared.playback.MovementPoint;
import se.spaced.shared.util.math.interval.IntervalInt;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;
import static se.mockachino.Mockachino.*;
import static se.mockachino.matchers.Matchers.*;


public class SpellCombatServiceTest extends AbstractCombatServiceTest {
	private static final double EPSILON = 0.000001;

	private final AtomicInteger projectileIdCounter = new AtomicInteger();


	@Test
	public void spellDoesntGetScheduledWithoutTarget() {
		assertFalse(entityCombatService.isInCombat(attacker));
		assertFalse(entityCombatService.isInCombat(target));

		Set<Effect> effects = new HashSet<Effect>();
		effects.add(new DamageSchoolEffect(spellCombatService,
				smrtBroadcaster,
				new IntervalInt(10, 20),
				MagicSchool.FIRE
		));
		ServerSpell spell = ServerSpell.createProjectileSpell(uuidFactory.randomUUID(),
				"Nuke",
				3000,
				MagicSchool.FIRE,
				effects,
				true,
				true,
				new IntervalInt(0, 100),
				0,
				actionScheduler,
				smrtBroadcaster,
				projectileIdCounter
		);

		spellCombatService.startSpellCast(attacker, null, spell, timeProvider.now(), null);
		assertTrue(actionScheduler.isEmpty());
	}

	@Test
	public void selfTargetSpellDoesntRequireTarget() {

		assertTrue(actionScheduler.isEmpty());
		ServerSpell spell = mock(ServerSpell.class);
		stubReturn(new IntervalInt(0, 0)).on(spell).getRanges();
		stubReturn(TargetingType.SELF_ONLY).on(spell).getTargetingType();
		when(spell.getCoolDown()).thenReturn(new CooldownSetTemplate());
		spellCombatService.startSpellCast(attacker, null, spell, timeProvider.now(), null);

		assertFalse(actionScheduler.isEmpty());
	}


	@Test
	public void spellEntersCombat() {
		assertFalse(entityCombatService.isInCombat(attacker));
		assertFalse(entityCombatService.isInCombat(target));

		int castTime = 3000;

		ServerSpell spell = ServerSpell.createDirectEffectSpell(uuidFactory.randomUUID(),
				"Nuke",
				castTime,
				MagicSchool.FIRE,
				new DamageSchoolEffect(spellCombatService,
						smrtBroadcaster,
						new IntervalInt(10, 20),
						MagicSchool.FIRE
				),
				true,
				true,
				new IntervalInt(0, 100));

		verifyNever().on(attackerReceiver.combat()).entityStartedSpellCast(eq(attacker), eq(target), eq(spell));
		verifyNever().on(target1Receiver.combat()).entityStartedSpellCast(eq(attacker), eq(target), eq(spell));

		spellCombatService.startSpellCast(attacker, target, spell, timeProvider.now(), null);
		assertFalse(entityCombatService.isInCombat(attacker));
		assertFalse(entityCombatService.isInCombat(target));

		verifyExactly(1).on(attackerReceiver.combat()).entityStartedSpellCast(eq(attacker), eq(target), eq(spell));
		verifyNever().on(attackerReceiver.combat()).entityCompletedSpellCast(eq(attacker), eq(target), eq(spell));
		verifyExactly(1).on(target1Receiver.combat()).entityStartedSpellCast(eq(attacker), eq(target), eq(spell));
		verifyNever().on(target1Receiver.combat()).entityCompletedSpellCast(eq(attacker), eq(target), eq(spell));


		tick(castTime);

		verifyExactly(1).on(attackerReceiver.combat()).combatStatusChanged(attacker, eq(true));
		verifyExactly(1).on(target1Receiver.combat()).combatStatusChanged(target, eq(true));

		verifyExactly(1).on(attackerReceiver.combat()).entityCompletedSpellCast(eq(attacker), eq(target), eq(spell));
		verifyExactly(1).on(target1Receiver.combat()).entityCompletedSpellCast(eq(attacker), eq(target), eq(spell));

		assertTrue(entityCombatService.isInCombat(attacker));
		assertTrue(entityCombatService.isInCombat(target));
		tick(EntityCombatServiceImpl.COMBAT_TIMEOUT - 1);
		assertTrue(entityCombatService.isInCombat(attacker));
		assertTrue(entityCombatService.isInCombat(target));

		verifyExactly(1).on(attackerReceiver.combat()).combatStatusChanged(attacker, eq(true));
		verifyExactly(1).on(target1Receiver.combat()).combatStatusChanged(target, eq(true));

		tick(1);
		assertFalse(entityCombatService.isInCombat(attacker));
		assertFalse(entityCombatService.isInCombat(target));
		verifyExactly(1).on(attackerReceiver.combat()).combatStatusChanged(attacker, eq(false));
		verifyExactly(1).on(target1Receiver.combat()).combatStatusChanged(target, eq(false));
	}

	@Test
	public void spellMissEntersCombat() {
		assertFalse(entityCombatService.isInCombat(attacker));
		assertFalse(entityCombatService.isInCombat(target));

		int castTime = 3000;

		ServerSpell spell = ServerSpell.createDirectEffectSpell(uuidFactory.randomUUID(),
				"Nuke",
				castTime,
				MagicSchool.FIRE,
				new DamageSchoolEffect(spellCombatService,
						smrtBroadcaster,
						new IntervalInt(10, 20),
						MagicSchool.FIRE
				),
				true,
				true,
				new IntervalInt(0, 100));

		verifyNever().on(attackerReceiver.combat()).entityStartedSpellCast(any(Entity.class),
				any(Entity.class),
				any(ServerSpell.class));
		verifyNever().on(attackerReceiver.combat()).entityCompletedSpellCast(any(Entity.class),
				any(Entity.class),
				any(ServerSpell.class));


		spellCombatService.startSpellCast(attacker, target, spell, timeProvider.now(), null);
		assertFalse(entityCombatService.isInCombat(attacker));
		assertFalse(entityCombatService.isInCombat(target));

		verifyExactly(1).on(attackerReceiver.combat()).entityStartedSpellCast(eq(attacker), eq(target), eq(spell));

		tick(castTime);

		verifyOnce().on(attackerReceiver.combat()).combatStatusChanged(attacker, eq(true));
		verifyOnce().on(target1Receiver.combat()).combatStatusChanged(target, eq(true));

		// TODO: Add these lines back when we bring back misses
//		verifyOnce().on(attackerReceiver.combat()).entityMissed(attacker, target, any(String.class), any(MagicSchool.class));
//		verifyOnce().on(target1Receiver.combat()).entityMissed(attacker, target, any(String.class), any(MagicSchool.class));


		assertTrue(entityCombatService.isInCombat(attacker));
		assertTrue(entityCombatService.isInCombat(target));
		tick(EntityCombatServiceImpl.COMBAT_TIMEOUT - 1);
		assertTrue(entityCombatService.isInCombat(attacker));
		assertTrue(entityCombatService.isInCombat(target));

		tick(1);
		assertFalse(entityCombatService.isInCombat(attacker));
		assertFalse(entityCombatService.isInCombat(target));
	}


	@Test
	public void onlyOneSpellCastAtATime() {

		Set<Effect> effects = Sets.newHashSet();
		effects.add(new DamageSchoolEffect(spellCombatService,
				smrtBroadcaster,
				new IntervalInt(10, 20),
				MagicSchool.FIRE
		));
		ServerSpell spell = ServerSpell.createProjectileSpell(uuidFactory.randomUUID(),
				"Nuke",
				3000,
				MagicSchool.FIRE,
				effects,
				true,
				true,
				new IntervalInt(0, 100),
				0,
				actionScheduler,
				smrtBroadcaster,
				projectileIdCounter
		);
		entityTargetService.setTarget(attacker, target);
		spellCombatService.startSpellCast(attacker, target, spell, timeProvider.now(), null);

		verifyExactly(1).on(attackerReceiver.combat()).entityStartedSpellCast(eq(attacker), eq(target), eq(spell));

		tick(spell.getCastTime() / 3);
		spellCombatService.startSpellCast(attacker, target, spell, timeProvider.now(), null);

		verifyExactly(1).on(attackerReceiver.combat()).entityStartedSpellCast(eq(attacker), eq(target), eq(spell));
	}

	@Test
	public void castTwoSpellsInARow() {

		ServerSpell spell = ServerSpell.createDirectEffectSpell(uuidFactory.randomUUID(),
				"Nuke",
				3000,
				MagicSchool.FIRE,
				new DamageSchoolEffect(spellCombatService,
						smrtBroadcaster,
						new IntervalInt(10, 20),
						MagicSchool.FIRE
				),
				true,
				true,
				new IntervalInt(0, 100));
		entityTargetService.setTarget(attacker, target);
		spellCombatService.startSpellCast(attacker, target, spell, timeProvider.now(), null);

		verifyExactly(1).on(attackerReceiver.combat()).entityStartedSpellCast(eq(attacker), eq(target), eq(spell));

		tick((long) (spell.getCastTime() * 1.1));
		spellCombatService.startSpellCast(attacker, target, spell, timeProvider.now(), null);

		verifyExactly(2).on(attackerReceiver.combat()).entityStartedSpellCast(eq(attacker), eq(target), eq(spell));
	}

	@Test
	public void castSpellsAfterInterruption() {

		ServerSpell spell = ServerSpell.createDirectEffectSpell(uuidFactory.randomUUID(), "Nuke", 3000, MagicSchool.FIRE,
				new DamageSchoolEffect(spellCombatService,
						smrtBroadcaster,
						new IntervalInt(10, 20),
						MagicSchool.FIRE
				), true, true, new IntervalInt(0, 100));
		entityTargetService.setTarget(attacker, target);
		spellCombatService.startSpellCast(attacker, target, spell, timeProvider.now(), null);

		verifyExactly(1).on(attackerReceiver.combat()).entityStartedSpellCast(eq(attacker), eq(target), eq(spell));

		tick((long) (spell.getCastTime() * 0.3));
		spellCombatService.interruptSpellCast(attacker);

		verifyExactly(1).on(attackerReceiver.combat()).entityStoppedSpellCast(eq(attacker), eq(spell));
		verifyExactly(1).on(target1Receiver.combat()).entityStoppedSpellCast(eq(attacker), eq(spell));

		spellCombatService.startSpellCast(attacker, target, spell, timeProvider.now(), null);

		verifyExactly(2).on(attackerReceiver.combat()).entityStartedSpellCast(eq(attacker), eq(target), eq(spell));
		verifyExactly(2).on(target1Receiver.combat()).entityStartedSpellCast(eq(attacker), eq(target), eq(spell));

		tick((long) (spell.getCastTime() * 1.1));
		verifyExactly(1).on(attackerReceiver.combat()).entityCompletedSpellCast(eq(attacker), eq(target), eq(spell));
		verifyExactly(1).on(target1Receiver.combat()).entityCompletedSpellCast(eq(attacker), eq(target), eq(spell));
	}


	@Test
	public void interruptSpellCast() {

		ServerSpell spell = ServerSpell.createDirectEffectSpell(uuidFactory.randomUUID(),
				"Nuke",
				3000,
				MagicSchool.FIRE,
				new DamageSchoolEffect(spellCombatService,
						smrtBroadcaster,
						new IntervalInt(10, 20),
						MagicSchool.FIRE
				),
				true,
				true,
				new IntervalInt(0, 100));

		spellCombatService.startSpellCast(attacker, target, spell, timeProvider.now(), null);

		tick(spell.getCastTime() / 3);
		spellCombatService.stopSpellCast(attacker);
		verifyExactly(1).on(attackerReceiver.combat()).entityStoppedSpellCast(eq(attacker), eq(spell));
		verifyExactly(1).on(target1Receiver.combat()).entityStoppedSpellCast(eq(attacker), eq(spell));

		tick(spell.getCastTime());
		verifyNever().on(attackerReceiver.combat()).entityCompletedSpellCast(eq(attacker), eq(target), eq(spell));
	}

	@Test
	public void moveStopsSpellCast() {

		Set<Effect> effects = Sets.newHashSet();
		effects.add(new DamageSchoolEffect(spellCombatService,
				smrtBroadcaster,
				new IntervalInt(10, 20),
				MagicSchool.FIRE
		));
		ServerSpell spell = ServerSpell.createProjectileSpell(uuidFactory.randomUUID(),
				"Nuke",
				3000,
				MagicSchool.FIRE,
				effects,
				true,
				true,
				new IntervalInt(0, 100),
				0,
				actionScheduler,
				smrtBroadcaster,
				projectileIdCounter
		);

		spellCombatService.startSpellCast(attacker, target, spell, timeProvider.now(), null);

		tick(spell.getCastTime() / 3);

		movementService.moveAndRotateEntity(attacker,
				new MovementPoint<AnimationState>(timeProvider.now(),
						AnimationState.WALK,
						new SpacedVector3(1.0, 0.0, 0.0),
						attacker.getPositionalData().getRotation()));

		verifyExactly(1).on(attackerReceiver.combat()).entityStoppedSpellCast(eq(attacker), eq(spell));
		verifyExactly(1).on(target1Receiver.combat()).entityStoppedSpellCast(eq(attacker), eq(spell));
		tick(spell.getCastTime());
		verifyNever().on(attackerReceiver.combat()).entityCompletedSpellCast(eq(attacker), eq(target), eq(spell));
	}


	@Test
	public void castSpellOutOfRange() {

		Set<Effect> effects = new HashSet<Effect>();
		effects.add(new DamageSchoolEffect(spellCombatService,
				smrtBroadcaster,
				new IntervalInt(10, 20),
				MagicSchool.FIRE
		));
		ServerSpell spell = ServerSpell.createProjectileSpell(uuidFactory.randomUUID(),
				"Nuke",
				3000,
				MagicSchool.FIRE,
				effects,
				true,
				true,
				new IntervalInt(0, 100),
				0,
				actionScheduler,
				smrtBroadcaster,
				projectileIdCounter
		);

		movementService.moveAndRotateEntity(attacker,
				new MovementPoint<AnimationState>(timeProvider.now(),
						AnimationState.IDLE,
						new SpacedVector3(0.0, 0.0, 0.0),
						attacker.getPositionalData().getRotation()));
		movementService.moveAndRotateEntity(target,
				new MovementPoint<AnimationState>(timeProvider.now(),
						AnimationState.IDLE,
						new SpacedVector3(110.0, 0.0, 0.0),
						target.getPositionalData().getRotation()));

		spellCombatService.startSpellCast(attacker, target, spell, timeProvider.now(), null);
		verifyNever().on(attackerReceiver.combat()).entityStartedSpellCast(eq(attacker), eq(target), eq(spell));
	}

	@Test
	public void castSpellInRangeAndThenMoveOutOfRange() {

		Set<Effect> effects = Sets.newHashSet();
		effects.add(new DamageSchoolEffect(spellCombatService,
				smrtBroadcaster,
				new IntervalInt(10, 20),
				MagicSchool.FIRE
		));
		ServerSpell spell = ServerSpell.createProjectileSpell(uuidFactory.randomUUID(),
				"Nuke",
				3000,
				MagicSchool.FIRE,
				effects,
				true,
				true,
				new IntervalInt(0, 100),
				0,
				actionScheduler,
				smrtBroadcaster,
				projectileIdCounter
		);

		movementService.moveAndRotateEntity(attacker,
				new MovementPoint<AnimationState>(timeProvider.now(),
						AnimationState.IDLE,
						new SpacedVector3(0.0, 0.0, 0.0),
						attacker.getPositionalData().getRotation()));
		movementService.moveAndRotateEntity(target,
				new MovementPoint<AnimationState>(timeProvider.now(),
						AnimationState.IDLE,
						new SpacedVector3(10.0, 0.0, 0.0),
						target.getPositionalData().getRotation()));

		spellCombatService.startSpellCast(attacker, target, spell, timeProvider.now(), null);

		verifyExactly(1).on(attackerReceiver.combat()).entityStartedSpellCast(eq(attacker), eq(target), eq(spell));

		movementService.moveAndRotateEntity(target,
				new MovementPoint<AnimationState>(timeProvider.now(),
						AnimationState.IDLE,
						new SpacedVector3(110.0, 0.0, 0.0),
						target.getPositionalData().getRotation()));

		tick(spell.getCastTime());
		verifyExactly(1).on(attackerReceiver.combat()).entityStoppedSpellCast(eq(attacker), eq(spell));
		verifyExactly(1).on(target1Receiver.combat()).entityStoppedSpellCast(eq(attacker), eq(spell));

		verifyNever().on(attackerReceiver.combat()).entityCompletedSpellCast(eq(attacker), eq(target), eq(spell));
	}

	@Test
	public void haveSpellFailAndThenCastAgain() {

		ServerSpell spell = ServerSpell.createDirectEffectSpell(uuidFactory.randomUUID(), "Nuke", 3000, MagicSchool.FIRE,
				new DamageSchoolEffect(spellCombatService,
						smrtBroadcaster,
						new IntervalInt(10, 20),
						MagicSchool.FIRE
				), true, true, new IntervalInt(0, 100));

		movementService.moveAndRotateEntity(attacker,
				new MovementPoint<AnimationState>(timeProvider.now(),
						AnimationState.IDLE,
						new SpacedVector3(0.0, 0.0, 0.0),
						attacker.getPositionalData().getRotation()));
		movementService.moveAndRotateEntity(target,
				new MovementPoint<AnimationState>(timeProvider.now(),
						AnimationState.IDLE,
						new SpacedVector3(10.0, 0.0, 0.0),
						target.getPositionalData().getRotation()));

		spellCombatService.startSpellCast(attacker, target, spell, timeProvider.now(), null);

		verifyExactly(1).on(attackerReceiver.combat()).entityStartedSpellCast(eq(attacker), eq(target), eq(spell));

		movementService.moveAndRotateEntity(target,
				new MovementPoint<AnimationState>(timeProvider.now(),
						AnimationState.IDLE,
						new SpacedVector3(110.0, 0.0, 0.0),
						target.getPositionalData().getRotation()));


		tick(spell.getCastTime());

		movementService.moveAndRotateEntity(attacker,
				new MovementPoint<AnimationState>(timeProvider.now(),
						AnimationState.IDLE,
						new SpacedVector3(100.0, 0.0, 0.0),
						attacker.getPositionalData().getRotation()));

		spellCombatService.startSpellCast(attacker, target, spell, timeProvider.now(), null);

		verifyExactly(2).on(attackerReceiver.combat()).entityStartedSpellCast(eq(attacker), eq(target), eq(spell));
		tick(spell.getCastTime());
		verifyExactly(1).on(attackerReceiver.combat()).entityCompletedSpellCast(eq(attacker), eq(target), eq(spell));
	}

	@Test
	public void testSimpleProjectile() {
		movementService.moveAndRotateEntity(attacker,
				new MovementPoint<AnimationState>(timeProvider.now(), AnimationState.IDLE, SpacedVector3.ZERO, SpacedRotation.IDENTITY));
		movementService.moveAndRotateEntity(target,
				new MovementPoint<AnimationState>(timeProvider.now(), AnimationState.IDLE, new SpacedVector3(0, 0, 100), SpacedRotation.IDENTITY));
		Set<Effect> effects = Sets.newHashSet();

		double SPEED = 1.0;
		effects.add(new DamageSchoolEffect(spellCombatService,
				smrtBroadcaster,
				new IntervalInt(10, 12),
				MagicSchool.FIRE
		));
		ServerSpell spell = ServerSpell.createProjectileSpell(uuidFactory.randomUUID(),
				"Nuke",
				3000,
				MagicSchool.FIRE,
				effects,
				true,
				true,
				new IntervalInt(0, 200),
				SPEED,
				actionScheduler,
				smrtBroadcaster,
				projectileIdCounter
		);

		spellCombatService.startSpellCast(attacker, target, spell, 0, null);

		tick(3000);

		assertFalse(entityCombatService.isInCombat(attacker));
		assertFalse(entityCombatService.isInCombat(target));

		double travelTime = 100 / SPEED;

		tick((long) (travelTime * 1000 - 1));
		assertFalse(entityCombatService.isInCombat(attacker));
		assertFalse(entityCombatService.isInCombat(target));
		verifyNever().on(attackerReceiver.combat()).entityDamaged(any(Entity.class),
				any(Entity.class),
				anyInt(),
				anyInt(),
				any(String.class),
				any(MagicSchool.class));

		tick(2);

		assertTrue(entityCombatService.isInCombat(attacker));
		assertTrue(entityCombatService.isInCombat(target));
		verifyExactly(1).on(attackerReceiver.combat()).entityAbsorbedDamaged(eq(attacker),
				eq(target),
				anyInt(),
				anyInt(),
				eq(spell.getName()),
				eq(MagicSchool.FIRE));
	}

	@Test
	public void testReallyReallyFastProjectileUpClose() {
		movementService.moveAndRotateEntity(attacker,
				new MovementPoint<AnimationState>(timeProvider.now(), AnimationState.IDLE, SpacedVector3.ZERO, SpacedRotation.IDENTITY));
		movementService.moveAndRotateEntity(target,
				new MovementPoint<AnimationState>(timeProvider.now(), AnimationState.IDLE, new SpacedVector3(0, 0, 1), SpacedRotation.IDENTITY));
		Set<Effect> effects = Sets.newHashSet();
		ProjectileEffect projectileEffect = new ProjectileEffect(actionScheduler,
				smrtBroadcaster,
				projectileIdCounter
		);
		double speedOfLight = 299792458.0;
		projectileEffect.setSpeed(speedOfLight);
		projectileEffect.addImpactEffect(new DamageSchoolEffect(spellCombatService,
				smrtBroadcaster,
				new IntervalInt(10, 12),
				MagicSchool.LIGHT
		));
		effects.add(projectileEffect);

		ServerSpell spell = ServerSpell.createProjectileSpell(uuidFactory.randomUUID(),
				"Giant Frickin Lazorz",
				3000,
				MagicSchool.LIGHT,
				effects,
				true,
				true,
				new IntervalInt(0, 200),
				speedOfLight,
				actionScheduler,
				smrtBroadcaster,
				projectileIdCounter
		);

		spellCombatService.startSpellCast(attacker, target, spell, 0, null);

		tick(3000);

		assertTrue(entityCombatService.isInCombat(attacker));
		assertTrue(entityCombatService.isInCombat(target));
		verifyExactly(1).on(attackerReceiver.combat()).entityAbsorbedDamaged(eq(attacker),
				eq(target),
				anyInt(),
				anyInt(),
				eq(spell.getName()),
				eq(MagicSchool.LIGHT));
	}


	@Test
	public void testSlowProjectile() {
		movementService.moveAndRotateEntity(attacker,
				new MovementPoint<AnimationState>(timeProvider.now(), AnimationState.IDLE, new SpacedVector3(31.6, 0, 5.7), SpacedRotation.IDENTITY));
		movementService.moveAndRotateEntity(target,
				new MovementPoint<AnimationState>(timeProvider.now(), AnimationState.IDLE, new SpacedVector3(76.6, 0.35, 9.75), SpacedRotation.IDENTITY));
		Set<Effect> effects = Sets.newHashSet();
		double SPEED = 3.0;

		effects.add(new DamageSchoolEffect(spellCombatService,
				smrtBroadcaster,
				new IntervalInt(10, 12),
				MagicSchool.FIRE
		));

		ServerSpell spell = ServerSpell.createProjectileSpell(uuidFactory.randomUUID(),
				"Nuke",
				3000,
				MagicSchool.FIRE,
				effects,
				true,
				true,
				new IntervalInt(0, 200),
				SPEED,
				actionScheduler,
				smrtBroadcaster,
				projectileIdCounter
		);

		spellCombatService.startSpellCast(attacker, target, spell, 0, null);

		tick(3000);

		assertFalse(entityCombatService.isInCombat(attacker));
		assertFalse(entityCombatService.isInCombat(target));

		double travelTime = SpacedVector3.distance(attacker.getPosition(), target.getPosition()) / SPEED;

		long waitTime = (long) (travelTime * 1000 - 1);
		long steps = 50;
		long smallTick = waitTime / steps;
		for (int i = 0; i < steps; i++) {
			tick(smallTick);
			SpacedVector3 attackerPosition = attacker.getPosition();
			attackerPosition = attackerPosition.add(new SpacedVector3(0, 0.5, 0));
			movementService.moveAndRotateEntity(attacker,
					new MovementPoint<AnimationState>(timeProvider.now(), AnimationState.IDLE, attackerPosition, attacker.getRotation()));
		}
		tick(waitTime - steps * smallTick);

		assertFalse(entityCombatService.isInCombat(attacker));
		assertFalse(entityCombatService.isInCombat(target));

		verifyNever().on(attackerReceiver.combat()).entityDamaged(any(Entity.class),
				any(Entity.class),
				anyInt(),
				anyInt(),
				any(String.class),
				any(MagicSchool.class));

		tick(1);

		assertTrue(entityCombatService.isInCombat(attacker));
		assertTrue(entityCombatService.isInCombat(target));
		verifyExactly(1).on(attackerReceiver.combat()).entityAbsorbedDamaged(eq(attacker),
				eq(target),
				anyInt(),
				anyInt(),
				eq(spell.getName()),
				eq(MagicSchool.FIRE));
	}

	@Test
	public void coolLowersHeat() {
		ServerSpell spell = mock(ServerSpell.class);
		CoolEffect effect = new CoolEffect(spellCombatService,
				smrtBroadcaster,
				MagicSchool.FROST,
				new IntervalInt(20, 20)
		);

		attacker.getBaseStats().getHeat().generate(50);
		assertEquals(50, attacker.getBaseStats().getHeat().getValue(), EPSILON);

		spellCombatService.doCool(attacker, attacker, timeProvider.now(), effect, spell.getName());

		assertEquals(30, attacker.getBaseStats().getHeat().getValue(), EPSILON);
	}

	@Test
	public void testCoolDoesntEnterCombatIfTargetIsNotInCombat() {
		ServerSpell spell = mock(ServerSpell.class);
		CoolEffect effect = new CoolEffect(spellCombatService,
				smrtBroadcaster,
				MagicSchool.FROST,
				new IntervalInt(20, 20)
		);

		spellCombatService.doCool(attacker, attacker, timeProvider.now(), effect, spell.getName());

		assertFalse(entityCombatService.isInCombat(attacker));
	}

	@Test
	public void healDoesntEnterCombatIfTargetIsNotInCombat() {
		ServerSpell spell = mock(ServerSpell.class);
		HealEffect effect = new HealEffect(spellCombatService,
				smrtBroadcaster,
				MagicSchool.FROST,
				new IntervalInt(20, 20)
		);

		spellCombatService.doHeal(attacker, attacker, timeProvider.now(), effect, spell.getName());

		assertFalse(entityCombatService.isInCombat(attacker));
	}

	@Test
	public void healEnterCombatIfTargetIsInCombat() {
		ServerSpell spell = mock(ServerSpell.class);
		entityCombatService.enterCombat(target, target2, timeProvider.now(), false);

		HealEffect effect = new HealEffect(spellCombatService,
				smrtBroadcaster,
				MagicSchool.FROST,
				new IntervalInt(20, 20)
		);

		spellCombatService.doHeal(attacker, target, timeProvider.now(), effect, spell.getName());

		assertTrue(entityCombatService.isInCombat(attacker));
	}


	@Test
	public void recoverDoesntEnterCombatIfTargetIsNotInCombat() {
		ServerSpell spell = mock(ServerSpell.class);
		RecoverEffect effect = new RecoverEffect(spellCombatService,
				smrtBroadcaster,
				MagicSchool.FROST,
				new IntervalInt(20, 20)
		);

		spellCombatService.doRecover(attacker, attacker, timeProvider.now(), effect, spell.getName());

		assertFalse(entityCombatService.isInCombat(attacker));
	}

	@Test
	public void recoverEnterCombatIfTargetIsInCombat() {
		ServerSpell spell = mock(ServerSpell.class);
		entityCombatService.enterCombat(target, target2, timeProvider.now(), false);

		RecoverEffect effect = new RecoverEffect(spellCombatService,
				smrtBroadcaster,
				MagicSchool.FROST,
				new IntervalInt(20, 20)
		);

		spellCombatService.doRecover(attacker, target, timeProvider.now(), effect, spell.getName());

		assertTrue(entityCombatService.isInCombat(attacker));
	}


	@Test
	public void coolEntersCombatIfTargetIsInCombat() {
		ServerSpell spell = mock(ServerSpell.class);
		CoolEffect effect = new CoolEffect(spellCombatService,
				smrtBroadcaster,
				MagicSchool.FROST,
				new IntervalInt(20, 20)
		);

		entityCombatService.enterCombat(target, target2, timeProvider.now(), false);
		assertTrue(entityCombatService.isInCombat(target));
		spellCombatService.doCool(attacker, target, timeProvider.now(), effect, spell.getName());

		assertTrue(entityCombatService.isInCombat(attacker));
	}

	@Test
	public void healIncreasesHp() {
		RangeableEffect healEffect = new HealEffect(spellCombatService,
				smrtBroadcaster,
				MagicSchool.LIGHT,
				new IntervalInt(20, 20)
		);
		double beforeTest = target2.getBaseStats().getCurrentHealth().getValue();
		target2.getBaseStats().getCurrentHealth().decreaseValue(beforeTest - 1);

		double beforeHeal = target2.getBaseStats().getCurrentHealth().getValue();
		assertTrue(beforeTest > beforeHeal);
		spellCombatService.doHeal(target, target2, timeProvider.now(), healEffect, "Cause");

		double afterHeal = target2.getBaseStats().getCurrentHealth().getValue();
		int amount = (int) (afterHeal - beforeHeal);
		assertTrue(amount > 0);
		verifyOnce().on(target2Receiver.combat()).entityHealed(target,
				target2,
				amount,
				(int) afterHeal,
				"Cause",
				MagicSchool.LIGHT);
		verifyOnce().on(target1Receiver.combat()).entityHealed(target,
				target2,
				amount,
				(int) afterHeal,
				"Cause",
				MagicSchool.LIGHT);
	}


	@Test
	public void recoverIncreasesShield() {
		RangeableEffect recoverEffect = new RecoverEffect(spellCombatService,
				smrtBroadcaster,
				MagicSchool.LIGHT,
				new IntervalInt(20, 20)
		);
		target2.getBaseStats().getBaseShieldCharge().changeValue(30);
		target2.getBaseStats().getShieldStrength().changeValue(20);
		assertEquals(20, target2.getBaseStats().getShieldStrength().getValue(), EPSILON);
		double beforeTest = target2.getBaseStats().getShieldStrength().getValue();

		spellCombatService.doRecover(target, target2, timeProvider.now(), recoverEffect, "Cause");

		double after = target2.getBaseStats().getShieldStrength().getValue();
		int amount = (int) (after - beforeTest);
		assertTrue(amount > 0);
		verifyOnce().on(target2Receiver.combat()).entityRecovered(target,
				target2,
				amount,
				(int) after,
				"Cause",
				MagicSchool.LIGHT);
		verifyOnce().on(target1Receiver.combat()).entityRecovered(target,
				target2,
				amount,
				(int) after,
				"Cause",
				MagicSchool.LIGHT);
	}

	@Test
	public void damageAtTheBorderOfShieldAndHp() throws Exception {
		target.getBaseStats().getShieldStrength().changeValue(3);

		MagicSchool magicSchool = MagicSchool.FIRE;
		DamageSchoolEffect damageSchoolEffect = new DamageSchoolEffect(spellCombatService,
				smrtBroadcaster,
				new IntervalInt(10, 10),
				magicSchool
		);
		String causeName = "Ouch";
		int healthPreDamage = (int) target.getBaseStats().getCurrentHealth().getValue();
		spellCombatService.doDamage(attacker, target, timeProvider.now(), damageSchoolEffect, causeName);

		int expectedHealthPost = healthPreDamage - 7;
		verifyOnce().on(target1Receiver.combat()).entityAbsorbedDamaged(attacker, target, 3, 0, causeName, magicSchool);
		verifyOnce().on(target1Receiver.combat()).entityDamaged(attacker, target, 7, expectedHealthPost, causeName, magicSchool);

		assertEquals(0, target.getBaseStats().getShieldStrength().getValue(), EPSILON);
		assertEquals(63, target.getBaseStats().getCurrentHealth().getValue(), EPSILON);
	}


	@Test
	public void damageAtTheBorderOfShieldAndHpThenWaitAndThenDamageAgain() throws Exception {
		EntityStats targetBaseStats = target.getBaseStats();
		targetBaseStats.getShieldStrength().changeValue(3);
		double baseShieldRecovery = targetBaseStats.getBaseShieldRecovery().getValue();

		MagicSchool magicSchool = MagicSchool.FIRE;
		DamageSchoolEffect damageSchoolEffect = new DamageSchoolEffect(spellCombatService,
				smrtBroadcaster,
				new IntervalInt(10, 10),
				magicSchool
		);
		String causeName = "Ouch";
		int healthPreDamage = (int) targetBaseStats.getCurrentHealth().getValue();
		spellCombatService.doDamage(attacker, target, timeProvider.now(), damageSchoolEffect, causeName);

		tick(4000);

		double expectedShieldAfter4Seconds = 4 * baseShieldRecovery;
		assertEquals(expectedShieldAfter4Seconds, targetBaseStats.getShieldStrength().getValue(), EPSILON);
		assertEquals(63, targetBaseStats.getCurrentHealth().getValue(), EPSILON);
	}


	@Test
	public void damageWhenBuffedWithAttackRating() throws Exception {
		EntityStats targetBaseStats = target.getBaseStats();
		targetBaseStats.getShieldEfficiency().changeValue(1);
		targetBaseStats.getShieldCharge().changeValue(400);
		targetBaseStats.getShieldStrength().changeValue(400);

		attacker.getBaseStats().getAttackRating().addModStat(new ModStat(EntityStats.ATTACK_RATING_PER_ATTACK_PERCENT_MULTIPLIER * 10, SpacedStatType.ATTACK_RATING, Operator.ADD));

		MagicSchool magicSchool = MagicSchool.FIRE;
		DamageSchoolEffect damageSchoolEffect = new DamageSchoolEffect(spellCombatService,
				smrtBroadcaster,
				new IntervalInt(200, 200),
				magicSchool
		);
		String causeName = "Ouch";
		int healthPreDamage = (int) targetBaseStats.getCurrentHealth().getValue();
		int shieldPreDamage = (int) targetBaseStats.getShieldStrength().getValue();
		spellCombatService.doDamage(attacker, target, timeProvider.now(), damageSchoolEffect, causeName);

		int expectedDamage = 220;
		int expectedShieldPost = shieldPreDamage - expectedDamage;
		verifyOnce().on(target1Receiver.combat()).entityAbsorbedDamaged(attacker, target, expectedDamage, expectedShieldPost, causeName, magicSchool);
		verifyNever().on(target1Receiver.combat()).entityDamaged(attacker, target, anyInt(), healthPreDamage, causeName, magicSchool);

		assertEquals(expectedShieldPost, targetBaseStats.getShieldStrength().getValue(), EPSILON);
		assertEquals(healthPreDamage, targetBaseStats.getCurrentHealth().getValue(), EPSILON);
	}


	@Test
	public void damageWhenDebuffedWithAttackRating() throws Exception {
		EntityStats targetBaseStats = target.getBaseStats();
		targetBaseStats.getShieldEfficiency().changeValue(1);
		targetBaseStats.getShieldCharge().changeValue(400);
		targetBaseStats.getShieldStrength().changeValue(400);

		attacker.getBaseStats().getAttackRating().addModStat(new ModStat(-EntityStats.ATTACK_RATING_PER_ATTACK_PERCENT_MULTIPLIER * 99, SpacedStatType.ATTACK_RATING, Operator.ADD));

		MagicSchool magicSchool = MagicSchool.FIRE;
		DamageSchoolEffect damageSchoolEffect = new DamageSchoolEffect(spellCombatService,
				smrtBroadcaster,
				new IntervalInt(200, 200),
				magicSchool
		);
		String causeName = "Ouch";
		int healthPreDamage = (int) targetBaseStats.getCurrentHealth().getValue();
		int shieldPreDamage = (int) targetBaseStats.getShieldStrength().getValue();
		spellCombatService.doDamage(attacker, target, timeProvider.now(), damageSchoolEffect, causeName);

		int expectedDamage = 2;
		int expectedShieldPost = shieldPreDamage - expectedDamage;
		verifyOnce().on(target1Receiver.combat()).entityAbsorbedDamaged(attacker, target, expectedDamage, expectedShieldPost, causeName, magicSchool);
		verifyNever().on(target1Receiver.combat()).entityDamaged(attacker, target, anyInt(), healthPreDamage, causeName, magicSchool);

		assertEquals(expectedShieldPost, targetBaseStats.getShieldStrength().getValue(), EPSILON);
		assertEquals(healthPreDamage, targetBaseStats.getCurrentHealth().getValue(), EPSILON);
	}
}