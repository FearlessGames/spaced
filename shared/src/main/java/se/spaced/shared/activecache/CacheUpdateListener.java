package se.spaced.shared.activecache;

public interface CacheUpdateListener<K, V> {
	void updatedValue(K key, V oldValue, V value);

	void deletedValue(K key, V oldValue);

	void addedValue(K key, V value);
}
