package se.ardorgui.components.listeners;

import se.ardorgui.input.events.ComponentMouseEvent;

public interface ComponentDraggedListener {
	public void dragged(ComponentMouseEvent e);
	public void dragStarted(ComponentMouseEvent e);
	public void dragEnded(ComponentMouseEvent e);
}