package se.spaced.server.mob.brains;

import org.junit.Before;
import org.junit.Test;
import se.ardortech.math.SpacedVector3;
import se.fearless.common.uuid.UUID;
import se.spaced.server.ScenarioTestBase;
import se.spaced.server.mob.MobDecision;
import se.spaced.server.mob.MobInfoProvider;
import se.spaced.server.model.Mob;
import se.spaced.server.model.PersistedCreatureType;
import se.spaced.server.model.Player;
import se.spaced.server.model.spawn.AttackingParameters;
import se.spaced.server.model.spawn.MobTemplate;
import se.spaced.server.model.spell.ServerSpell;
import se.spaced.server.model.spell.effect.Effect;
import se.spaced.shared.model.MagicSchool;
import se.spaced.shared.util.math.interval.IntervalInt;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static se.mockachino.Mockachino.*;
import static se.mockachino.matchers.Matchers.eq;
import static se.mockachino.matchers.Matchers.same;

public class AttackingBrainTest extends ScenarioTestBase {
	private MobInfoProvider mobInfoProvider;

	@Before
	public void setup() {
		mobInfoProvider = new MobInfoProvider();
	}

	@Test
	public void testMovesTowards() {
		Effect effect = mock(Effect.class);
		ServerSpell spell = ServerSpell.createDirectEffectSpell(new UUID(0, 0),
				"wtfspell",
				0,
				MagicSchool.FIRE,
				effect,
				true,
				true,
				new IntervalInt(10, 100));
		MobTemplate template = new MobTemplate.Builder(uuidFactory.randomUUID(), "template").creatureType(
				new PersistedCreatureType(uuidFactory.combUUID(), "creaturetype")).spells(Arrays.asList(spell)).build();
		Mob mob = template.createMob(timeProvider, new UUID(0, 0), randomProvider);
		visibilityService.entityAdded(mob);
		Player player = mock(Player.class);
		stubReturn(true).on(player).isAlive();

		stubReturn(new SpacedVector3(1000, 0, 0)).on(player).getPosition();

		AttackingBrain brain = new AttackingBrain(mobOrderExecutor,
				mobInfoProvider,
				entityTargetService,
				mob,
				new AttackingParameters(true, true));


		MobDecision decision = brain.act(timeProvider.now());
		assertEquals(MobDecision.UNDECIDED, decision);

		brain.getSmrtReceiver().combat().entityDamaged(player, mob, 100, 200, spell.getName(), MagicSchool.FIRE);
		decision = brain.act(timeProvider.now());
		assertEquals(MobDecision.DECIDED, decision);
		verifyOnce().on(mobOrderExecutor).runTo(same(mob), eq(new SpacedVector3(1000, 0, 0)));
	}

	@Test
	public void testFollowsTarget() {
		Effect effect = mock(Effect.class);
		ServerSpell spell = ServerSpell.createDirectEffectSpell(new UUID(0, 0),
				"wtfspell",
				0,
				MagicSchool.FIRE,
				effect,
				true,
				true,
				new IntervalInt(10, 100));
		MobTemplate template = new MobTemplate.Builder(uuidFactory.randomUUID(), "template").creatureType(
				new PersistedCreatureType(uuidFactory.combUUID(), "creaturetype")).spells(Arrays.asList(spell)).build();
		Mob mob = template.createMob(timeProvider, new UUID(0, 0), randomProvider);
		visibilityService.entityAdded(mob);
		Player player = mock(Player.class);
		stubReturn(true).on(player).isAlive();

		stubReturn(new SpacedVector3(1000, 0, 0)).on(player).getPosition();

		AttackingBrain brain = new AttackingBrain(mobOrderExecutor,
				mobInfoProvider,
				entityTargetService,
				mob,
				new AttackingParameters(true, true));


		MobDecision decision = brain.act(timeProvider.now());
		assertEquals(MobDecision.UNDECIDED, decision);

		brain.getSmrtReceiver().combat().entityDamaged(player, mob, 100, 200, spell.getName(), MagicSchool.FIRE);
		decision = brain.act(timeProvider.now());
		assertEquals(MobDecision.DECIDED, decision);
		verifyOnce().on(mobOrderExecutor).runTo(same(mob), eq(new SpacedVector3(1000, 0, 0)));

		stubReturn(new SpacedVector3(0, 1000, 0)).on(player).getPosition();

		decision = brain.act(timeProvider.now());
		assertEquals(MobDecision.DECIDED, decision);
		verifyOnce().on(mobOrderExecutor).runTo(same(mob), eq(new SpacedVector3(0, 1000, 0)));


	}

