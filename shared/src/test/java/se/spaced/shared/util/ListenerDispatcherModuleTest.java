package se.spaced.shared.util;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.name.Names;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static se.mockachino.Mockachino.mock;
import static se.mockachino.Mockachino.verifyOnce;

public class ListenerDispatcherModuleTest {
	@Test
	public void createsListenerDispatcher() {
		Injector injector = Guice.createInjector(new AbstractListenerDispatcherModule() {
			@Override
			protected void configure() {
				register(Listener.class);
			}
		});

		DispatchingClass dispatchingClass = injector.getInstance(DispatchingClass.class);
		
		assertNotNull(dispatchingClass.listenerDispatcher);
	}

	@Test
	public void connectsListeningClasses() {
		final Listener listener = mock(Listener.class);

		Injector injector = Guice.createInjector(new AbstractListenerDispatcherModule() {
			@Override
			protected void configure() {
				register(Listener.class);

				bind(Listener.class).toInstance(listener);
			}
		});

		injector.getInstance(DispatchingClass.class).triggerTestEvent();

		verifyOnce().on(listener).testEvent();
	}

	@Test
	public void connectsMultipleListeningClasses() {
		final Listener listener = mock(Listener.class);
		final Listener listener2 = mock(Listener.class);

		Injector injector = Guice.createInjector(new AbstractListenerDispatcherModule() {
			@Override
			protected void configure() {
				register(Listener.class);

				bind(Listener.class).toInstance(listener);
				bind(Listener.class).annotatedWith(Names.named("listener2")).toInstance(listener2);
			}
		});

		injector.getInstance(DispatchingClass.class).triggerTestEvent();

		verifyOnce().on(listener).testEvent();
		verifyOnce().on(listener2).testEvent();
	}

	interface Listener {
		void testEvent();
	}

	static class DispatchingClass {
		final ListenerDispatcher<Listener> listenerDispatcher;

		@Inject
		DispatchingClass(ListenerDispatcher<Listener> listenerDispatcher) {
			this.listenerDispatcher = listenerDispatcher;
		}

		void triggerTestEvent() {
			listenerDispatcher.trigger().testEvent();
		}
	}
}
