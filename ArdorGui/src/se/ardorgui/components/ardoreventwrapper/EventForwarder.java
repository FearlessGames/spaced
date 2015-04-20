package se.ardorgui.components.ardoreventwrapper;

import com.ardor3d.extension.ui.UIComponent;
import com.ardor3d.input.InputState;
import com.ardor3d.input.Key;
import com.ardor3d.input.KeyEvent;
import com.ardor3d.input.MouseButton;
import se.spaced.shared.events.EventHandler;

public class EventForwarder {
	private final UIComponent component;
	private final EventHandler eventHandler;

	public EventForwarder(UIComponent component, EventHandler eventHandler) {
		this.component = component;
		this.eventHandler = eventHandler;
	}

	public void mouseEntered(int mouseX, int mouseY) {
		if (!isActive()) {
			return;
		}
		eventHandler.fireEvent(MouseEvents.OnMouseEnter, mouseX, mouseY);
	}

	public void mouseDeparted(int mouseX, int mouseY) {
		if (!isActive()) {
			return;
		}
		eventHandler.fireEvent(MouseEvents.OnMouseLeave, mouseX, mouseY);
	}

	public void mousePressed(MouseButton button, InputState state) {
		if (!isActive()) {
			return;
		}
		int x = state.getMouseState().getX();
		int y = state.getMouseState().getY();
		eventHandler.fireEvent(MouseEvents.OnMouseDown, getButton(button), x, y);
	}

	private boolean isActive() {
		return component.isVisible();
	}

	public void mouseReleased(MouseButton button, InputState state) {
		if (!isActive()) {
			return;
		}
		int x = state.getMouseState().getX();
		int y = state.getMouseState().getY();
		eventHandler.fireEvent(MouseEvents.OnMouseUp, getButton(button), x, y);
	}

	public void mouseClicked(MouseButton button, InputState state) {
		if (!isActive()) {
			return;
		}
		int x = state.getMouseState().getX();
		int y = state.getMouseState().getY();
		eventHandler.fireEvent(MouseEvents.OnClick, getButton(button), x, y);
	}

	private String getButton(final MouseButton e) {
		switch (e) {
			case LEFT:
				return "LeftButton";
			case MIDDLE:
				return "MiddleButton";
			case RIGHT:
				return "RightButton";
		}
		return "Unknown";
	}

	public void keyPressed(Key key, InputState state) {
		if (!isActive()) {
			return;
		}
		KeyEvent ardorEvent = state.getKeyboardState().getKeyEvent();
		eventHandler.fireEvent(KeyEvents.KeyDown, key);
	}

	public void keyReleased(Key key, InputState state) {
		if (!isActive()) {
			return;
		}
		KeyEvent ardorEvent = state.getKeyboardState().getKeyEvent();
		eventHandler.fireEvent(KeyEvents.KeyUp, key);
	}
}
