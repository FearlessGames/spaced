package se.ardorgui.input.events;

import se.ardorgui.components.base.Component;

public class ComponentMouseWheelEvent {
	private final Component activeComponent;
	private final int wheelDelta;
	private final int x;
	private final int y;

	public ComponentMouseWheelEvent(final Component activeComponent, final int wheelDelta, final int x, final int y) {
		this.activeComponent = activeComponent;
		this.wheelDelta = wheelDelta;
		this.x = x;
		this.y = y;
	}

	public Component getActiveComponent() {
		return activeComponent;
	}

	public int getWheelDelta() {
		return wheelDelta;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}
}