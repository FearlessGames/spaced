package se.spaced.shared.model.stats;

import org.junit.Test;
import se.fearlessgames.common.util.MockTimeProvider;
import se.fearlessgames.common.util.TimeProvider;

import static org.junit.Assert.assertEquals;

public class StatDataTest {
	private static final double EPSILON = 1e-10;

	@Test
	public void testIt() {
		StatData data = new StatData(10, 37, 3.1415926, 1.0, 0.123, 0.0);
		assertEquals(10*EntityStats.STAMINA_TO_HEALTH_FACTOR, data.getCurrentHealth(), EPSILON);

		data.setCurrentHealth(12);
		assertEquals(12, data.getCurrentHealth(), EPSILON);
		assertEquals(37, data.getBaseShieldCharge(), EPSILON);
		assertEquals(0.123, data.getBaseCoolRate(), EPSILON);

		TimeProvider timeProvider = new MockTimeProvider();
		EntityStats stats = new EntityStats(timeProvider, data);
		stats.getCurrentHealth().changeValue(13);
		stats.getBaseShieldCharge().changeValue(48);
		stats.getBaseShieldEfficiency().changeValue(1.1);
		stats.getBaseCoolRate().changeValue(0.234);
		data.update(stats);
		assertEquals(13, data.getCurrentHealth(), EPSILON);
		assertEquals(48, data.getBaseShieldCharge(), EPSILON);
		assertEquals(1.1, data.getBaseShieldEfficiency(), EPSILON);
		assertEquals(0.234, data.getBaseCoolRate(), EPSILON);

	}
}
