package se.spaced.server.model.action;

import se.spaced.server.model.ServerEntity;


public abstract class OrderedAction extends Action {
	protected ServerEntity performer;

	protected OrderedAction(long executionTime, ServerEntity performer) {
		super(executionTime);
		this.performer = performer;
	}

	public ServerEntity getPerformer() {
		return performer;
	}

	public void performerMoved() {
	}
}
