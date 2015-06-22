package se.spaced.server.model.action;

import se.spaced.server.model.ServerEntity;

public abstract class TargetedAction extends OrderedAction {
	protected ServerEntity target;

	protected TargetedAction(long executionTime, ServerEntity performer, ServerEntity target) {
		super(executionTime, performer);
		this.target = target;
	}

	public ServerEntity getTarget() {
		return target;
	}
}
