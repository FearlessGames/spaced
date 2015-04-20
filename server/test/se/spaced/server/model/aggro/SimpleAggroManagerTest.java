package se.spaced.server.model.aggro;

import com.google.common.collect.Lists;
import org.junit.Test;
import se.spaced.server.model.Mob;
import se.spaced.server.model.ServerEntity;

import java.util.ArrayList;
import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static se.mockachino.Mockachino.*;

public class SimpleAggroManagerTest {
	@Test
	public void simpleTest() {
		AggroManager aggro = new SimpleAggroManager(1.0, new Random());
		Mob mob1 = mock(Mob.class);
		Mob mob2 = mock(Mob.class);
		Mob mob3 = mock(Mob.class);

		assertEquals(null, aggro.getMostHated());
		assertEquals(0, aggro.getHate(mob1));
		assertEquals(0, aggro.getHate(mob2));
		assertEquals(0, aggro.getHate(mob3));

		aggro.addHate(mob1, 10);
		assertEquals(10, aggro.getHate(mob1));
		assertEquals(mob1, aggro.getMostHated());

		aggro.clearHate(mob1);
		assertEquals(null, aggro.getMostHated());
		assertEquals(0, aggro.getHate(mob1));

		aggro.addHate(mob2, 12);
		assertEquals(12, aggro.getHate(mob2));
		assertEquals(mob2, aggro.getMostHated());

		aggro.clearAll();
		assertEquals(null, aggro.getMostHated());
		assertEquals(0, aggro.getHate(mob1));
		assertEquals(0, aggro.getHate(mob2));
		assertEquals(0, aggro.getHate(mob3));

		aggro.addHate(mob1, 11);
		aggro.addHate(mob2, 22);
		aggro.addHate(mob3, 33);
		assertEquals(mob3, aggro.getMostHated());
		aggro.addHate(mob3, -12);
		assertEquals(mob2, aggro.getMostHated());
		aggro.addHate(mob2, -12);
		assertEquals(mob3, aggro.getMostHated());
		aggro.addHate(mob3, -12);
		assertEquals(mob1, aggro.getMostHated());


		aggro.clearAll();
	}

	@Test
	public void testSameHate() {
		AggroManager aggro = new SimpleAggroManager(1.0, new Random());
		Mob mob1 = mock(Mob.class);
		Mob mob2 = mock(Mob.class);
		Mob mob3 = mock(Mob.class);

		aggro.addHate(mob1, 10);
		aggro.addHate(mob2, 10);
		aggro.addHate(mob3, 10);

		assertEquals(mob1, aggro.getMostHated());

		aggro.addHate(mob3, 1);
		assertEquals(mob3, aggro.getMostHated());

		aggro.addHate(mob2, 1);
		aggro.addHate(mob1, 1);
		assertEquals(mob3, aggro.getMostHated());

		aggro.clearHate(mob3);
		assertEquals(mob1, aggro.getMostHated());
	}

	@Test
	public void clearHate() throws Exception {
		AggroManager aggro = new SimpleAggroManager(1.0, new Random());
		Mob mob1 = mock(Mob.class);
		Mob mob2 = mock(Mob.class);

		aggro.addHate(mob1, 10);
		aggro.addHate(mob2, 8);

		assertTrue(aggro.isAggroWith(mob1));
		assertTrue(aggro.isAggroWith(mob2));

		aggro.clearHate(mob1);

		assertEquals(mob2, aggro.getMostHated());

		assertFalse(aggro.isAggroWith(mob1));
		assertTrue(aggro.isAggroWith(mob2));
	}

	@Test
	public void randomHated() throws Exception {
		AggroManager aggro = new SimpleAggroManager(1.0, new Random());
		Mob mob1 = mock(Mob.class);
		Mob mob2 = mock(Mob.class);
		Mob mob3 = mock(Mob.class);

		aggro.addHate(mob1, 2);
		aggro.addHate(mob2, 3);
		aggro.addHate(mob3, 4);
		ArrayList<ServerEntity> mobs = Lists.<ServerEntity>newArrayList(mob1, mob2, mob3);
		for (int i = 0; i < 1000; i++) {
			ServerEntity randomHated = aggro.getRandomHated();
			assertTrue(mobs.contains(randomHated));
		}
	}

	@Test
	public void randomHatedWithExclude() throws Exception {
		AggroManager aggro = new SimpleAggroManager(1.0, new Random());
		Mob mob1 = mock(Mob.class);
		Mob mob2 = mock(Mob.class);
		Mob mob3 = mock(Mob.class);
		Mob mob4 = mock(Mob.class);

		aggro.addHate(mob1, 2);
		aggro.addHate(mob2, 3);
		aggro.addHate(mob3, 4);
		aggro.addHate(mob4, 5);
		ArrayList<ServerEntity> mobs = Lists.<ServerEntity>newArrayList(mob1, mob2, mob3);
		for (int i = 0; i < 1000; i++) {
			ServerEntity randomHated = aggro.getRandomHated(mob4);
			assertTrue(mobs.contains(randomHated));
		}
	}


}
