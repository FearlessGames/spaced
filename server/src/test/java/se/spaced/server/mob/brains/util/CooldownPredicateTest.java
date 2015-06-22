package se.spaced.server.mob.brains.util;

import org.junit.Before;
import org.junit.Test;
import se.fearlessgames.common.util.MockTimeProvider;
import se.spaced.server.model.ServerEntity;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static se.mockachino.Mockachino.*;

public class CooldownPredicateTest {
	private CooldownPredicate cooldownPredicate;
	private MockTimeProvider timeProvider;

	@Before
	public void setUp() throws Exception {
		timeProvider = new MockTimeProvider();
		cooldownPredicate = new CooldownPredicate(timeProvider, 1000);
	}

	@Test
	public void singleMob() throws Exception {
		timeProvider.setNow(3000);
		ServerEntity entity = mock(ServerEntity.class);
		assertTrue(cooldownPredicate.apply(entity));
		assertTrue(cooldownPredicate.apply(entity));
		cooldownPredicate.updateLastTime(entity);
		assertFalse(cooldownPredicate.apply(entity));
		timeProvider.advanceTime(999);
		assertFalse(cooldownPredicate.apply(entity));
		timeProvider.advanceTime(1);
		assertTrue(cooldownPredicate.apply(entity));
	}

	@Test
	public void multipleMobs() throws Exception {
		ServerEntity entity1 = mock(ServerEntity.class);
		ServerEntity entity2 = mock(ServerEntity.class);

		assertTrue(cooldownPredicate.apply(entity1));
		assertTrue(cooldownPredicate.apply(entity2));
		assertTrue(cooldownPredicate.apply(entity1));
		assertTrue(cooldownPredicate.apply(entity2));

		cooldownPredicate.updateLastTime(entity1);

		assertFalse(cooldownPredicate.apply(entity1));
		assertTrue(cooldownPredicate.apply(entity2));
		assertFalse(cooldownPredicate.apply(entity1));
		assertTrue(cooldownPredicate.apply(entity2));

		timeProvider.advanceTime(500);

		assertFalse(cooldownPredicate.apply(entity1));
		assertTrue(cooldownPredicate.apply(entity2));
		assertFalse(cooldownPredicate.apply(entity1));
		assertTrue(cooldownPredicate.apply(entity2));

		cooldownPredicate.updateLastTime(entity2);

		assertFalse(cooldownPredicate.apply(entity1));
		assertFalse(cooldownPredicate.apply(entity2));
		assertFalse(cooldownPredicate.apply(entity1));
		assertFalse(cooldownPredicate.apply(entity2));

		timeProvider.advanceTime(500);

		assertTrue(cooldownPredicate.apply(entity1));
		assertFalse(cooldownPredicate.apply(entity2));
		assertTrue(cooldownPredicate.apply(entity1));
		assertFalse(cooldownPredicate.apply(entity2));

		timeProvider.advanceTime(500);

		assertTrue(cooldownPredicate.apply(entity1));
		assertTrue(cooldownPredicate.apply(entity2));
		assertTrue(cooldownPredicate.apply(entity1));
		assertTrue(cooldownPredicate.apply(entity2));

	}
}
