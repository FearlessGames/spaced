package se.ardorgui.components.progress;

import se.ardorgui.components.listeners.ListenerContainer;

public class ProgressListeners extends ListenerContainer<ProgressListenerInterface> implements ProgressListenerInterface {
	@Override
	public void onFillChanged(final Progress progress) {
		for (final ProgressListenerInterface listener : listeners) {
			listener.onFillChanged(progress);
		}
	}
}