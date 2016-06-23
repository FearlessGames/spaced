package se.spaced.shared.model.stats;

import org.junit.Before;
import org.junit.Test;
import se.fearless.common.stats.CappedStat;
import se.fearless.common.stats.SimpleStat;

import static org.junit.Assert.assertEquals;

public class HybridStatTest {
	private SimpleStat stamina;
	private MaxHitPoints maxHealth;
	private CappedStat currentHealth;
	private static final double EPSILON = 0.001;

	@Before
	public void setUp() {
		stamina = new SimpleStat("Stamina", 10);
		maxHealth = new MaxHitPoints(stamina, 10);
		currentHealth = new CappedStat("CurrentHealth", Integer.MAX_VALUE, maxHealth);

	}

	@Test
	public void maxValue() {
		assertEquals("Bad max health", 100, maxHealth.getValue(), EPSILON);
	}

	@Test
	public void currentValue() {
		currentHealth.changeValue(70);
		assertEquals("Bad current health", 70, currentHealth.getValue(), EPSILON);
	}

	@Test
	public void changeCurrent() {
		double initialHp = currentHealth.getValue();
		currentHealth.increaseValue(-10);
		currentHealth.increaseValue(-10);
		currentHealth.increaseValue(-initialHp);
		assertEquals(0, currentHealth.getValue(), EPSILON);
		currentHealth.increaseValue(initialHp);
		assertEquals(initialHp, currentHealth.getValue(), EPSILON);
	}

	@Test
	public void maxDecresedBelowCurrent() {
		currentHealth.changeValue(70);

		stamina.changeValue(5);
		assertEquals("Bad current health", 50, currentHealth.getValue(), EPSILON);
	}

	@Test
	public void setHpToMuchMoreThanMax() {
		double hp = currentHealth.getValue();
		currentHealth.changeValue(Integer.MAX_VALUE);
		assertEquals("Current hp isn't capped", maxHealth.getValue(), currentHealth.getValue(), EPSILON);
	}
}
