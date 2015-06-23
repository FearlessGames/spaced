package se.spaced.server.model.aura;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.fearless.common.mock.MockUtil;
import se.fearless.common.uuid.UUID;
import se.spaced.messages.protocol.s2c.S2CProtocol;
import se.spaced.server.ScenarioTestBase;
import se.spaced.server.model.ServerEntity;
import se.spaced.server.model.spawn.MobTemplate;
import se.spaced.server.model.spell.effect.DamageSchoolEffect;
import se.spaced.server.model.spell.effect.HealEffect;
import se.spaced.shared.model.MagicSchool;
import se.spaced.shared.model.aura.ModStat;
import se.spaced.shared.model.stats.Operator;
import se.spaced.shared.model.stats.StatType;
import se.spaced.shared.util.math.interval.IntervalInt;

import static org.junit.Assert.*;


public class AuraServiceTest extends ScenarioTestBase {
	private final Logger log = LoggerFactory.getLogger(getClass());
	private static final Long DURATION = 4000L;
	private static final UUID AURA_PK = new UUID(4226, 1253215);

	private ServerAura serverAura;
	private ServerEntity performer;
	private S2CProtocol receiver;
	private S2CProtocol targetReceiver;
	private ServerEntity target;
	private static final double EPSILON = 1e-10;

	@Before
	public void setup() {
		serverAura = new ModStatAura("Some AuraTemplate",
				"",
				DURATION,
				true,
				0,
				true,
				new ModStat(10.0, StatType.STAMINA, Operator.ADD));
		serverAura.setPk(AURA_PK);

		MobTemplate template = new MobTemplate.Builder(uuidFactory.randomUUID(), "Mr Mob").stamina(12).build();
		performer = template.createMob(timeProvider, new UUID(523626, 12214), randomProvider);
		receiver = MockUtil.deepMock(S2CProtocol.class);
		targetReceiver = MockUtil.deepMock(S2CProtocol.class);

		entityService.addEntity(performer, receiver);

		target = template.createMob(timeProvider, new UUID(123L, 456L), randomProvider);
		entityService.addEntity(target, targetReceiver);

	}

	@Test
	public void cantHasAura() {
		assertFalse(auraService.hasAura(target, serverAura));
	}

	@Test
	public void canHasAura() {
		auraService.apply(performer, target, serverAura, timeProvider.now());
		assertTrue(auraService.hasAura(target, serverAura));
		tick(3999);
		assertTrue(auraService.hasAura(target, serverAura));
		tick(1);
		assertFalse(auraService.hasAura(target, serverAura));
	}

	@Test
	public void reapplyAura() {
		assertEquals(12.0, target.getBaseStats().getStamina().getValue(), AuraServiceTest.EPSILON);
		auraService.apply(performer, target, serverAura, timeProvider.now());
		assertTrue(auraService.hasAura(target, serverAura));
		assertEquals(22.0, target.getBaseStats().getStamina().getValue(), AuraServiceTest.EPSILON);

		tick(2000);
		auraService.apply(performer, target, serverAura, timeProvider.now());
		assertEquals(22.0, target.getBaseStats().getStamina().getValue(), AuraServiceTest.EPSILON);

		tick(2000);
		assertTrue(auraService.hasAura(target, serverAura));
		assertEquals(22.0, target.getBaseStats().getStamina().getValue(), AuraServiceTest.EPSILON);

		tick(1999);
		assertTrue(auraService.hasAura(target, serverAura));
		assertEquals(22.0, target.getBaseStats().getStamina().getValue(), AuraServiceTest.EPSILON);

		tick(1);
		assertFalse(auraService.hasAura(target, serverAura));
		assertEquals(12.0, target.getBaseStats().getStamina().getValue(), AuraServiceTest.EPSILON);

	}

	@Test
	public void testStackedAura() {
		serverAura = new ModStatAura("Some AuraTemplate",
				"",
				DURATION,
				true,
				1,
				true,
				new ModStat(10.0, StatType.STAMINA, Operator.ADD));

		assertEquals(12.0, target.getBaseStats().getStamina().getValue(), AuraServiceTest.EPSILON);
		auraService.apply(performer, target, serverAura, timeProvider.now());
		assertTrue(auraService.hasAura(target, serverAura));
		assertEquals(22.0, target.getBaseStats().getStamina().getValue(), AuraServiceTest.EPSILON);

		tick(2000);
		auraService.apply(performer, target, serverAura, timeProvider.now());
		assertEquals(32.0, target.getBaseStats().getStamina().getValue(), AuraServiceTest.EPSILON);

		tick(2000);
		assertEquals(22.0, target.getBaseStats().getStamina().getValue(), AuraServiceTest.EPSILON);

		tick(2000);
		assertEquals(12.0, target.getBaseStats().getStamina().getValue(), AuraServiceTest.EPSILON);

		assertFalse(auraService.hasAura(target, serverAura));
	}

