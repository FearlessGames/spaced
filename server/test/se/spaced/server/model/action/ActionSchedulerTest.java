package se.spaced.server.model.action;

import com.google.common.collect.Lists;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class ActionSchedulerTest {

	@Test
	public void testCorrectOrder() {
		ActionScheduler scheduler = new ActionScheduler();
		ArrayList<String> list = Lists.newArrayList();

		for (int i = 0; i < 10; i++) {
			scheduler.add(new DummyAction(1 << 36, "middle" + i, list));
		}

		scheduler.add(new DummyAction(1 << 50, "last", list));
		scheduler.add(new DummyAction(1 << 40, "secondToLast", list));
		scheduler.add(new DummyAction(10, "second", list));
		scheduler.add(new DummyAction(0, "first", list));

		long now = 1l << 52;
		scheduler.tick(now);
		assertTrue(scheduler.isEmpty());
		assertEquals(14, list.size());
		assertEquals("first", list.get(0));
		assertEquals("second", list.get(1));
		for (int i = 0; i < 10; i++) {
			assertEquals("middle", list.get(i + 2).substring(0, 6));
		}
		assertEquals("secondToLast", list.get(12));
		assertEquals("last", list.get(13));
	}

	static class DummyAction extends Action {

		private final String s;
		private final List<String> list;

		protected DummyAction(long executionTime, String s, List<String> list) {
			super(executionTime);
			this.s = s;
			this.list = list;
		}

		@Override
		public void perform() {
			list.add(s);
		}

	}
}
