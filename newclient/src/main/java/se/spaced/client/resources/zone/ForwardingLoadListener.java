package se.spaced.client.resources.zone;

public class ForwardingLoadListener implements LoadListener {
	private final LoadListener delegate;

	public ForwardingLoadListener(LoadListener delegate) {
		this.delegate = delegate;
	}

	@Override
	public void loadCompleted() {
		delegate.loadCompleted();
	}

	@Override
	public void loadUpdate(int remainingTasks) {
		delegate.loadUpdate(remainingTasks);
	}
}
