package se.ardorgui.components.button;

import se.ardorgui.components.listeners.ComponentMouseListener;
import se.ardorgui.input.events.ComponentMouseEvent;

public class ButtonMouseListener implements ComponentMouseListener {
	private final Button button;

	public ButtonMouseListener(final Button button) {
		this.button = button;
	}

	@Override
	public void mouseClicked(final ComponentMouseEvent e) {
	}

	@Override
	public void mousePressed(final ComponentMouseEvent e) {
		button.setState(ButtonState.DOWN);
	}

	@Override
	public void mouseReleased(final ComponentMouseEvent e) {
		if (button.getHot(e.getX(), e.getY()) == null || !button.isEnabled()) {
			button.setState(ButtonState.UP);
		} else {
			button.setState(ButtonState.OVER);
		}
	}

	@Override
	public void mouseEntered(final ComponentMouseEvent e) {
		button.setState(ButtonState.OVER);
	}

	@Override
	public void mouseExited(final ComponentMouseEvent e) {
		button.setState(ButtonState.UP);
	}
}
