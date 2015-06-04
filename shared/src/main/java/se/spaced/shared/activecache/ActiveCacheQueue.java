package se.spaced.shared.activecache;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicReference;

public class ActiveCacheQueue<V> {
	private final Queue<Job<V>> queue = new ConcurrentLinkedQueue<Job<V>>();	
	private final AtomicReference<V> value = new AtomicReference<V>(null);

	public void add(Job<V> job) {
		queue.add(job);
		if (dead()) {
			processQueue();
		}
	}

	public void consumeAll(V value) {
		this.value.set(value);
		processQueue();
	}

	private void processQueue() {
		while (!queue.isEmpty()) {
			Job<V> job = queue.poll();
			if (job != null) {
				job.run(value.get());
			}
		}
	}

	public boolean dead() {
		return value.get() != null;
	}
}
