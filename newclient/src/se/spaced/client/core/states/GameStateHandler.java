package se.spaced.client.core.states;

import com.google.inject.Inject;
import se.spaced.shared.util.ListenerDispatcher;

import static com.google.common.base.Preconditions.checkNotNull;

public class GameStateHandler implements GameStateContext, GameStateUpdater {
	private GameState currentState = new NullState();
	private GameState pendingState;
	private InternalState internalState = InternalState.UPDATING;
	private final ListenerDispatcher<StateChangeListener> listeners;

	@Inject
	public GameStateHandler(ListenerDispatcher<StateChangeListener> listeners) {
		this.listeners = listeners;
	}


	@Override
	public void changeState(final GameState state) {
		pendingState = checkNotNull(state, "state");
		internalState = InternalState.CHANGING;
	}

	@Override
	public GameState current() {
		return currentState;
	}

	@Override
	public void update(double timePerFrame) {
		internalState.update(this, timePerFrame);
	}

	@Override
	public void updateFixed(long millisPerFrame) {
		internalState.updateFixed(this, millisPerFrame);
	}

	private enum InternalState {
		UPDATING {
			@Override
			public void updateFixed(GameStateHandler parent, long millisPerFrame) {
				parent.currentState.updateFixed(parent, millisPerFrame);
			}

			@Override
			public void update(GameStateHandler parent, double timePerFrame) {
				parent.currentState.update(parent, timePerFrame);
			}
		},
		CHANGING {
			@Override
			public void updateFixed(GameStateHandler parent, long millisPerFrame) {
				parent.currentState.updateFixed(parent, millisPerFrame);
			}

			@Override
			public void update(GameStateHandler parent, double timePerFrame) {
				parent.currentState.exit();
				parent.listeners.trigger().onStateChange(parent.currentState, parent.pendingState);
				parent.pendingState.start();
				parent.currentState = parent.pendingState;
				parent.pendingState = null;
				parent.currentState.update(parent, timePerFrame);
				parent.internalState = InternalState.UPDATING;

			}
		};

		public abstract void update(GameStateHandler parent, double timePerFrame);

		public abstract void updateFixed(GameStateHandler parent, long millisPerFrame);
	}
}
