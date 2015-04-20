package se.spaced.shared.util;

import org.junit.Test;

import static se.mockachino.Mockachino.*;


public class ListenerDispatcherTest {
	@Test
	public void testSimple() {
		ListenerDispatcher<TestInterface> dispatcher = ListenerDispatcher.create(TestInterface.class);
		TestInterface listener1 = mock(TestInterface.class);
		TestInterface listener2 = mock(TestInterface.class);
		dispatcher.addListener(listener1);
		dispatcher.addListener(listener2);
		dispatcher.trigger().method("My call");
		verifyOnce().on(listener1).method("My call");
		verifyOnce().on(listener2).method("My call");

	}

	@Test
	public void testNPE() {
		ListenerDispatcher<TestInterface> dispatcher = ListenerDispatcher.create(TestInterface.class);
		dispatcher.trigger().method("my call");

	}

	private static interface TestInterface {
		void method(String arg1);
	}
}
