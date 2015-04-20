package se.spaced.shared.activecache;

public interface KeyRequestHandler<K> {
	void requestKey(K key);
}
