package se.ardorgui.components.listeners;

import se.ardorgui.input.events.ComponentMouseEvent;

public interface ComponentDragReceiverListener {
	void dragDropped(ComponentMouseEvent e);
	void dragEnter(ComponentMouseEvent e);
	void dragLeave(ComponentMouseEvent e);
}