	@Test
	public void testFire() {
		Effect effect = mock(Effect.class);
		ServerSpell spell = ServerSpell.createDirectEffectSpell(new UUID(0, 0),
				"wtfspell",
				200,
				MagicSchool.FIRE,
				effect,
				true,
				true,
				new IntervalInt(10, 100));
		MobTemplate template = new MobTemplate.Builder(uuidFactory.randomUUID(), "Foo").
				spells(Arrays.asList(spell)).build();
		Mob mob = template.createMob(timeProvider, UUID.ZERO, randomProvider);
		visibilityService.entityAdded(mob);
		Player player = mock(Player.class);
		stubReturn(true).on(player).isAlive();

		stubReturn(new SpacedVector3(50, 0, 0)).on(player).getPosition();

		long aggroTime = 60 * 1000;

		AttackingBrain brain = new AttackingBrain(mobOrderExecutor,
				mobInfoProvider,
				entityTargetService,
				mob,
				new AttackingParameters(true, true));


		MobDecision decision = brain.act(timeProvider.now());
		assertEquals(MobDecision.UNDECIDED, decision);

		brain.getSmrtReceiver().combat().entityDamaged(player, mob, 100, 200, spell.getName(), MagicSchool.FIRE);
		decision = brain.act(timeProvider.now());
		assertEquals(MobDecision.DECIDED, decision);
		verifyOnce().on(mobOrderExecutor).castSpell(same(mob), same(player), same(spell));

		tick(199);
		brain.act(timeProvider.now());
		verifyOnce().on(mobOrderExecutor).castSpell(same(mob), same(player), same(spell));

		tick(1);
		brain.act(timeProvider.now());
		verifyExactly(2).on(mobOrderExecutor).castSpell(same(mob), same(player), same(spell));

	}

	@Test
	public void testBackOff() {
		Effect effect = mock(Effect.class);
		ServerSpell spell = ServerSpell.createDirectEffectSpell(new UUID(0, 0),
				"wtfspell",
				0,
				MagicSchool.FIRE,
				effect,
				true,
				true,
				new IntervalInt(10, 100));

		MobTemplate template = new MobTemplate.Builder(uuidFactory.randomUUID(),
				"template").creatureType(new PersistedCreatureType(new UUID(10,
				10), "creaturetype")).spells(Arrays.asList(spell)).build();
		Mob mob = template.createMob(timeProvider, UUID.ZERO, randomProvider);
		visibilityService.entityAdded(mob);
		Player player = mock(Player.class);
		stubReturn(true).on(player).isAlive();

		stubReturn(new SpacedVector3(5, 0, 0)).on(player).getPosition();


		long aggroTime = 60 * 1000;
		AttackingBrain brain = new AttackingBrain(mobOrderExecutor,
				mobInfoProvider,
				entityTargetService,
				mob,
				new AttackingParameters(true, true));

		MobDecision decision = brain.act(timeProvider.now());
		assertEquals(MobDecision.UNDECIDED, decision);

		brain.getSmrtReceiver().combat().entityDamaged(player, mob, 100, 200, spell.getName(), MagicSchool.FIRE);
		decision = brain.act(timeProvider.now());
		assertEquals(MobDecision.DECIDED, decision);

		verifyOnce().on(mobOrderExecutor).backAwayFrom(same(mob), player.getPosition(), 10);
	}

	@Test
	public void testMovesTowardsWithStationaryMob() {
		Effect effect = mock(Effect.class);
		ServerSpell spell = ServerSpell.createDirectEffectSpell(new UUID(0, 0),
				"wtfspell",
				0,
				MagicSchool.FIRE,
				effect,
				true,
				true,
				new IntervalInt(10, 100));
		MobTemplate template = new MobTemplate.Builder(uuidFactory.randomUUID(), "template").creatureType(
				new PersistedCreatureType(uuidFactory.combUUID(), "creaturetype")).spells(Arrays.asList(spell)).build();
		Mob mob = template.createMob(timeProvider, new UUID(0, 0), randomProvider);
		visibilityService.entityAdded(mob);
		Player player = mock(Player.class);
		stubReturn(true).on(player).isAlive();

		stubReturn(new SpacedVector3(1000, 0, 0)).on(player).getPosition();

		long aggroTime = 60 * 1000;


		AttackingBrain brain = new AttackingBrain(mobOrderExecutor,
				mobInfoProvider,
				entityTargetService,
				mob,
				new AttackingParameters(false, true));


		MobDecision decision = brain.act(timeProvider.now());
		assertEquals(MobDecision.UNDECIDED, decision);

		brain.getSmrtReceiver().combat().entityDamaged(player, mob, 100, 200, spell.getName(), MagicSchool.FIRE);
		decision = brain.act(timeProvider.now());
		assertEquals(MobDecision.DECIDED, decision);
		verifyNever().on(mobOrderExecutor).walkTo(same(mob), eq(new SpacedVector3(1000, 0, 0)));
	}

}
