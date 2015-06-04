package se.ardorgui.components.label;

import se.ardorgui.components.listeners.ListenerContainer;

public class LabelListeners extends ListenerContainer<LabelListenerInterface> implements LabelListenerInterface {
	@Override
	public void onTextChanged(final Label label) {
		for (final LabelListenerInterface listener : listeners) {
			listener.onTextChanged(label);
		}
	}
}