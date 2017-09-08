package se.ardorgui.components.listeners;

import se.ardorgui.input.events.ComponentFocusEvent;

public interface ComponentFocusListener {
	void focusGained(ComponentFocusEvent focusEvent);
	void focusLost(ComponentFocusEvent focusEvent);
}