package se.ardorgui.components.listeners;

import se.ardorgui.input.events.ComponentMouseEvent;

public interface ComponentDragReceiverListener {
	public void dragDropped(ComponentMouseEvent e);
	public void dragEnter(ComponentMouseEvent e);
	public void dragLeave(ComponentMouseEvent e);
}
