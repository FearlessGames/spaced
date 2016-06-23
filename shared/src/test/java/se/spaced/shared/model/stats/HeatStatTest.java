package se.spaced.shared.model.stats;

import org.junit.Before;
import org.junit.Test;
import se.fearless.common.publisher.Subscriber;
import se.fearless.common.stats.SimpleStat;
import se.fearless.common.stats.Stat;
import se.fearless.common.time.MockTimeProvider;

import static org.junit.Assert.assertEquals;
import static se.mockachino.Mockachino.*;
import static se.mockachino.matchers.Matchers.any;


public class HeatStatTest {
	private static final double EPSILON = 0.00001;
	private MockTimeProvider timeProvider;
	private HeatStat heat;
	private SimpleStat maxHeat;
	private SimpleStat coolRate;

	@Before
	public void setup() {
		timeProvider = new MockTimeProvider();
		maxHeat = new SimpleStat("maxHeat", 100);
		coolRate = new SimpleStat("coolRate", 10);
		heat = new HeatStat(timeProvider, maxHeat, coolRate);
	}

	@Test
	public void initialValue() {
		assertEquals(0, heat.getValue(), EPSILON);
	}

	@Test
	public void warmup() {
		heat.generate(10);
		assertEquals(10, heat.getValue(), EPSILON);
		timeProvider.advanceTime(500);
		assertEquals(5, heat.getValue(), EPSILON);

		timeProvider.advanceTime(500);
		assertEquals(0, heat.getValue(), EPSILON);

		timeProvider.advanceTime(500);
		assertEquals(0, heat.getValue(), EPSILON);
	}

	@Test
	public void notifySubscribers() {
		Subscriber<Stat> obs = mock(Subscriber.class);
		heat.subscribe(obs);

		heat.getValue();
		verifyNever().on(obs).update(any(Stat.class));

		heat.generate(10);
		verifyExactly(1).on(obs).update(any(Stat.class));

		heat.generate(20);
		verifyExactly(2).on(obs).update(any(Stat.class));

		heat.setValue(30);
		verifyExactly(3).on(obs).update(any(Stat.class));

		maxHeat.changeValue(20);
		verifyExactly(4).on(obs).update(any(Stat.class));
	}

	@Test
	public void setThenGet() {
		heat.setValue(30);
		assertEquals(30, heat.getValue(), EPSILON);
	}
}
