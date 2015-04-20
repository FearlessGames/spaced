package se.ardorgui.components.base;

import se.ardorgui.components.listeners.ListenerContainer;

public class ComponentListeners extends ListenerContainer<ComponentListener> implements ComponentListener {
	@Override
	public void onShow(final Component component) {
		for (final ComponentListener listener : listeners) {
			listener.onShow(component);
		}
	}

	@Override
	public void onHide(final Component component) {
		for (final ComponentListener listener : listeners) {
			listener.onHide(component);
		}
	}

	@Override
	public void onDisable(final Component component) {
		for (final ComponentListener listener : listeners) {
			listener.onDisable(component);
		}
	}

	@Override
	public void onEnable(final Component component) {
		for (final ComponentListener listener : listeners) {
			listener.onEnable(component);
		}
	}

	@Override
	public void onMove(final Component component) {
		for (final ComponentListener listener : listeners) {
			listener.onMove(component);
		}
	}

	@Override
	public void onResize(final Component component) {
		for (final ComponentListener listener : listeners) {
			listener.onResize(component);
		}
	}

	@Override
	public void onChangeColor(final Component component) {
		for (final ComponentListener listener : listeners) {
			listener.onChangeColor(component);
		}
	}

	@Override
	public void onChangeFade(final Component component) {
		for (final ComponentListener listener : listeners) {
			listener.onChangeFade(component);
		}
	}

	@Override
	public void onReleaseResources(final Component component) {
		for (final ComponentListener listener : listeners) {
			listener.onReleaseResources(component);
		}
	}
}
