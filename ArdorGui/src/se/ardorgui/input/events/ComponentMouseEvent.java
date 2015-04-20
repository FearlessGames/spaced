package se.ardorgui.input.events;

import com.ardor3d.extension.ui.UIComponent;

public class ComponentMouseEvent {
	private final UIComponent sourceComponent;
	private final int x;
	private final int y;
	private final int xDelta;
	private final int yDelta;
	private final int button;
	private final boolean pressed;

	public ComponentMouseEvent(final int button, final boolean pressed, final UIComponent sourceComponent, final int x, final int xDelta, final int y, final int yDelta) {
		this.button = button;
		this.pressed = pressed;
		this.sourceComponent = sourceComponent;
		this.x = x;
		this.xDelta = xDelta;
		this.y = y;
		this.yDelta = yDelta;
	}

	public UIComponent getSourceComponent() {
		return sourceComponent;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getButton() {
		return button;
	}

	public boolean isPressed() {
		return pressed;
	}

	public int getxDelta() {
		return xDelta;
	}

	public int getyDelta() {
		return yDelta;
	}
}
