package se.spaced.server.persistence.migrator.converters;

public interface NamedObjectCache<K> {
	Object getCachedReference(K keySpace, String name);

	void addCacheReference(K keySpace, String name, Object t);
}
