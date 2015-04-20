package se.ardorgui.components.listeners;

import se.ardorgui.input.events.ComponentMouseEvent;

public interface ComponentMouseListener {
	public void mouseClicked(ComponentMouseEvent e);
	public void mousePressed(ComponentMouseEvent e);
	public void mouseReleased(ComponentMouseEvent e);
	public void mouseEntered(ComponentMouseEvent e);
	public void mouseExited(ComponentMouseEvent e);
}