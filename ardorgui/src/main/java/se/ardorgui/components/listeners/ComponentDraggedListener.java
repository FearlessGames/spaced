package se.ardorgui.components.listeners;

import se.ardorgui.input.events.ComponentMouseEvent;

public interface ComponentDraggedListener {
	void dragged(ComponentMouseEvent e);
	void dragStarted(ComponentMouseEvent e);
	void dragEnded(ComponentMouseEvent e);
}