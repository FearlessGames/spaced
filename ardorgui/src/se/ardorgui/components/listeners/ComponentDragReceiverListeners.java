package se.ardorgui.components.listeners;

import se.ardorgui.input.events.ComponentMouseEvent;

public class ComponentDragReceiverListeners extends ListenerContainer<ComponentDragReceiverListener> {

	public void sendDragEnterEvent(ComponentMouseEvent e) {
		for (ComponentDragReceiverListener listener : listeners) {
			listener.dragEnter(e);
		}
	}

	public void sendDragLeaveEvent(ComponentMouseEvent e) {
		for (ComponentDragReceiverListener listener : listeners) {
			listener.dragLeave(e);
		}
	}

	public void sendDragDroppedEvent(ComponentMouseEvent e) {
		for (ComponentDragReceiverListener listener : listeners) {
			listener.dragDropped(e);
		}
	}
}