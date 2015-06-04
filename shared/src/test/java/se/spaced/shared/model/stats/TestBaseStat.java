package se.spaced.shared.model.stats;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


public class TestBaseStat {
	private static final double EPSILON = 0.000001;
	private SimpleStat strength;

	@Before
	public void setUp() throws Exception {
		strength = new SimpleStat("Strength", 100);
	}

	@Test
	public void create() {
		assertEquals("Init failed", 100, strength.getValue(), EPSILON);
		assertEquals("NormalType failed", 100, strength.getValue(), EPSILON);
		assertTrue(strength.toString().contains("Strength"));
		assertTrue(strength.toString().contains("100"));
	}


	@Test
	public void changeCurrent() {

		strength.changeValue(50);
		assertEquals("Set failed", 50, strength.getValue(), EPSILON);

		strength.increaseValue(-20);
		assertEquals("Change failed", 30, strength.getValue(), EPSILON);

		strength.changeValue(50);
		strength.increaseValue(70);
		assertEquals("Change failed", 120, strength.getValue(), EPSILON);

		strength.decreaseValue(10);
		assertEquals("Change failed", 110, strength.getValue(), EPSILON);
	}

	@Test
	public void bounds() {
		strength.decreaseValue(140);
		assertEquals("Change below 0 failed", 0, strength.getValue(), EPSILON);

		strength.changeValue(-30);
		assertEquals("Set below 0 failed", 0, strength.getValue(), EPSILON);
	}

	@Test
	public void equalsAndHash() {
		assertFalse(strength.equals("Strength"));

		assertEquals(strength, strength);
		SimpleStat strength2 = new SimpleStat("Strength", 100);
		assertEquals(strength, strength2);
		assertEquals(strength.hashCode(), strength2.hashCode());

		strength2.decreaseValue(10);
		assertFalse(strength.equals(strength2));
		assertFalse(strength.hashCode() == strength2.hashCode());
	}
}
