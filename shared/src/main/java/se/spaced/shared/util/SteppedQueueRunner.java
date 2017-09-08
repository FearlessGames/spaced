package se.spaced.shared.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayDeque;
import java.util.Queue;

public class SteppedQueueRunner<Key, Data> implements QueueRunner<Key, Data> {
	private final Logger log = LoggerFactory.getLogger(getClass());
	private final Queue<QueueData<Key, Data>> tasks = new ArrayDeque<QueueData<Key, Data>>(10);

	private final Runner<Key, Data> loader;

	public SteppedQueueRunner(Runner<Key, Data> loader) {
		this.loader = loader;
	}

	@Override
	public void runWith(Key key, Callback<Key, Data> keyDataCallback, 
							  ExceptionCallback<Key, Data> keyDataExceptionCallback) {
		tasks.add(new QueueData<Key, Data>(key, keyDataCallback, keyDataExceptionCallback));
	}

	@Override
	public void runWith(Key key, Callback<Key, Data> keyDataCallback) {
		runWith(key, keyDataCallback, null);
	}
	
	public void step() {
		if (tasks.isEmpty()) {
			return;
		}
		QueueData<Key, Data> task = tasks.poll();
		try {
			Data data = loader.onRunWith(task.key);
			int remainingJobs = tasks.size();
			if (task.callback != null) {
				task.callback.afterRunWith(task.key, data, remainingJobs);
			}
		} catch (Exception e) {
			if (task.exceptionCallback != null) {
				task.exceptionCallback.onException(task.key, e);
			} else {
				log.warn("Exception in SteppedQueueRunner with no exceptionCallback set", e);
			}
		}
	}

	@Override
	public void onStart() {
		log.info("Start");
	}

	@Override
	public void onShutdown() {
		log.info("Shutdown");
	}

	private static class QueueData<Key, Data> {
		final Key key;
		final Callback<Key, Data> callback;
		final ExceptionCallback<Key, Data> exceptionCallback;

		private QueueData(
				Key key,
				Callback<Key, Data> callback,
				ExceptionCallback<Key, Data> exceptionCallback) {
			this.key = key;
			this.callback = callback;
			this.exceptionCallback = exceptionCallback;
		}
	}
}
