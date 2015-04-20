package se.ardorgui.components.button;

import se.ardorgui.components.listeners.ListenerContainer;

public class ButtonListeners extends ListenerContainer<ButtonListenerInterface> implements ButtonListenerInterface {
	@Override
	public void onChangeState(final Button button) {
		for (final ButtonListenerInterface listener : listeners) {
			listener.onChangeState(button);
		}
	}
}