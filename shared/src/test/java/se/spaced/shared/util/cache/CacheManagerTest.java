package se.spaced.shared.util.cache;

import org.junit.Before;
import org.junit.Test;

import static se.mockachino.Mockachino.*;


public class CacheManagerTest {
	Cache<String, String> stringCache;
	Cache<String, Integer> integerCache;
	CacheManager cacheManager;

	@Before
	@SuppressWarnings("unchecked")
	public void setUp() throws Exception {
		cacheManager = new CacheManager();
		stringCache = mock(Cache.class);
		integerCache = mock(Cache.class);
	}

	@Test
	public void testAddManagedCache() throws Exception {
		cacheManager.addManagedCache(stringCache);
		cacheManager.addManagedCache(integerCache);
	}

	@Test
	public void testInvalidateAll() throws Exception {
		testAddManagedCache();
		cacheManager.invalidateAll();
		verifyOnce().on(stringCache).invalidateAll();
		verifyOnce().on(integerCache).invalidateAll();
	}
}
