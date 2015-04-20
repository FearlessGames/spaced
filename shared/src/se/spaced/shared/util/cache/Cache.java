package se.spaced.shared.util.cache;

public interface Cache<Key, Data> {
	Data get(Key key);

	void invalidate(Key key);

	void invalidateAll();
}
