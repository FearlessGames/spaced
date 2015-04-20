package se.spaced.client.ardor;

import com.ardor3d.input.KeyEvent;
import com.ardor3d.input.KeyState;
import com.ardor3d.input.MouseButton;
import com.ardor3d.input.MouseState;
import com.google.common.collect.Sets;
import se.ardortech.input.ClientMouseButton;
import se.ardortech.input.KeyListener;
import se.ardortech.input.MouseListener;

import java.util.EnumSet;
import java.util.Set;

public class InputManager {

	private final Set<KeyListener> keyListeners = Sets.newLinkedHashSet();
	private final Set<MouseListener> mouseListeners = Sets.newLinkedHashSet();

	public void addKeyListener(KeyListener listener) {
		keyListeners.add(listener);
	}

	public void removeKeyListener(KeyListener listener) {
		keyListeners.remove(listener);
	}

	public void addMouseListener(MouseListener listener) {
		mouseListeners.add(listener);
	}

	public void removeMouseListener(MouseListener listener) {
		mouseListeners.remove(listener);
	}

	public void onKeyEvent(KeyEvent keyEvent) {
		for (KeyListener keyListener : keyListeners) {
			keyListener.onKey(keyEvent.getKeyChar(), keyEvent.getKey(), keyEvent.getState() == KeyState.DOWN);
		}
	}

	public void onMouseEvent(MouseState previousMouseState, MouseState nextMouseState) {
		int dx = nextMouseState.getDx();
		int dy = nextMouseState.getDy();
		if (dx != 0 || dy != 0) {
			triggerMouseMoved(dx, dy, nextMouseState.getX(), nextMouseState.getY());
		}
		if (nextMouseState.getDwheel() != 0) {
			for (MouseListener mouseInputListener : mouseListeners) {
				mouseInputListener.onWheel(nextMouseState.getDwheel(), nextMouseState.getX(), nextMouseState.getY());
			}
		}

		EnumSet<MouseButton> buttonsPressedSince = nextMouseState.getButtonsPressedSince(previousMouseState);
		for (MouseButton button : buttonsPressedSince) {
			ClientMouseButton clientMouseButton = ClientMouseButton.fromId(button.ordinal());
			if (clientMouseButton != ClientMouseButton.UNUSED) {
				triggerMousePressed(clientMouseButton, nextMouseState.getX(), nextMouseState.getY());
			}
		}
		EnumSet<MouseButton> buttonsReleasedSince = nextMouseState.getButtonsReleasedSince(previousMouseState);

		for (MouseButton button : buttonsReleasedSince) {
			ClientMouseButton clientMouseButton = ClientMouseButton.fromId(button.ordinal());
			if (clientMouseButton != ClientMouseButton.UNUSED) {
				triggerMouseRelease(ClientMouseButton.fromId(button.ordinal()), nextMouseState.getX(), nextMouseState.getY());
			}
		}
	}


	private void triggerMouseRelease(ClientMouseButton button, int x, int y) {
		for (MouseListener mouseInputListener : mouseListeners) {
			if (mouseInputListener.onButton(button, false, x, y)) {
				break;
			}
		}
	}

	private void triggerMousePressed(ClientMouseButton button, int x, int y) {
		for (MouseListener mouseInputListener : mouseListeners) {
			if (mouseInputListener.onButton(button, true, x, y)) {
				break;
			}
		}
	}

	private void triggerMouseMoved(int dx, int dy, int newX, int newY) {
		for (MouseListener mouseInputListener : mouseListeners) {
			mouseInputListener.onMove(dx, dy, newX, newY);
		}
	}


}
