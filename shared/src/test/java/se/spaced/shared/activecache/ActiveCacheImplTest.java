package se.spaced.shared.activecache;

import org.junit.Before;
import org.junit.Test;

import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static se.mockachino.Mockachino.*;
import static se.mockachino.matchers.Matchers.*;

public class ActiveCacheImplTest {
	private KeyRequestHandler<Integer> keyRequestHandler;
	private CacheUpdateListener<Integer, String> listener;
	private ActiveCache<Integer, String> cache;

	@Before
	public void setUp() throws Exception {
		keyRequestHandler = mock(KeyRequestHandler.class);
		listener = mock(CacheUpdateListener.class);
		cache = new ActiveCacheImpl<Integer, String>(keyRequestHandler);
	}

	@Test
	public void runWhenReady() {
		cache.addListener(listener);
		Job<String> job1 = mock(Job.class);
		Job<String> job2 = mock(Job.class);
		cache.runWhenReady(3, job1);
		cache.runWhenReady(3, job2);
		cache.runWhenReady(3, job2);

		verifyOnce().on(keyRequestHandler).requestKey(3);
		verifyNever().on(job1).run(any(String.class));
		verifyNever().on(job2).run(any(String.class));
		verifyNever().on(listener).addedValue(anyInt(), any(String.class));
		cache.setValue(3, "Foo");

		verifyOnce().on(job1).run(any(String.class));
		verifyExactly(2).on(job2).run(any(String.class));

		verifyOnce().on(listener).addedValue(3, "Foo");

		cache.setValue(3, "bar");
		verifyOnce().on(job1).run(any(String.class));
		verifyExactly(2).on(job2).run(any(String.class));

		verifyOnce().on(listener).addedValue(3, "Foo");
		verifyOnce().on(listener).updatedValue(3, "Foo", "bar");

		Job<String> job3 = mock(Job.class);
		cache.runWhenReady(3, job3);
		verifyOnce().on(job3).run("bar");

		Collection<String> values = cache.getValues();
		assertEquals(1, values.size());
		assertTrue(values.contains("bar"));
	}

	@Test
	public void remove() {
		Job<String> job1 = mock(Job.class);
		cache.runWhenReady(4, job1);
		cache.setValue(4, "Foo");
		cache.addListener(listener);
		cache.delete(4);
		verifyOnce().on(listener).deletedValue(4, "Foo");
		assertFalse(cache.isKnown(4));
	}

	@Test
	public void removeNonExisting() {
		cache.addListener(listener);
		cache.delete(2);
		verifyNever().on(listener).deletedValue(2, any(String.class));
	}

	@Test
	public void isKnown() throws Exception {
		assertFalse(cache.isKnown(3));
		assertFalse(cache.isKnown(42));

		cache.setValue(3, "foo");

		assertTrue(cache.isKnown(3));
		assertFalse(cache.isKnown(42));

		cache.setValue(3, "bar");

		assertTrue(cache.isKnown(3));
		assertFalse(cache.isKnown(42));

		cache.delete(3);

		assertFalse(cache.isKnown(3));
		assertFalse(cache.isKnown(42));
	}

	@Test
	public void listenerCallbacks() throws Exception {
		cache.addListener(listener);
		CacheUpdateListener<Integer, String> listener2 = mock(CacheUpdateListener.class);
		cache.addListener(listener2);

		cache.setValue(1, "Ett");
		cache.setValue(4, "Fyra");
		verifyOnce().on(listener).addedValue(4, "Fyra");
		verifyOnce().on(listener2).addedValue(4, "Fyra");

		cache.setValue(4, "Fyra och en halv");
		verifyOnce().on(listener).updatedValue(4, "Fyra", "Fyra och en halv");
		verifyOnce().on(listener2).updatedValue(4, "Fyra", "Fyra och en halv");

		for (int i = 0; i < 5; i++) {
			cache.setValue(5, "Making sure it's 5");
		}
		verifyOnce().on(listener).addedValue(5, "Making sure it's 5");
		verifyExactly(4).on(listener).updatedValue(5, "Making sure it's 5", "Making sure it's 5");


		cache.delete(4);
		verifyOnce().on(listener).deletedValue(4, "Fyra och en halv");

		cache.clear();
		verifyOnce().on(listener).deletedValue(1, "Ett");
		verifyOnce().on(listener).deletedValue(5, "Making sure it's 5");

		getData(listener).resetCalls();
		cache.setValue(4, "Fyra");
		verifyOnce().on(listener).addedValue(4, "Fyra");
	}
}
