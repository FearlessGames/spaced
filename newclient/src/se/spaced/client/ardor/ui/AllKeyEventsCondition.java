package se.spaced.client.ardor.ui;

import com.ardor3d.input.InputState;
import com.ardor3d.input.logical.TwoInputStates;
import com.google.common.base.Predicate;

public class AllKeyEventsCondition implements Predicate<TwoInputStates> {
	@Override
	public boolean apply(TwoInputStates twoInputStates) {
		InputState currentState = twoInputStates.getCurrent();
		InputState previousState = twoInputStates.getPrevious();
		return !currentState.getKeyboardState().getKeysPressedSince(previousState.getKeyboardState()).isEmpty() ||
				!currentState.getKeyboardState().getKeysReleasedSince(previousState.getKeyboardState()).isEmpty();
	}
}
