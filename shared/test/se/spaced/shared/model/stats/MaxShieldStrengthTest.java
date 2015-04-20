package se.spaced.shared.model.stats;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MaxShieldStrengthTest {
	private static final double EPSILON = 1e-4;
	private SimpleStat shieldEfficiency;
	private SimpleStat baseShieldStrength;
	private AuraStats shieldStrength;
	private MaxShieldStrength maxShieldStrength;

	@Before
	public void setUp() throws Exception {
		shieldEfficiency = new SimpleStat("ShieldEfficiency", 1.0);
		baseShieldStrength = new SimpleStat("BaseShieldStrength", 0);
		shieldStrength = new AuraStats(baseShieldStrength);
		maxShieldStrength = new MaxShieldStrength(shieldEfficiency, shieldStrength);
	}

	@Test
	public void create() throws Exception {
		assertEquals(0, maxShieldStrength.getValue(), EPSILON);
	}

	@Test
	public void zeroEfficiency() throws Exception {
		baseShieldStrength.changeValue(30);
		shieldEfficiency.changeValue(0);
		assertEquals(0, maxShieldStrength.getValue(), EPSILON);

		baseShieldStrength.changeValue(100);
		assertEquals(0, maxShieldStrength.getValue(), EPSILON);
	}

	@Test
	public void changeBase() throws Exception {
		baseShieldStrength.changeValue(30);
		assertEquals(30, maxShieldStrength.getValue(), EPSILON);

		baseShieldStrength.changeValue(100);
		assertEquals(100, maxShieldStrength.getValue(), EPSILON);
	}

	@Test
	public void efficiencyUpFromZero() throws Exception {
		shieldEfficiency.changeValue(0);

		baseShieldStrength.changeValue(42);

		shieldEfficiency.changeValue(0.5);
		assertEquals(21, maxShieldStrength.getValue(), EPSILON);
	}

	@Test
	public void changeBoth() throws Exception {
		shieldEfficiency.changeValue(2.0);
		baseShieldStrength.changeValue(33);
		assertEquals(66, maxShieldStrength.getValue(), EPSILON);
	}
}
