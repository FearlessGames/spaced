package se.spaced.shared.util.cache;

import java.util.ArrayList;
import java.util.Collection;

public class CacheManager {
	private final Collection<Cache<?, ?>> managedCaches = new ArrayList<Cache<?, ?>>();

	public void addManagedCache(Cache<?, ?> cache) {
		managedCaches.add(cache);
	}

	public void invalidateAll() {
		for (Cache<?, ?> managedCach : managedCaches) {
			managedCach.invalidateAll();
		}
	}
}
