package se.spaced.server.persistence.migrator.converters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class NamedObjectCacheImpl<K> implements NamedObjectCache<K> {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private final Map<K, Map<String, Object>> cacheMap = new HashMap<K, Map<String, Object>>();

	@Override
	public Object getCachedReference(K keySpace, String name) {
		Map<String, Object> stringObjectMap = cacheMap.get(keySpace);

		if (stringObjectMap != null) {
			Object o = stringObjectMap.get(name);
			if (o != null) {
				return o;
			}
		}
		throw new RuntimeException("Failed to find " + keySpace + ":" + name + " in cache");
	}

	@Override
	public void addCacheReference(K keySpace, String name, Object t) {
		if (!cacheMap.containsKey(keySpace)) {
			cacheMap.put(keySpace, new HashMap<String, Object>());
		}

		logger.debug("Added to cache: " + keySpace + "/" + name);
		cacheMap.get(keySpace).put(name, t);
	}
}
