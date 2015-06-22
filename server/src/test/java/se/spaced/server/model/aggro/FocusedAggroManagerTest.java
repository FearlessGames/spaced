package se.spaced.server.model.aggro;

import org.junit.Before;
import org.junit.Test;
import se.spaced.server.model.ServerEntity;

import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static se.mockachino.Mockachino.*;

public class FocusedAggroManagerTest {
	private AggroManager aggro;
	private ServerEntity entity1;
	private ServerEntity entity2;

	@Before
	public void setup() {
		aggro = new SimpleAggroManager(1.1, new Random());
		entity1 = mock(ServerEntity.class);
		entity2 = mock(ServerEntity.class);
	}

	@Test
	public void startFresh() {
		assertFalse(aggro.isAggroWith(entity1));
		assertFalse(aggro.isAggroWith(entity2));
		assertTrue(aggro.getMostHated() == null);
	}

	@Test
	public void testSimpleAggro() {
		aggro.addHate(entity1, 10);
		assertEquals(entity1, aggro.getMostHated());
		assertFalse(aggro.isAggroWith(entity2));

		aggro.addHate(entity2, 5);
		assertEquals(entity1, aggro.getMostHated());
		assertTrue(aggro.isAggroWith(entity1));
		assertTrue(aggro.isAggroWith(entity2));
	}

	@Test
	public void testAggroSwitch() throws Exception {
		aggro.addHate(entity1, 20);
		aggro.addHate(entity2, 20);
		assertEquals(entity1, aggro.getMostHated());
		assertTrue(aggro.isAggroWith(entity1));
		assertTrue(aggro.isAggroWith(entity2));

		aggro.addHate(entity2, 1);
		assertEquals(entity1, aggro.getMostHated());
		assertTrue(aggro.isAggroWith(entity1));
		assertTrue(aggro.isAggroWith(entity2));

		aggro.addHate(entity2, 1);
		assertEquals(entity1, aggro.getMostHated());
		assertTrue(aggro.isAggroWith(entity1));
		assertTrue(aggro.isAggroWith(entity2));

		aggro.addHate(entity2, 1);
		assertEquals(entity2, aggro.getMostHated());
		assertTrue(aggro.isAggroWith(entity1));
		assertTrue(aggro.isAggroWith(entity2));
	}
}
