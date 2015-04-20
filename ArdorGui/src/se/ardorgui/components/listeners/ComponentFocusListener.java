package se.ardorgui.components.listeners;

import se.ardorgui.input.events.ComponentFocusEvent;

public interface ComponentFocusListener {
	public void focusGained(ComponentFocusEvent focusEvent);
	public void focusLost(ComponentFocusEvent focusEvent);
}