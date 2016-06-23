package se.spaced.shared.model.stats;

import org.junit.Before;
import org.junit.Test;
import se.fearless.common.stats.SimpleStat;

import static org.junit.Assert.assertEquals;

public class OutOfCombatHealthRegenTest {

	private static final double EPSILON = 1e-10;

	private MaxHitPoints maxHealth;
	private SimpleStat stamina;
	private OutOfCombatHealthRegen oocHealthRegen;
	private double secondsToFullRegen;

	@Before
	public void setUp() throws Exception {
		stamina = new SimpleStat("stamina", 10);
		maxHealth = new MaxHitPoints(stamina, EntityStats.STAMINA_TO_HEALTH_FACTOR);
		secondsToFullRegen = 90.0;
		oocHealthRegen = new OutOfCombatHealthRegen(maxHealth, secondsToFullRegen);
	}

	@Test
	public void init() throws Exception {
		assertEquals(maxHealth.getValue() / secondsToFullRegen, oocHealthRegen.getValue(), EPSILON);
	}

	@Test
	public void updateStaminaChangesValue() throws Exception {
		stamina.changeValue(11);
		assertEquals(maxHealth.getValue() / secondsToFullRegen, oocHealthRegen.getValue(), EPSILON);
	}
}
