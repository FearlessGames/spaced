package se.ardorgui.components.base;

public interface ComponentContainerListenerInterface {
	void onRemove(Component component);
	void onAdd(Component component);
	void onBringToFront(Component component);
}