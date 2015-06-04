package se.spaced.shared.util.cache;

public interface CacheLoader<Key, Data> {
	Data load(Key key);
}
