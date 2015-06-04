package se.spaced.shared.activecache;

public interface Job<V> {
	void run(V value);
}