	@Test
	public void simplePostMultiplyAura() {
		serverAura = new ModStatAura("Some AuraTemplate",
				"",
				DURATION,
				true,
				1,
				true,
				new ModStat(1.2, StatType.STAMINA, Operator.POST_MULTIPLY));

		assertEquals(12.0, target.getBaseStats().getStamina().getValue(), AuraServiceTest.EPSILON);

		auraService.apply(performer, target, serverAura, timeProvider.now());
		assertTrue(auraService.hasAura(target, serverAura));
		assertEquals(14.4, target.getBaseStats().getStamina().getValue(), AuraServiceTest.EPSILON);

		tick(DURATION - 1);
		assertEquals(14.4, target.getBaseStats().getStamina().getValue(), AuraServiceTest.EPSILON);

		tick(1);
		assertEquals(12.0, target.getBaseStats().getStamina().getValue(), AuraServiceTest.EPSILON);
		assertFalse(auraService.hasAura(target, serverAura));
	}

	@Test
	public void complexPostMultiplyAndAddAuras() {
		ModStatAura multiplyAura = new ModStatAura("Multiply template",
				"",
				DURATION,
				true,
				1,
				true,
				new ModStat(1.2, StatType.STAMINA, Operator.POST_MULTIPLY));

		multiplyAura.setPk(uuidFactory.combUUID());
		ModStatAura addAura = new ModStatAura("Add template",
				"",
				DURATION,
				true,
				1,
				true,
				new ModStat(10, StatType.STAMINA, Operator.ADD));

		addAura.setPk(uuidFactory.combUUID());
		assertEquals(12.0, target.getBaseStats().getStamina().getValue(), AuraServiceTest.EPSILON);

		auraService.apply(performer, target, addAura, timeProvider.now());
		assertTrue(auraService.hasAura(target, addAura));
		assertEquals(22.0, target.getBaseStats().getStamina().getValue(), AuraServiceTest.EPSILON);

		auraService.apply(performer, target, multiplyAura, timeProvider.now());
		assertTrue(auraService.hasAura(target, multiplyAura));
		assertTrue(auraService.hasAura(target, addAura));
		assertEquals(26.4, target.getBaseStats().getStamina().getValue(), AuraServiceTest.EPSILON);

		tick(DURATION / 2);
		auraService.apply(performer, target, addAura, timeProvider.now());
		assertEquals(38.4, target.getBaseStats().getStamina().getValue(), AuraServiceTest.EPSILON);

		tick(DURATION / 2 + 1);
		assertEquals(22.0, target.getBaseStats().getStamina().getValue(), AuraServiceTest.EPSILON);
		assertFalse(auraService.hasAura(target, multiplyAura));

	}


	@Test
	public void applyPeriodicDamage() throws Exception {
		int numberOfTicks = 4;
		long tickTime = DURATION / numberOfTicks;
		target.getBaseStats().getShieldStrength().changeValue(0);
		target.getBaseStats().getShieldRecoveryRate().changeValue(0);
		target.getBaseStats().getOutOfCombatShieldRecovery().disable();
		double startHealth = target.getBaseStats().getCurrentHealth().getValue();
		int totalDamage = (int) (startHealth * 0.8);
		int damage = totalDamage / numberOfTicks;
		DamageSchoolEffect damageSchoolEffect = new DamageSchoolEffect(spellCombatService,
				smrtBroadcaster,
				new IntervalInt(damage, damage),
				MagicSchool.FIRE
		);
		serverAura = new PeriodicEffectAura("Periodic aura template",
				"",
				DURATION,
				true,
				0,
				numberOfTicks,
				damageSchoolEffect);

		tick(10000);

		auraService.apply(performer, target, serverAura, timeProvider.now());

		assertTrue(auraService.hasAura(target, serverAura));

		tick(1);
		double tick1 = target.getBaseStats().getCurrentHealth().getValue();
		log.info("1 HP = {}", target.getBaseStats().getCurrentHealth().getValue());
		assertEquals(tick1, startHealth, EPSILON);

		tick(tickTime);
		double tick2 = target.getBaseStats().getCurrentHealth().getValue();
		log.info("2 HP = {}", target.getBaseStats().getCurrentHealth().getValue());
		assertTrue(tick2 < tick1);

		tick(tickTime);
		double tick3 = target.getBaseStats().getCurrentHealth().getValue();
		log.info("3 HP = {}", target.getBaseStats().getCurrentHealth().getValue());
		assertTrue(tick3 < tick2);

		tick(tickTime);
		double tick4 = target.getBaseStats().getCurrentHealth().getValue();
		log.info("4 HP = {}", target.getBaseStats().getCurrentHealth().getValue());
		assertTrue(tick4 < tick3);

		tick(tickTime);
		double tick5 = target.getBaseStats().getCurrentHealth().getValue();
		log.info("5 HP = {}", target.getBaseStats().getCurrentHealth().getValue());
		assertTrue(tick5 < tick4);

		tick(tickTime);
		assertFalse(auraService.hasAura(target, serverAura));

		double after = target.getBaseStats().getCurrentHealth().getValue();
		assertEquals(after, tick5, EPSILON);

		assertEquals(startHealth - damage * numberOfTicks, after, EPSILON);
	}

