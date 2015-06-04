package se.spaced.shared.util.cache.impl;

import se.spaced.shared.util.cache.Cache;
import se.spaced.shared.util.cache.CacheLoader;

import java.util.HashMap;
import java.util.Map;

public class ThreadLocalCache<Key, Data> implements Cache<Key, Data> {
	private final CacheLoader<Key, Data> loader;
	private final ThreadLocal<Map<Key, Data>> threadLocal = new ThreadLocal<Map<Key, Data>>() {
		@Override
		protected Map<Key, Data> initialValue() {
			return new HashMap<Key, Data>();
		}
	};

	public ThreadLocalCache(CacheLoader<Key, Data> loader) {
		this.loader = loader;
	}

	@Override
	public Data get(Key key) {
		final Map<Key, Data> map = threadLocal.get();

		if (!map.containsKey(key)) {
			map.put(key, loader.load(key));
		}

		return map.get(key);

	}

	@Override
	public void invalidate(Key key) {
		threadLocal.get().remove(key);
	}

	@Override
	public void invalidateAll() {
		threadLocal.get().clear();
	}
}
