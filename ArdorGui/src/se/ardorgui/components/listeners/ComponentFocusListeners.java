package se.ardorgui.components.listeners;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.ardorgui.input.events.ComponentFocusEvent;

public class ComponentFocusListeners extends ListenerContainer<ComponentFocusListener>  {
	private static Logger logger = LoggerFactory.getLogger(ComponentFocusListeners.class);

	public void sendFocusLostEvent(final ComponentFocusEvent event) {
		for (final ComponentFocusListener listener : listeners) {
			logger.debug("sending clicked event to listener: " + listener);
			listener.focusLost(event);
		}
	}

	public void sendFocusGainedEvent(final ComponentFocusEvent event) {
		for (final ComponentFocusListener listener : listeners) {
			logger.debug("sending clicked event to listener: " + listener);
			listener.focusGained(event);
		}
	}
}