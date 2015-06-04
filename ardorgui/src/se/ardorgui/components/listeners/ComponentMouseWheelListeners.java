package se.ardorgui.components.listeners;

import se.ardorgui.input.events.ComponentMouseWheelEvent;


public class ComponentMouseWheelListeners extends ListenerContainer<ComponentMouseWheelListener> {
	public void sendWheelMovedEvent(ComponentMouseWheelEvent e) {
		for (ComponentMouseWheelListener listener : listeners) {
			listener.wheelMoved(e);
		}
	}
}