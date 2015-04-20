package se.spaced.server.persistence.migrator.converters;

public interface NamedObjectCache<K> {
	<T> T getCachedReference(K keySpace, String name);

	<T> void addCacheReference(K keySpace, String name, T t);
}
