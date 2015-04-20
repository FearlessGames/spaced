package se.spaced.shared.util.cache.impl;

import se.spaced.shared.util.cache.Cache;
import se.spaced.shared.util.cache.CacheLoader;

import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

public class ThreadSafeCache<Key, Data> implements Cache<Key, Data> {
	private final CacheLoader<Key, Data> loader;
	protected final ConcurrentHashMap<Key, Future<Data>> map;

	public ThreadSafeCache(CacheLoader<Key, Data> loader) {
		this.loader = loader;
		map = new ConcurrentHashMap<Key, Future<Data>>();
	}

	@Override
	public Data get(final Key key) {
		Callable<Data> load = new Callable<Data>() {
			@Override
			public Data call() throws Exception {
				return loader.load(key);
			}
		};
		FutureTask<Data> futureTask = new FutureTask<Data>(load);
		Future<Data> data = map.putIfAbsent(key, futureTask);
		if (data == null) {
			data = futureTask;
			futureTask.run();
		}
		try {
			return data.get();
		} catch (InterruptedException e) {
			map.remove(key);
			return null;
		} catch (ExecutionException e) {
			map.remove(key);
			throw new RuntimeException(e.getCause());
		}
	}

	@Override
	public void invalidate(Key key) {
		map.remove(key);
	}

	@Override
	public void invalidateAll() {
		map.clear();
	}
}
