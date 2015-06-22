package se.spaced.server.model.spawn.area;

import com.google.common.collect.Lists;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static se.mockachino.Mockachino.*;

public class SpawnAreaComparatorTest {

	@Test
	public void compare() throws Exception {
		SpawnAreaComparator comparator = new SpawnAreaComparator();

		SpawnArea a = mock(SpawnArea.class);
		when(a.getSpawnCount()).thenReturn(2);
		SpawnArea b = mock(SpawnArea.class);
		when(b.getSpawnCount()).thenReturn(1);
		SpawnArea c = mock(SpawnArea.class);
		when(c.getSpawnCount()).thenReturn(3);
		ArrayList<SpawnArea> areas = Lists.newArrayList(a, b, c);
		Collections.sort(areas, comparator);

		assertEquals(b, areas.get(0));
		assertEquals(a, areas.get(1));
		assertEquals(c, areas.get(2));

	}
}
