package se.spaced.server.model.combat;

import org.junit.Test;
import se.ardortech.math.SpacedVector3;
import se.fearless.common.mock.MockUtil;
import se.spaced.messages.protocol.s2c.S2CProtocol;
import se.spaced.server.model.Player;
import se.spaced.server.model.ServerEntity;
import se.spaced.server.model.player.PlayerMockFactory;
import se.spaced.server.model.spawn.area.SinglePointSpawnArea;
import se.spaced.server.model.spell.ServerSpell;
import se.spaced.server.model.spell.effect.DamageSchoolEffect;
import se.spaced.server.model.spell.effect.Effect;
import se.spaced.server.persistence.dao.impl.hibernate.GraveyardTemplate;
import se.spaced.shared.model.MagicSchool;
import se.spaced.shared.util.math.interval.IntervalInt;

import static org.junit.Assert.*;
import static se.mockachino.Mockachino.*;
import static se.mockachino.matchers.Matchers.any;
import static se.mockachino.matchers.Matchers.eq;


public class CombatServiceTest extends AbstractCombatServiceTest {
	private static final double EPSILON = 0.0000001;


   @Test
	public void enterCombat() {
		assertFalse(entityCombatService.isInCombat(attacker));
		assertFalse(entityCombatService.isInCombat(target));

		int castTime = 1000;
		ServerSpell spell = createInstantDamageSpell(castTime, 1, "Poke");
		spellCombatService.startSpellCast(attacker, target, spell, timeProvider.now(), null);

		assertFalse(entityCombatService.isInCombat(attacker));
		assertFalse(entityCombatService.isInCombat(target));
		tick(castTime);

		assertTrue(entityCombatService.isInCombat(attacker));
		assertTrue(entityCombatService.isInCombat(target));
	}

	@Test
	public void enterCombatSentToParticipants() {
		int castTime = 1000;
		ServerSpell spell = createInstantDamageSpell(castTime, 1, "Poke");
		spellCombatService.startSpellCast(attacker, target, spell, timeProvider.now(), null);

		tick(castTime);

		verifyOnce().on(attackerReceiver.combat()).combatStatusChanged(attacker, eq(true));
		verifyOnce().on(target1Receiver.combat()).combatStatusChanged(target, eq(true));
	}


	@Test
	public void getDeathMessage() {
		getData(attackerReceiver.combat()).resetCalls();
		getData(target1Receiver.combat()).resetCalls();

		target.getBaseStats().getShieldStrength().changeValue(0);
		target.getBaseStats().getShieldRecoveryRate().changeValue(0);

		int castTime = 1000;
		ServerSpell spell = createInstantDamageSpell(castTime, 10, "Boom");

		castMultipleSpells(attacker, target, castTime, spell, 3);

		verifyNever().on(attackerReceiver.combat()).entityWasKilled(eq(attacker), eq(target));
		verifyNever().on(target1Receiver.combat()).entityWasKilled(eq(attacker), eq(target));

		castMultipleSpells(attacker, target, castTime, spell, 10);

		assertEquals(0, target.getBaseStats().getCurrentHealth().getValue(), EPSILON);
		assertFalse(target.isAlive());

		verifyExactly(1).on(attackerReceiver.combat()).entityWasKilled(eq(attacker), eq(target));
		verifyExactly(1).on(target1Receiver.combat()).entityWasKilled(eq(attacker), eq(target));

	}

	private void castMultipleSpells(ServerEntity attacker, ServerEntity target, int castTime, ServerSpell spell, int casts) {
		for (int i = 0; i < casts; i++) {
			spellCombatService.startSpellCast(attacker, target, spell, timeProvider.now(), null);
			tick(castTime);
		}
	}


	@Test
	public void killAndResOverAndOverAgain() {
		when(graveyardService.getClosestGraveyard(any(SpacedVector3.class))).thenReturn(new GraveyardTemplate(uuidFactory.randomUUID(), "Foo", new SinglePointSpawnArea(target.getPosition(), target.getRotation())));
		getDeathMessage();
		tick(6000);
		entityCombatService.respawnWithHealth(target, (int) target.getBaseStats().getMaxHealth().getValue());
		assertTrue(target.isAlive());
		getDeathMessage();
		assertFalse(target.isAlive());
		entityCombatService.respawnWithHealth(target, (int) target.getBaseStats().getMaxHealth().getValue());
		assertTrue(target.isAlive());
		getDeathMessage();
		assertFalse(target.isAlive());
		entityCombatService.respawnWithHealth(target, (int) target.getBaseStats().getMaxHealth().getValue());
		assertTrue(target.isAlive());
		getDeathMessage();
		assertFalse(target.isAlive());
	}


	@Test
	public void stopAttackAfterSeveral() {
		int castTime = 1000;
		ServerSpell spell = createInstantDamageSpell(castTime, 1, "Poke");
		castMultipleSpells(attacker, target, castTime, spell, 10);

		// wait for combat to stop
		tick(10000L);
		assertTrue(actionScheduler.isEmpty());

		assertEquals(0, entityCombatService.numberOfCombat());
	}

