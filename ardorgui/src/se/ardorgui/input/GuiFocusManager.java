package se.ardorgui.input;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.ardorgui.components.base.Component;
import se.ardorgui.input.events.ComponentFocusEvent;

public class GuiFocusManager {
	private static final Logger logger = LoggerFactory.getLogger(GuiFocusManager.class);

	private Component focusComponent;

	public boolean hasFocus() {
		return focusComponent != null && focusComponent.isVisible() && focusComponent.isEnabled();
	}

	public Component getFocus() {
		if (hasFocus()) {
			return focusComponent;
		}
		return null;
	}

	public void setFocus(Component component) {
		Component newFocusComponent = null;
		if (component != null) {
			if (!component.isCanHaveFocus()) {
				return;
			}
			newFocusComponent = component;
		}
		if (focusComponent != null) {
			logger.debug("Focus lost " + focusComponent);
			focusComponent.getInputListeners().getFocusListeners().sendFocusLostEvent(new ComponentFocusEvent(focusComponent, newFocusComponent));
		}
		if (newFocusComponent != null) {
			logger.debug("Focus gained " + newFocusComponent);
			newFocusComponent.getInputListeners().getFocusListeners().sendFocusGainedEvent(new ComponentFocusEvent(focusComponent, newFocusComponent));
		}
		focusComponent = newFocusComponent;
	}
}
