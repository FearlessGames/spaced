package se.ardorgui.components.listeners;

import se.ardorgui.input.events.ComponentMouseEvent;

public class ComponentMouseListeners extends ListenerContainer<ComponentMouseListener> {

	public void sendPressedEvent(final ComponentMouseEvent e) {
		for (final ComponentMouseListener listener : listeners) {
			listener.mousePressed(e);
		}
	}

	public void sendClickedEvent(final ComponentMouseEvent e) {
		for (final ComponentMouseListener listener : listeners) {
			listener.mouseClicked(e);
		}
	}

	public void sendReleasedEvent(final ComponentMouseEvent e) {
		for (final ComponentMouseListener listener : listeners) {
			listener.mouseReleased(e);
		}
	}

	public void sendEnteredEvent(final ComponentMouseEvent e) {
		for (final ComponentMouseListener listener : listeners) {
			listener.mouseEntered(e);
		}
	}

	public void sendExitedEvent(final ComponentMouseEvent e) {
		for (final ComponentMouseListener listener : listeners) {
			listener.mouseExited(e);
		}
	}
}