	@Test
	public void combatEnds() {
		int castTime = 1000;
		ServerSpell spell = createInstantDamageSpell(castTime, 1, "Poke");
		castMultipleSpells(attacker, target, castTime, spell, 1);

		tick(10000L);

		assertFalse("attacker is still in combat", entityCombatService.isInCombat(attacker));
		assertFalse("target is still in combat", entityCombatService.isInCombat(target));

		verifyOnce().on(attackerReceiver.combat()).combatStatusChanged(attacker, eq(false));
		verifyOnce().on(target1Receiver.combat()).combatStatusChanged(target, eq(false));
	}

	@Test
	public void beingTheLastOneLeftInCombatMakesCombatEndInstantly() throws Exception {
		int damage = (int) (target.getBaseStats().getCurrentHealth().getValue() * 2);
		int castTime = 1000;
		ServerSpell spell = createInstantDamageSpell(castTime, damage, "Big Boom");
		castMultipleSpells(attacker, target, castTime, spell, 1);
		assertFalse("Target didn't die", target.isAlive());
		assertFalse("Target wasn't removed from combat", entityCombatService.isInCombat(target));
		assertFalse("Attacker wasn't removed from combat", entityCombatService.isInCombat(attacker));
	}


	@Test
	public void onlyFriendlyPlayersLeftInCombatMakesCombatEndInstantly() throws Exception {
		int damage = (int) (target.getBaseStats().getCurrentHealth().getValue() * 2);
		int castTime = 1000;
		ServerSpell spell = createInstantDamageSpell(castTime, damage, "Big Boom");

		ServerSpell poke = createInstantDamageSpell(castTime, 1, "Poke");

		castMultipleSpells(target2, target, castTime, poke, 1);

		castMultipleSpells(attacker, target, castTime, spell, 1);
		assertFalse("Target didn't die", target.isAlive());
		assertFalse("Target wasn't removed from combat", entityCombatService.isInCombat(target));
		assertFalse("Attacker wasn't removed from combat", entityCombatService.isInCombat(attacker));
		assertFalse("Target2 wasn't removed from combat", entityCombatService.isInCombat(target2));
	}

	@Test
	public void onlyFriendlyPlayersLeftInCombatMakesCombatEndInstantlyAfterMerge() throws Exception {
		Player attacker2 = playerFactory.createPlayer("alice");
		S2CProtocol attacker2Receiver = MockUtil.deepMock(S2CProtocol.class);
		entityService.addEntity(attacker2, attacker2Receiver);

		int damage = (int) (target.getBaseStats().getCurrentHealth().getValue() * 2);
		int castTime = 1000;
		ServerSpell spell = createInstantDamageSpell(castTime, damage, "Big Boom");

		ServerSpell poke = createInstantDamageSpell(castTime, 1, "Poke");

		castMultipleSpells(attacker, target, castTime, poke, 3);
		castMultipleSpells(attacker2, target2, castTime, poke, 3);

		castMultipleSpells(target, target2, castTime, poke, 1);

		castMultipleSpells(attacker, target, castTime, spell, 1);
		castMultipleSpells(attacker2, target2, castTime, spell, 1);

		assertFalse("Target didn't die", target.isAlive());
		assertFalse("Target didn't die", target2.isAlive());

		assertFalse("Target wasn't removed from combat", entityCombatService.isInCombat(target));
		assertFalse("Attacker wasn't removed from combat", entityCombatService.isInCombat(attacker));
		assertFalse("Attacker2 wasn't removed from combat", entityCombatService.isInCombat(attacker2));
		assertFalse("Target2 wasn't removed from combat", entityCombatService.isInCombat(target2));
	}



	@Test
	public void playerLeavesCombat() {
		PlayerMockFactory factory = new PlayerMockFactory.Builder(timeProvider, uuidFactory).creatureType(creatureType).build();
		Player coward = factory.createPlayer("coward");
		entityService.addEntity(coward, MockUtil.deepMock(S2CProtocol.class));


		int castTime = 1000;
		ServerSpell spell = createInstantDamageSpell(castTime, 1, "Poke");

		castMultipleSpells(coward, target, castTime, spell, 1);
		castMultipleSpells(attacker, target, castTime, spell, 1);

		assertTrue("attacker is not in combat", entityCombatService.isInCombat(attacker));
		assertTrue("target is not in combat", entityCombatService.isInCombat(target));
		assertTrue("coward is not in combat", entityCombatService.isInCombat(coward));

		tick(1000L);
		castMultipleSpells(attacker, target, castTime, spell, 10);
		assertFalse("coward is still in combat", entityCombatService.isInCombat(coward));
		assertTrue("attacker is still in combat", entityCombatService.isInCombat(attacker));
		assertTrue("target is still in combat", entityCombatService.isInCombat(target));
	}


