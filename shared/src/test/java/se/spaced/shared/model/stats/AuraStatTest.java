package se.spaced.shared.model.stats;

import org.junit.Before;
import org.junit.Test;
import se.fearless.common.stats.AuraStats;
import se.fearless.common.stats.ModStat;
import se.fearless.common.stats.Operator;
import se.fearless.common.stats.SimpleStat;

import static org.junit.Assert.assertEquals;

public class AuraStatTest {
	private static final double EPSILON = 1e-10;
	private SimpleStat stamina;
	private MaxHitPoints maxHp;
	private ModStat fortitude;
	private AuraStats auraStats;
	private ModStat fatigue;

	@Before
	public void setup() {
		stamina = new SimpleStat("Stamina", 10);
		auraStats = new AuraStats(stamina);
		maxHp = new MaxHitPoints(auraStats, 10);
		fortitude = new ModStat(2, SpacedStatType.STAMINA, Operator.ADD);
		fatigue = new ModStat(-3, SpacedStatType.STAMINA, Operator.ADD);

	}

	@Test
	public void applyFortitude() {
		assertEquals(100, maxHp.getValue(), EPSILON);

		auraStats.addModStat(fortitude);
		assertEquals(120, maxHp.getValue(), EPSILON);
	}

	@Test
	public void removeFortitude() {
		auraStats.addModStat(fortitude);

		auraStats.removeModStat(fortitude);
		assertEquals(100, maxHp.getValue(), EPSILON);
	}

	@Test
	public void applyMultipleFortitude() {
		auraStats.addModStat(fortitude);
		auraStats.addModStat(fortitude);
		auraStats.addModStat(fortitude);
		assertEquals(160, maxHp.getValue(), EPSILON);
	}

	@Test
	public void twoDifferentAuraScenario() {
		assertEquals(100, maxHp.getValue(), EPSILON);

		auraStats.addModStat(fatigue);
		assertEquals(70, maxHp.getValue(), EPSILON);

		auraStats.addModStat(fortitude);
		assertEquals(90, maxHp.getValue(), EPSILON);

		auraStats.removeModStat(fatigue);
		assertEquals(120, maxHp.getValue(), EPSILON);

		auraStats.removeModStat(fortitude);
		assertEquals(100, maxHp.getValue(), EPSILON);
	}

	@Test
	public void changeValue() {
		assertEquals(10, auraStats.getValue(), EPSILON);

		auraStats.changeValue(20);
		assertEquals(20, auraStats.getValue(), EPSILON);

		stamina.changeValue(15);
		assertEquals(15, auraStats.getValue(), EPSILON);

		auraStats.increaseValue(3);
		auraStats.decreaseValue(2);
		assertEquals(16, auraStats.getValue(), EPSILON);

	}
}
