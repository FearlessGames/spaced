package se.spaced.server.model.spawn;

public class AttackingParameters {
	private final boolean moveToTarget;
	private final boolean lookAtTarget;

	public AttackingParameters(boolean moveToTarget, boolean lookAtTarget) {
		this.moveToTarget = moveToTarget;
		this.lookAtTarget = lookAtTarget;
	}

	public boolean isMoveToTarget() {
		return moveToTarget;
	}

	public boolean isLookAtTarget() {
		return lookAtTarget;
	}
}
