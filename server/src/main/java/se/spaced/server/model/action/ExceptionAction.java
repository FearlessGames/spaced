package se.spaced.server.model.action;

public class ExceptionAction extends Action {
	public ExceptionAction(long executionTime) {
		super(executionTime);
	}

	@Override
	public void perform() {
		throw new RuntimeException("The Actionloop goes boom!");
	}
}
