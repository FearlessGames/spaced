package se.spaced.shared.util;

import com.google.common.collect.Maps;

import java.util.Map;

public class Repository<K, D> {
	private final Map<K, D> storage = Maps.newHashMap();

	public D get(K key) {
		return storage.get(key);
	}

	public void put(K key, D data) {
		storage.put(key, data);
	}
}
