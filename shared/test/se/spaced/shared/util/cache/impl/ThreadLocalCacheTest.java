package se.spaced.shared.util.cache.impl;

import org.junit.Before;
import org.junit.Test;
import se.spaced.shared.util.cache.Cache;
import se.spaced.shared.util.cache.CacheLoader;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static se.mockachino.Mockachino.*;


public class ThreadLocalCacheTest {
	CacheLoader<String, TempObject> loader;
	Cache<String, TempObject> threadLocalCache;

	@Before
	@SuppressWarnings("unchecked")
	public void setUp() {
		loader = mock(CacheLoader.class);
	}


	@Test
	public void testGet() {
		String key = "key";
		stubReturn(new TempObject(key)).on(loader).load(key);

		threadLocalCache = new ThreadLocalCache<String, TempObject>(loader);
		TempObject firstObject = threadLocalCache.get(key);
		assertNotNull(firstObject);
		TempObject secondObject = threadLocalCache.get(key);
		assertNotNull(secondObject);
		assertSame(firstObject, secondObject);
		verifyOnce().on(loader).load(key);

	}

	@Test
	public void testInvalidate() {
		String key = "key";
		stubReturn(new TempObject(key)).on(loader).load(key);

		threadLocalCache = new ThreadLocalCache<String, TempObject>(loader);
		TempObject firstObject = threadLocalCache.get(key);
		assertNotNull(firstObject);
		verifyExactly(1).on(loader).load(key);

		threadLocalCache.invalidate(key);
		TempObject thirdObject = threadLocalCache.get(key);
		assertNotNull(thirdObject);
		verifyExactly(2).on(loader).load(key);

	}

	@Test
	public void testInvalidateAll() {
		String key = "key";
		stubReturn(new TempObject(key)).on(loader).load(key);

		threadLocalCache = new ThreadLocalCache<String, TempObject>(loader);
		TempObject firstObject = threadLocalCache.get(key);
		assertNotNull(firstObject);
		verifyExactly(1).on(loader).load(key);

		threadLocalCache.invalidateAll();
		TempObject thirdObject = threadLocalCache.get(key);
		assertNotNull(thirdObject);
		verifyExactly(2).on(loader).load(key);
	}

	private static class TempObject {
		String data;

		private TempObject(String data) {
			this.data = data;
		}
	}
}
