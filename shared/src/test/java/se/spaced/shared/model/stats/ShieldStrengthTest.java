package se.spaced.shared.model.stats;

import org.junit.Before;
import org.junit.Test;
import se.fearless.common.stats.SimpleStat;
import se.fearless.common.time.MockTimeProvider;

import static org.junit.Assert.assertEquals;

public class ShieldStrengthTest {
	private MockTimeProvider timeProvider;
	private SimpleStat maxShieldPower;
	private SimpleStat shieldRecoveryRate;
	private ShieldStrength shieldStrength;
	private static final double EPSILON = 1e-10;

	@Before
	public void setUp() throws Exception {
		timeProvider = new MockTimeProvider();
		maxShieldPower = new SimpleStat("Max Shield Power", 10);
		shieldRecoveryRate = new SimpleStat("Shield Recovery Rate", 0.2);
		shieldStrength = new ShieldStrength(timeProvider, maxShieldPower, shieldRecoveryRate);
	}

	@Test
	public void cappedByMax() throws Exception {
		assertEquals(10, shieldStrength.getValue(), EPSILON);

		shieldStrength.increaseValue(5);
		assertEquals(10, shieldStrength.getValue(), EPSILON);
	}

	@Test
	public void cappedAtZero() throws Exception {
		shieldStrength.decreaseValue(20);
		assertEquals(0, shieldStrength.getValue(), EPSILON);
	}

	@Test
	public void recoversAfterCappingAtZero() throws Exception {
		shieldStrength.decreaseValue(20);
		timeProvider.advanceTime(5000);
		assertEquals(1, shieldStrength.getValue(), EPSILON);
	}


	@Test
	public void recoversOverTime() throws Exception {
		shieldStrength.decreaseValue(5);
		assertEquals(5, shieldStrength.getValue(), EPSILON);

		timeProvider.advanceTime(5000);
		assertEquals(6, shieldStrength.getValue(), EPSILON);
	}

	@Test
	public void recoveryIsCappedAtMax() throws Exception {
		shieldStrength.decreaseValue(5);
		assertEquals(5, shieldStrength.getValue(), EPSILON);

		timeProvider.advanceTime(60 * 60 * 1000); // move ahead an hour
		assertEquals(10, shieldStrength.getValue(), EPSILON);
	}


	@Test
	public void changeRecoveryRate() throws Exception {
		shieldStrength.decreaseValue(5);
		timeProvider.advanceTime(5000);
		shieldRecoveryRate.changeValue(1);

		timeProvider.advanceTime(2000);

		assertEquals(8, shieldStrength.getValue(), EPSILON);
	}

	@Test
	public void maxDecreases() throws Exception {
		maxShieldPower.changeValue(8);
		assertEquals(8, shieldStrength.getValue(), EPSILON);
	}

	@Test
	public void maxDecreasesThenGoesBackUpAndShieldRecovers() throws Exception {
		maxShieldPower.changeValue(8);
		maxShieldPower.changeValue(10);
		assertEquals(8, shieldStrength.getValue(), EPSILON);

		timeProvider.advanceTime(5000);
		assertEquals(9, shieldStrength.getValue(), EPSILON);

		timeProvider.advanceTime(5000);
		assertEquals(10, shieldStrength.getValue(), EPSILON);

		timeProvider.advanceTime(5000);
		assertEquals(10, shieldStrength.getValue(), EPSILON);

	}
}
