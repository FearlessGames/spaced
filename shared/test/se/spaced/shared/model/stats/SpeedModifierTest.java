package se.spaced.shared.model.stats;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SpeedModifierTest {

	private static final double EPSILON = 1e-8;

	@Test
	public void simple() throws Exception {
		SpeedModifierStat speedModifierStat = new SpeedModifierStat(1.0);

		speedModifierStat.decreaseValue(0.2);
		assertEquals(0.8, speedModifierStat.getValue(), EPSILON);
		speedModifierStat.decreaseValue(1.0);
		assertEquals(0.0, speedModifierStat.getValue(), EPSILON);
	}
}
