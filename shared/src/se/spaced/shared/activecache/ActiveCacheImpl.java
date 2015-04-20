package se.spaced.shared.activecache;

import se.spaced.shared.util.ListenerDispatcher;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ActiveCacheImpl<K, V> implements ActiveCache<K, V> {
	private final Map<K, V> data = new ConcurrentHashMap<K, V>();
	protected final ConcurrentHashMap<K, ActiveCacheQueue<V>> pendingJobs = new ConcurrentHashMap<K, ActiveCacheQueue<V>>();
	protected final KeyRequestHandler<K> keyRequestHandler;

	private final ListenerDispatcher<CacheUpdateListener> dispatcher = ListenerDispatcher.create(CacheUpdateListener.class);

	public ActiveCacheImpl(KeyRequestHandler<K> keyRequestHandler) {
		this.keyRequestHandler = keyRequestHandler;
	}

	@Override
	public void runWhenReady(final K key, Job<V> job) {
		final V value = getValue(key);
		if (value != null) {
			job.run(value);
		} else {
			ActiveCacheQueue<V> queue = getOrCreateQueue(key);
			queue.add(job);
		}
	}

	private ActiveCacheQueue<V> getOrCreateQueue(K key) {
		final ActiveCacheQueue<V> queue = pendingJobs.get(key);
		if (queue != null) {
			return queue;
		}

		final ActiveCacheQueue<V> newQueue = new ActiveCacheQueue<V>();
		final ActiveCacheQueue<V> oldQueue = pendingJobs.putIfAbsent(key, newQueue);

		if (oldQueue != null) {
			return oldQueue;
		}

		keyRequestHandler.requestKey(key);
		return newQueue;
	}

	@Override
	public void setValue(final K key, final V value) {
		final V oldValue = data.put(key, value);

		while (true) {
			ActiveCacheQueue<V> queue = pendingJobs.remove(key);
			if (queue == null) {
				break;
			}
			queue.consumeAll(value);
		}
		if (oldValue == null) {
			dispatcher.trigger().addedValue(key, value);
		} else {
			dispatcher.trigger().updatedValue(key, oldValue, value);
		}
	}

	@Override
	public void delete(K key) {
		Object oldValue = data.remove(key);
		if (oldValue != null) {
			dispatcher.trigger().deletedValue(key, oldValue);
		}
	}

	@Override
	public boolean isKnown(K key) {
		return data.containsKey(key);
	}

	@Override
	public V getValue(K key) {
		return data.get(key);
	}

	@Override
	public Collection<V> getValues() {
		return data.values();
	}

	@Override
	public void addListener(CacheUpdateListener<K, V> cacheUpdateListener) {
		dispatcher.addListener(cacheUpdateListener);
	}

	@Override
	public void clear() {
		for (K k : data.keySet()) {
			delete(k);
		}
	}
}
