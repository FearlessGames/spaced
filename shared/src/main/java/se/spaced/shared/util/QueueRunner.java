package se.spaced.shared.util;

import se.fearless.common.lifetime.LifetimeListener;

public interface QueueRunner<Key, Data> extends LifetimeListener {

	void runWith(Key key, Callback<Key, Data> callback, ExceptionCallback<Key, Data> exceptionCallback);

	void runWith(Key key, Callback<Key, Data> callback);

	interface Runner<Key, Data> {
		Data onRunWith(Key key);
	}

	interface Callback<Key, Data> {
		void afterRunWith(Key key, Data data, int numberOfJobsRemaining);
	}

	interface ExceptionCallback<Key, Data> {
		void onException(Key key, Exception exception);
	}
}
