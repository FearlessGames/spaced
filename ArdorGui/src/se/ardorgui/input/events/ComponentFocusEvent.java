package se.ardorgui.input.events;

import se.ardorgui.components.base.Component;

public class ComponentFocusEvent {
	private final Component oldFocus;
	private final Component newFocus;

	public ComponentFocusEvent(final Component oldFocus, final Component newFocus) {
		this.oldFocus = oldFocus;
		this.newFocus = newFocus;
	}

	public Component getOldFocus() {
		return oldFocus;
	}

	public Component getNewFocus() {
		return newFocus;
	}
}