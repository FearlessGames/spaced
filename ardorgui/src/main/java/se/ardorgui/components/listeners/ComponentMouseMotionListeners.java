package se.ardorgui.components.listeners;

import se.ardorgui.input.events.ComponentMouseEvent;

public class ComponentMouseMotionListeners extends ListenerContainer<ComponentMouseMotionListener> {
	public void sendMovedEvent(ComponentMouseEvent e) {
		for (ComponentMouseMotionListener listener : listeners) {
			listener.mouseMoved(e);
		}
	}
}