package se.spaced.shared.activecache;

import java.util.Collection;

public interface ActiveCache<K, V> {
	boolean isKnown(K key);
	V getValue(K key);
	void setValue(K key, V value);
	void delete(K key);

	void runWhenReady(K key, Job<V> job);

	Collection<V> getValues();

	void addListener(CacheUpdateListener<K, V> listener);

	void clear();
}

