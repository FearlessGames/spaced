package se.spaced.shared.concurrency;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class SimpleThreadFactory {
	private SimpleThreadFactory() {
	}

	public static ThreadFactory withPrefix(final String prefix) {
		return new ThreadFactory() {
			private final AtomicInteger id = new AtomicInteger(-1);

			@Override
			public Thread newThread(Runnable r) {
				return new Thread(r, prefix + id.incrementAndGet());
			}
		};
	}
}
