package se.spaced.shared.model.stats;

import org.junit.Before;
import org.junit.Test;
import se.fearlessgames.common.util.MockTimeProvider;
import se.spaced.shared.model.aura.ModStat;

import static org.junit.Assert.assertEquals;

public class EntityStatsTest {
	private static final double EPSILON = 1e-10;
	private static final int STAMINA = 20;
	private static final int MAX_CHARGE = 35;
	private static final double SHIELD_RECOVERY = 0.4;
	private static final double BASE_SHIELD_EFFICIENCY = 2.0;
	private MockTimeProvider timeProvider;

	@Before
	public void setUp() throws Exception {
		timeProvider = new MockTimeProvider();
	}

	@Test
	public void createFromStatData() {
		StatData data = new StatData(STAMINA, MAX_CHARGE, SHIELD_RECOVERY, BASE_SHIELD_EFFICIENCY, EntityStats.IN_COMBAT_COOLRATE, 0.5);
		EntityStats entityStats = new EntityStats(new MockTimeProvider(), data);
		assertEquals("Stamina isn't saved correctly", STAMINA, entityStats.getStamina().getValue(), EPSILON);
		assertEquals("Stamina to health conversion is wrong",
				STAMINA * EntityStats.STAMINA_TO_HEALTH_FACTOR, entityStats.getMaxHealth().getValue(), 0.1);

		assertEquals("Health isn't calculated correctly",
				STAMINA * EntityStats.STAMINA_TO_HEALTH_FACTOR, entityStats.getCurrentHealth().getValue(), 0.1);

		assertEquals(MAX_CHARGE, entityStats.getShieldCharge().getValue(), EPSILON);
		assertEquals(SHIELD_RECOVERY, entityStats.getBaseShieldRecovery().getValue(), EPSILON);
		double maxShieldStrength = entityStats.getMaxShieldStrength().getValue();
		assertEquals(SHIELD_RECOVERY + maxShieldStrength/EntityStats.SECONDS_TO_FULL_SHIELD_RECOVERY, entityStats.getShieldRecoveryRate().getValue(), EPSILON);

		assertEquals(MAX_CHARGE * BASE_SHIELD_EFFICIENCY, entityStats.getMaxShieldStrength().getValue(), EPSILON);
		assertEquals(1.0, entityStats.getSpeedModifier().getValue(), EPSILON);

		assertEquals(0.5, entityStats.getAttackRating().getValue(), EPSILON);
	}

	@Test
	public void update() {
		MockTimeProvider timeProvider = new MockTimeProvider();
		EntityStats stats = new EntityStats(timeProvider);
		stats.getStamina().changeValue(10);
		stats.getCurrentHealth().changeValue(stats.getMaxHealth().getValue());
		stats.getCurrentHealth().decreaseValue(5);
		stats.getHeat().setValue(40);
		stats.getMaxHeat().changeValue(35);
		stats.getCoolRate().changeValue(2);
		stats.getBaseShieldCharge().decreaseValue(2);
		stats.getShieldStrength().decreaseValue(2);
		stats.getShieldRecoveryRate().increaseValue(0.1);
		stats.getBaseShieldEfficiency().decreaseValue(0.2);
		stats.getSpeedModifier().addModStat(new ModStat(-0.5, StatType.SPEED, Operator.ADD));
		stats.getAttackRating().changeValue(1.2);

		EntityStats stats2 = new EntityStats(timeProvider);
		stats2.update(stats);

		assertEquals(0.5, stats2.getSpeedModifier().getValue(), EPSILON);
		assertEquals(stats, stats2);
		timeProvider.advanceTime(1000);
		assertEquals(stats, stats2);
		assertEquals(1.2, stats2.getAttackRating().getValue(), EPSILON);
	}

	@Test
	public void healWhenBuffedUp() {
		EntityStats stats = new EntityStats(timeProvider);
		stats.getStamina().changeValue(10);

		assertEquals(70, stats.getMaxHealth().getValue(), EPSILON);
		ModStat stat = new ModStat(5, StatType.STAMINA, Operator.ADD);
		stats.getAuraStatByType(StatType.STAMINA).addModStat(stat);
		assertEquals(105, stats.getMaxHealth().getValue(), EPSILON);

		stats.getAuraStatByType(StatType.STAMINA).removeModStat(stat);
		assertEquals(70, stats.getMaxHealth().getValue(), EPSILON);
	}

	@Test
	public void buffAttackRating() throws Exception {
		EntityStats stats = new EntityStats(timeProvider);

		AuraStats attackRating = stats.getAttackRating();
		attackRating.addModStat(new ModStat(EntityStats.ATTACK_RATING_PER_ATTACK_PERCENT_MULTIPLIER, StatType.ATTACK_RATING, Operator.ADD));

		AttackModifier attackModifier = stats.getAttackModifier();
		assertEquals(1.01, attackModifier.getValue(), EPSILON);
	}
}