	@Test
	public void testLongCombatAndThenStopAttack() {

		target.getBaseStats().getShieldStrength().changeValue(0);
		target.getBaseStats().getShieldRecoveryRate().changeValue(0);

		assertTrue(actionScheduler.isEmpty());
		target.getBaseStats().getStamina().changeValue(20);
		target.getBaseStats().getCurrentHealth().changeValue(Integer.MAX_VALUE);


		int castTime = 1000;
		int damage = 10;
		ServerSpell spell = createInstantDamageSpell(castTime, damage, "Boom");
		spellCombatService.startSpellCast(attacker, target, spell, timeProvider.now(), null);

		tick(0);
		double hpBefore = target.getBaseStats().getCurrentHealth().getValue();
		double shieldRecovery = target.getBaseStats().getShieldRecoveryRate().getValue();

		assertFalse(actionScheduler.isEmpty());

		tick(castTime);
		double shieldWhenCombatStarts = castTime / 1000.0 * shieldRecovery;
		assertEquals(hpBefore + shieldWhenCombatStarts - damage, target.getBaseStats().getCurrentHealth().getValue(), EPSILON);
		assertEquals(0, target.getBaseStats().getShieldStrength().getValue(), EPSILON);

		castMultipleSpells(attacker, target, castTime, spell, 13);
		assertEquals(hpBefore + shieldWhenCombatStarts - (damage * 14), target.getBaseStats().getCurrentHealth().getValue(), EPSILON);

		assertFalse(actionScheduler.isEmpty());

		castMultipleSpells(attacker, target, castTime, spell, 1);

		assertEquals(0, target.getBaseStats().getCurrentHealth().getValue(), EPSILON);

		// only combat should still be in queue
		assertEquals(1, actionScheduler.size());

		tick(castTime);
		assertEquals(0, target.getBaseStats().getCurrentHealth().getValue(), EPSILON);

		assertEquals(0, entityCombatService.numberOfCombat());
		// wait for the combat to end
		tick(10000L);
		assertTrue(actionScheduler.isEmpty());
	}


	@Test
	public void mergeCombat() {
		assertEquals(0, entityCombatService.numberOfCombat());

		Player attacker2 = playerFactory.createPlayer("alice");
		S2CProtocol attacker2Receiver = MockUtil.deepMock(S2CProtocol.class);
		entityService.addEntity(attacker2, attacker2Receiver);
		entityTargetService.setTarget(attacker, target);

		int castTime = 1000;
		ServerSpell spell = createInstantDamageSpell(castTime, 10, "Boom");
		spellCombatService.startSpellCast(attacker, target, spell, timeProvider.now(), null);

		tick(castTime);

		assertEquals(1, entityCombatService.numberOfCombat());

		tick(100);

		entityTargetService.setTarget(attacker2, target2);
		spellCombatService.startSpellCast(attacker2, target2, spell, timeProvider.now(), null);
		tick(castTime);

		assertEquals(2, entityCombatService.numberOfCombat());


		spellCombatService.startSpellCast(attacker, target, spell, timeProvider.now(), null);
		spellCombatService.startSpellCast(attacker2, target2, spell, timeProvider.now(), null);

		tick(castTime);

		spellCombatService.startSpellCast(attacker, target2, spell, timeProvider.now(), null);

		verifyOnce().on(attackerReceiver.combat()).entityStartedSpellCast(eq(attacker), eq(target2), eq(spell));
		verifyOnce().on(target1Receiver.combat()).entityStartedSpellCast(eq(attacker), eq(target2), eq(spell));
		verifyOnce().on(target2Receiver.combat()).entityStartedSpellCast(eq(attacker), eq(target2), eq(spell));
		verifyOnce().on(attacker2Receiver.combat()).entityStartedSpellCast(eq(attacker), eq(target2), eq(spell));

		tick(castTime);

		assertEquals(1, entityCombatService.numberOfCombat());
	}

	@Test
	public void combatStartIsSentToArea() throws Exception {
		ServerSpell spell = createInstantDamageSpell(100, 10, "Ouch");
		spellCombatService.startSpellCast(attacker, target, spell, timeProvider.now(), null);

		tick(150);

		verifyOnce().on(target2Receiver.combat()).combatStatusChanged(attacker, true);
		verifyOnce().on(target2Receiver.combat()).combatStatusChanged(target, true);
	}

	@Test
	public void combatEndIsSentToArea() throws Exception {
		ServerSpell spell = createInstantDamageSpell(100, 10, "Ouch");
		spellCombatService.startSpellCast(attacker, target, spell, timeProvider.now(), null);

		tick(150);
		getData(target2Receiver).resetCalls();
		tick(10000L);
		verifyOnce().on(target2Receiver.combat()).combatStatusChanged(attacker, false);
		verifyOnce().on(target2Receiver.combat()).combatStatusChanged(target, false);
	}

	private ServerSpell createInstantDamageSpell(int castTime, int damage, String name) {
		Effect effect = new DamageSchoolEffect(spellCombatService, smrtBroadcaster, new IntervalInt(damage, damage), MagicSchool.FIRE);
		return new ServerSpell.Builder(name).castTime(castTime).addSpellEffect(effect).build();
	}
}
