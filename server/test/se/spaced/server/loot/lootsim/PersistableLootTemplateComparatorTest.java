package se.spaced.server.loot.lootsim;

import com.google.common.collect.Lists;
import org.junit.Test;
import se.spaced.server.loot.PersistableLootTemplate;
import se.spaced.server.tools.loot.simulator.PersistableLootTemplateComparator;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static se.mockachino.Mockachino.*;

public class PersistableLootTemplateComparatorTest {

	@Test
	public void testOrder() {
		PersistableLootTemplate t1 = mock(PersistableLootTemplate.class);
		PersistableLootTemplate t2 = mock(PersistableLootTemplate.class);
		when(t2.getName()).thenReturn("boo");
		PersistableLootTemplate t3 = mock(PersistableLootTemplate.class);
		when(t3.getName()).thenReturn("foo");

		List<PersistableLootTemplate> loots = Lists.newArrayList(t1, t2, t3);

		Collections.sort(loots, new PersistableLootTemplateComparator());
		assertEquals(t2, loots.get(0));
		assertEquals(t3, loots.get(1));
		assertEquals(t1, loots.get(2));
	}

	@Test
	public void testOrderAllNamed() {
		PersistableLootTemplate t1 = mock(PersistableLootTemplate.class);
		when(t1.getName()).thenReturn("foo");
		PersistableLootTemplate t2 = mock(PersistableLootTemplate.class);
		when(t2.getName()).thenReturn("moo");
		PersistableLootTemplate t3 = mock(PersistableLootTemplate.class);
		when(t3.getName()).thenReturn("boo");

		List<PersistableLootTemplate> loots = Lists.newArrayList(t1, t2, t3);

		Collections.sort(loots, new PersistableLootTemplateComparator());
		assertEquals(t3, loots.get(0));
		assertEquals(t1, loots.get(1));
		assertEquals(t2, loots.get(2));
	}


}
