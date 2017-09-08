package se.ardorgui.components.listeners;

import se.ardorgui.input.events.ComponentMouseEvent;

public interface ComponentMouseListener {
	void mouseClicked(ComponentMouseEvent e);
	void mousePressed(ComponentMouseEvent e);
	void mouseReleased(ComponentMouseEvent e);
	void mouseEntered(ComponentMouseEvent e);
	void mouseExited(ComponentMouseEvent e);
}