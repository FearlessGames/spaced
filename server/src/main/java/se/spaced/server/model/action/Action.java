package se.spaced.server.model.action;

public abstract class Action implements Comparable<Action> {

	long executionTime;

	long newExecutionTime;
	boolean rescheduled;

	private boolean cancelled;

	protected Action(long executionTime) {
		this.executionTime = executionTime;
	}

	public final boolean timeToExecute(long now) {
		return executionTime <= now;
	}

	public final long getExecutionTime() {
		return executionTime;
	}

	public abstract void perform();

	@Override
	public final int compareTo(Action o) {
		return Long.signum(executionTime - o.getExecutionTime());
	}

	public final boolean isRescheduled() {
		return rescheduled;
	}

	public final void cancel() {
		cancelled = true;
		onCancel();
	}

	public final boolean isCancelled() {
		return cancelled;
	}

	/**
	 * This method will be called before an action is removed from the scheduler
	 * in the case of cancellation of an action
	 */
	public void onCancel() {
	}
}
