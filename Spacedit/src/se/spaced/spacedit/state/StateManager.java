package se.spaced.spacedit.state;

public interface StateManager {
	public void switchState(RunningState to);

	public void registerStateChangeListener(StateChangeListener listener);

	RunningState getCurrentRunningState();
}
