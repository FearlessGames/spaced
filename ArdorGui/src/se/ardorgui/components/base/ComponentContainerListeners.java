package se.ardorgui.components.base;

import se.ardorgui.components.listeners.ListenerContainer;

public class ComponentContainerListeners extends ListenerContainer<ComponentContainerListenerInterface> implements ComponentContainerListenerInterface {
	@Override
	public void onRemove(final Component component) {
		for (final ComponentContainerListenerInterface listener : listeners) {
			listener.onRemove(component);
		}
	}

	@Override
	public void onAdd(final Component component) {
		for (final ComponentContainerListenerInterface listener : listeners) {
			listener.onAdd(component);
		}
	}

	@Override
	public void onBringToFront(Component component) {
		for (final ComponentContainerListenerInterface listener : listeners) {
			listener.onBringToFront(component);
		}
	}
}