package se.ardorgui.components.base;

import se.ardorgui.components.area.ComponentArea;

import java.util.ArrayList;
import java.util.List;

public class ComponentContainer extends Component {
	private final List<Component> children;
	private final ComponentContainerListeners componentContainerListeners;

	public ComponentContainer(final ComponentContainerViewInterface view, final ComponentArea componentArea) {
		super(view, componentArea);
		children = new ArrayList<Component>();
		componentContainerListeners = new ComponentContainerListeners();
		componentContainerListeners.add(view);
	}

	public final void addComponent(final Component component) {
		children.add(component);
		component.setParent(this);
		componentContainerListeners.onAdd(component);
		notifyAreaChanged();
	}

	public final boolean removeComponent(final Component component) {
		final boolean removed = children.remove(component);
		componentContainerListeners.onRemove(component);
		notifyAreaChanged();
		return removed;
	}

	@Override
	public Component getHot(final int newX, final int newY) {
		if (!isLocalVisible() && !isCanBeActive()) {
			return null;
		}
		for (int i = children.size()-1; i >= 0; i--) {
			final Component hot = children.get(i).getHot(newX, newY);
			if (hot != null) {
				return hot;
			}
		}
		return super.getHot(newX, newY);
	}

	public final List<Component> getChildren() {
		return children;
	}

	public ComponentContainerListeners getComponentContainerListeners() {
		return componentContainerListeners;
	}

	@Override
	public void notifyDisabled() {
		super.notifyDisabled();
		for (final Component child : children) {
			child.notifyDisabled();
		}
	}

	@Override
	public void notifyEnabled() {
		super.notifyEnabled();
		for (final Component child : children) {
			if (child.isLocalEnabled()) {
				child.notifyEnabled();
			}
		}
	}

	@Override
	public void releaseResources() {
		Iterable<Component> components = new ArrayList<Component>(children);
		for (Component component : components) {
			component.releaseResources();
		}
		children.clear();
		componentContainerListeners.clear();
		super.releaseResources();
	}

	public void bringToFront(Component component) {
		if (component != null) {
			if (children.remove(component)) {
				children.add(component);
				componentContainerListeners.onBringToFront(component);
			}
		}
	}
}