package se.spaced.shared.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.fearless.common.lifetime.LifetimeListener;
import se.spaced.shared.concurrency.SimpleThreadFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class MultiThreadedQueueRunner<Key, Data> implements LifetimeListener, QueueRunner<Key,Data> {
	private final Logger log = LoggerFactory.getLogger(getClass());
	private final Runner<Key, Data> loader;
	private final ExecutorService executorService;

	private final AtomicInteger pendingJobs = new AtomicInteger();

	public MultiThreadedQueueRunner(int nrOfThreads, Runner<Key, Data> loader) {
		this.loader = loader;
		executorService = Executors.newFixedThreadPool(nrOfThreads,
				SimpleThreadFactory.withPrefix("MultiThreadedQueueRunner-"));
	}

	@Override
	public void runWith(
			final Key key,
			final Callback<Key, Data> callback,
			final ExceptionCallback<Key, Data> exceptionCallback) {

		pendingJobs.incrementAndGet();
		executorService.submit(new Runnable() {
			@Override
			public void run() {
				try {
					Data data = loader.onRunWith(key);
					int remainingJobs = pendingJobs.decrementAndGet();
					if (callback != null) {
						callback.afterRunWith(key, data, remainingJobs);
					}
				} catch (Exception e) {
					if (exceptionCallback != null) {
						exceptionCallback.onException(key, e);
					} else {
						log.warn("Exception in MultiThreadedQueueRunner with no exceptionCallback set", e);
					}
				}
			}
		});
	}

	@Override
	public void runWith(final Key key, final Callback<Key, Data> callback) {
		runWith(key, callback, null);
	}

	@Override
	public void onStart() {
	}

	@Override
	public void onShutdown() {
		executorService.shutdownNow();
	}

}