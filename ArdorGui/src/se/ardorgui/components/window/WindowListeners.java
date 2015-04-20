package se.ardorgui.components.window;

import se.ardorgui.components.listeners.ListenerContainer;

public class WindowListeners extends ListenerContainer<WindowListener> {
	public void onClose() {
		for (WindowListener listener : listeners) {
			listener.onClose();
		}
	}
}