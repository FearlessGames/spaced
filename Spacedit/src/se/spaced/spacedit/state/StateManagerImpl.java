package se.spaced.spacedit.state;

import com.google.inject.Singleton;
import se.spaced.shared.util.ListenerDispatcher;

@Singleton
public class StateManagerImpl implements StateManager {
	private ListenerDispatcher<StateChangeListener> xmoListenerDispatcher = ListenerDispatcher.create(StateChangeListener.class);
	private RunningState currentRunningState;

	public StateManagerImpl() {
		this.currentRunningState = RunningState.DEFAULT;
	}

	@Override
	public void switchState(RunningState to) {
		if (currentRunningState == RunningState.DEFAULT && to == RunningState.XMO_IN_CONTEXT) {
			xmoListenerDispatcher.trigger().fromDefaultToXMOInContext();
		} else if (currentRunningState == RunningState.XMO_IN_CONTEXT && to == RunningState.DEFAULT) {
			xmoListenerDispatcher.trigger().fromXMOInContextToDefault();
		}
		this.currentRunningState = to;
	}

	@Override
	public void registerStateChangeListener(StateChangeListener listener) {
		xmoListenerDispatcher.addListener(listener);
	}

	@Override
	public RunningState getCurrentRunningState() {
		return currentRunningState;
	}
}
