package se.spaced.shared.util.cache.impl;

import org.junit.Before;
import org.junit.Test;
import se.spaced.shared.util.cache.Cache;
import se.spaced.shared.util.cache.CacheLoader;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static se.mockachino.Mockachino.*;


public class ThreadSafeCacheTest {
	CacheLoader<String, TempObject> loader;
	Cache<String, TempObject> threadSafeCache;
	private static final int NUMBER_OF_THREADS = 100;
	private static final int NUMBER_OF_KEYS = 100;
	private static final int NUMBER_OF_RUNS = 20;

	@Before
	@SuppressWarnings("unchecked")
	public void setUp() {
		loader = mock(CacheLoader.class);
	}


	@Test
	public void testGet() {
		String key = "key";
		stubReturn(new TempObject(key)).on(loader).load(key);

		threadSafeCache = new ThreadSafeCache<String, TempObject>(loader);
		TempObject firstObject = threadSafeCache.get(key);
		assertNotNull(firstObject);
		TempObject secondObject = threadSafeCache.get(key);
		assertNotNull(secondObject);
		assertSame(firstObject, secondObject);
		verifyOnce().on(loader).load(key);

	}

	@Test
	public void testInvalidate() {
		String key = "key";
		stubReturn(new TempObject(key)).on(loader).load(key);

		threadSafeCache = new ThreadSafeCache<String, TempObject>(loader);
		TempObject firstObject = threadSafeCache.get(key);
		assertNotNull(firstObject);
		verifyExactly(1).on(loader).load(key);

		threadSafeCache.invalidate(key);
		TempObject thirdObject = threadSafeCache.get(key);
		assertNotNull(thirdObject);
		verifyExactly(2).on(loader).load(key);

	}

	@Test
	public void testInvalidateAll() {
		String key = "key";
		stubReturn(new TempObject(key)).on(loader).load(key);

		threadSafeCache = new ThreadSafeCache<String, TempObject>(loader);
		TempObject firstObject = threadSafeCache.get(key);
		assertNotNull(firstObject);
		verifyExactly(1).on(loader).load(key);

		threadSafeCache.invalidateAll();
		TempObject thirdObject = threadSafeCache.get(key);
		assertNotNull(thirdObject);
		verifyExactly(2).on(loader).load(key);
	}

	@Test
	public void testParalellGetWithSameKey() throws InterruptedException {
		LatchCacheLoader latchLoader = new LatchCacheLoader();

		threadSafeCache = new ThreadSafeCache<String, TempObject>(latchLoader);

		int numberOfThreads = 5;

		final CountDownLatch start = new CountDownLatch(numberOfThreads);
		final CountDownLatch done = new CountDownLatch(numberOfThreads);
		Runnable runner = new Runnable() {
			@Override
			public void run() {
				start.countDown();
				threadSafeCache.get("key");
				done.countDown();
			}
		};

		for (int i = 0; i < numberOfThreads; i++) {
			Thread thread = new Thread(runner);
			thread.start();
		}

		start.await();

		latchLoader.latch.countDown();

		done.await();

		assertEquals(1, latchLoader.invokeCount.intValue());

	}


	@Test
	public void testParalellGetWithSameKeyLoad() throws InterruptedException {
		long totalTime = 0;
		long maxTime = 0;
		long minTime = Long.MAX_VALUE;

		for (int i = 0; i < NUMBER_OF_RUNS; i++) {
			LatchCacheLoader latchLoader = new LatchCacheLoader();

			threadSafeCache = new ThreadSafeCache<String, TempObject>(latchLoader);

			final CountDownLatch start = new CountDownLatch(NUMBER_OF_THREADS);
			final CountDownLatch done = new CountDownLatch(NUMBER_OF_THREADS);
			final CountDownLatch goSignal = new CountDownLatch(1);

			for (int j = 0; j < NUMBER_OF_THREADS; j++) {
				final int keyExtension = j % NUMBER_OF_KEYS;
				Runnable runner = new Runnable() {
					@Override
					public void run() {
						start.countDown();
						try {
							goSignal.await();
						} catch (InterruptedException ignore) {
						}
						threadSafeCache.get("key" + keyExtension);
						done.countDown();
					}
				};
				Thread thread = new Thread(runner);
				thread.start();
			}

			start.await();
			goSignal.countDown();
			long startTime = System.nanoTime();
			latchLoader.latch.countDown();

			done.await();
			long endTime = System.nanoTime();
			long duration = endTime - startTime;
			totalTime += duration;
			maxTime = Math.max(maxTime, duration);
			minTime = Math.min(minTime, duration);
			assertEquals(NUMBER_OF_KEYS, latchLoader.invokeCount.intValue());
		}
		long averageTime = totalTime / (NUMBER_OF_RUNS * 1000 * 1000);
		System.out.println("Run 1");
		System.out.println("============================");
		System.out.println("Average time: " + averageTime + " ms");
		maxTime = maxTime / (1000 * 1000);
		minTime = minTime / (1000 * 1000);
		System.out.println("Max time: " + maxTime + " ms");
		System.out.println("Min time: " + minTime + " ms");
		System.out.println("============================");
	}


	private static class LatchCacheLoader implements CacheLoader<String, TempObject> {

		CountDownLatch latch = new CountDownLatch(1);
		AtomicInteger invokeCount = new AtomicInteger();

		@Override
		public TempObject load(String key) {
			try {
				latch.await();
			} catch (InterruptedException ignored) {
			}
			invokeCount.incrementAndGet();
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return new TempObject(key);
		}
	}


	private static class TempObject {
		String data;

		private TempObject(String data) {
			this.data = data;
		}
	}
}
