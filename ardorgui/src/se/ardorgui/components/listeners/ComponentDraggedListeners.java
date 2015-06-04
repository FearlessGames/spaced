package se.ardorgui.components.listeners;

import se.ardorgui.input.events.ComponentMouseEvent;

public class ComponentDraggedListeners extends ListenerContainer<ComponentDraggedListener> {
	public void sendDraggedEvent(ComponentMouseEvent e) {
		for (ComponentDraggedListener listener : listeners) {
			listener.dragged(e);
		}
	}

	public void sendDragStartedEvent(ComponentMouseEvent e) {
		for (ComponentDraggedListener listener : listeners) {
			listener.dragStarted(e);
		}
	}

	public void sendDragEndedEvent(ComponentMouseEvent e) {
		for (ComponentDraggedListener listener : listeners) {
			listener.dragEnded(e);
		}
	}
}