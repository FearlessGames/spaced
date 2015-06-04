package se.spaced.shared.util;

import se.fearlessgames.common.lifetime.LifetimeListener;

public interface QueueRunner<Key, Data> extends LifetimeListener {

	void runWith(Key key, Callback<Key, Data> callback, ExceptionCallback<Key, Data> exceptionCallback);

	void runWith(Key key, Callback<Key, Data> callback);

	public interface Runner<Key, Data> {
		Data onRunWith(Key key);
	}

	public interface Callback<Key, Data> {
		void afterRunWith(Key key, Data data, int numberOfJobsRemaining);
	}

	public interface ExceptionCallback<Key, Data> {
		void onException(Key key, Exception exception);
	}
}
