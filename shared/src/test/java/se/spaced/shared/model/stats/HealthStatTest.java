package se.spaced.shared.model.stats;

import org.junit.Before;
import org.junit.Test;
import se.fearless.common.publisher.Subscriber;
import se.fearless.common.time.MockTimeProvider;

import static org.junit.Assert.assertEquals;
import static se.mockachino.Mockachino.*;
import static se.mockachino.matchers.Matchers.*;


public class HealthStatTest {
	private static final double EPSILON = 0.000001;
	private MockTimeProvider timeProvider;
	private HealthStat health;
	private SimpleStat maxHealth;
	private SimpleStat healthRegenRate;

	@Before
	public void setup() {
		timeProvider = new MockTimeProvider();
		maxHealth = new SimpleStat("maxHealth", 100);
		healthRegenRate = new SimpleStat("healthRegenRate", 1);
		health = new HealthStat(timeProvider, 100, maxHealth, healthRegenRate);
	}

	@Test
	public void initialValue() {
		assertEquals(maxHealth.getValue(), health.getValue(), EPSILON);
	}

	@Test
	public void healthRegens() {
		health.changeValue(90);
		assertEquals(90, health.getValue(), EPSILON);
		timeProvider.advanceTime(1000);
		assertEquals(91, health.getValue(), EPSILON);
		timeProvider.advanceTime(1000);
		assertEquals(92, health.getValue(), EPSILON);
	}

	@Test
	public void notifySubscribers() {
		Subscriber<Stat> obs = mock(Subscriber.class);
		health.subscribe(obs);

		health.getValue();
		verifyNever().on(obs).update(any(Stat.class));

		health.changeValue(10);
		verifyOnce().on(obs).update(any(Stat.class));

		health.increaseValue(10);
		verifyExactly(2).on(obs).update(any(Stat.class));

		health.decreaseValue(10);
		verifyExactly(3).on(obs).update(any(Stat.class));
	}

	@Test
	public void setThenGet() {
		health.changeValue(30);
		assertEquals(30, health.getValue(), EPSILON);
	}

	@Test
	public void increaseHealth() {
		health.changeValue(40);
		health.increaseValue(10);

		assertEquals(50, health.getValue(), EPSILON);
	}

	@Test
	public void decreaseHealth() {
		health.changeValue(40);
		health.decreaseValue(10);

		assertEquals(30, health.getValue(), EPSILON);
	}
}