	@Test
	public void targetDiesAfterFirstTick() throws Exception {
		int numberOfTicks = 4;
		long tickTime = DURATION / numberOfTicks;
		target.getBaseStats().getShieldStrength().changeValue(0);
		target.getBaseStats().getShieldRecoveryRate().changeValue(0);
		double startHealth = target.getBaseStats().getCurrentHealth().getValue();
		int totalDamage = (int) (startHealth * 4.5);
		int damage = totalDamage / numberOfTicks;
		DamageSchoolEffect damageSchoolEffect = new DamageSchoolEffect(spellCombatService,
				smrtBroadcaster,
				new IntervalInt(damage, damage),
				MagicSchool.FIRE
		);
		serverAura = new PeriodicEffectAura("Periodic aura template",
				"",
				DURATION,
				true,
				0,
				numberOfTicks,
				damageSchoolEffect);

		tick(10000);

		auraService.apply(performer, target, serverAura, timeProvider.now());

		assertTrue(auraService.hasAura(target, serverAura));

		tick(1);
		double tick1 = target.getBaseStats().getCurrentHealth().getValue();
		log.info("1 HP = {}", target.getBaseStats().getCurrentHealth().getValue());
		assertEquals(startHealth, tick1, EPSILON);

		tick(tickTime);
		double tick2 = target.getBaseStats().getCurrentHealth().getValue();
		log.info("2 HP = {}", target.getBaseStats().getCurrentHealth().getValue());
		assertEquals(0, tick2, EPSILON);


		assertFalse(auraService.hasAura(target, serverAura));
	}

	@Test
	public void applyPeriodicHealing() throws Exception {
		int numberOfTicks = 4;
		long tickTime = DURATION / numberOfTicks;

		double startHealth = target.getBaseStats().getCurrentHealth().getValue();
		assertTrue(startHealth > 40);
		target.getBaseStats().getOutOfCombatHealthRegen().disable();
		target.getBaseStats().getCurrentHealth().changeValue(startHealth - 40);
		int healPerTick = 10;
		HealEffect healEffect = new HealEffect(spellCombatService,
				smrtBroadcaster,
				MagicSchool.FIRE,
				new IntervalInt(healPerTick, healPerTick)
		);
		serverAura = new PeriodicEffectAura("Periodic aura template", "", DURATION, true, 0, numberOfTicks, healEffect);

		tick(10000);

		auraService.apply(performer, target, serverAura, timeProvider.now());

		assertTrue(auraService.hasAura(target, serverAura));

		tick(1);
		double tick1 = target.getBaseStats().getCurrentHealth().getValue();
		log.info("1 HP = {}", target.getBaseStats().getCurrentHealth().getValue());
		assertEquals(tick1, startHealth - 40, EPSILON);

		tick(tickTime);
		double tick2 = target.getBaseStats().getCurrentHealth().getValue();
		log.info("2 HP = {}", target.getBaseStats().getCurrentHealth().getValue());
		assertTrue(tick2 > tick1);

		tick(tickTime);
		double tick3 = target.getBaseStats().getCurrentHealth().getValue();
		log.info("3 HP = {}", target.getBaseStats().getCurrentHealth().getValue());
		assertTrue(tick3 > tick2);

		tick(tickTime);
		double tick4 = target.getBaseStats().getCurrentHealth().getValue();
		log.info("4 HP = {}", target.getBaseStats().getCurrentHealth().getValue());
		assertTrue(tick4 > tick3);

		tick(tickTime);
		double tick5 = target.getBaseStats().getCurrentHealth().getValue();
		log.info("5 HP = {}", target.getBaseStats().getCurrentHealth().getValue());
		assertTrue(tick5 > tick4);

		tick(tickTime);
		assertFalse(auraService.hasAura(target, serverAura));

		double after = target.getBaseStats().getCurrentHealth().getValue();
		assertEquals(after, tick5, EPSILON);

		assertEquals(startHealth, after, EPSILON);
	}

	@Test
	public void applyTwiceRemoveOnceAuraShouldBePresent() throws Exception {
		ServerAura aura = new KeyAura("Some AuraTemplate",
				"",
				DURATION,
				true,
				true);

		auraService.apply(performer, target, aura, timeProvider.now());
		tick(100);
		auraService.apply(performer, target, aura, timeProvider.now());

		assertTrue(auraService.hasAura(target, aura));

		tick(100);
		auraService.removeInstance(target, aura);
		assertTrue(auraService.hasAura(target, aura));

		tick(100);
		auraService.removeInstance(target, aura);
		assertFalse(auraService.hasAura(target, aura));

	}
}
