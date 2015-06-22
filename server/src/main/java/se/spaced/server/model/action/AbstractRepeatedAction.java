package se.spaced.server.model.action;

public abstract class AbstractRepeatedAction extends Action {
	private final ActionScheduler actionScheduler;

	protected AbstractRepeatedAction(ActionScheduler actionScheduler, long executionTime) {
		super(executionTime);
		this.actionScheduler = actionScheduler;
	}

	@Override
	public final void perform() {
		performRepeat();
		if (!isCancelled()) {
			actionScheduler.reschedule(this, getExecutionTime() + getTimeToNextUpdate());
		} else {
			actionScheduler.reschedule(this, getExecutionTime());
		}
	}

	protected abstract long getTimeToNextUpdate();

	protected abstract void performRepeat();
}
