package se.ardorgui.components.button;

import se.ardorgui.components.listeners.ComponentMouseListener;
import se.ardorgui.input.events.ComponentMouseEvent;

public class SelectButtonMouseListener implements ComponentMouseListener {
	private final Button button;

	public SelectButtonMouseListener(final Button button) {
		this.button = button;
	}

	@Override
	public void mousePressed(final ComponentMouseEvent e) {
		button.setState(ButtonState.DOWN);
	}

	@Override
	public void mouseEntered(final ComponentMouseEvent e) {
		button.setState(ButtonState.OVER);
	}

	@Override
	public void mouseExited(final ComponentMouseEvent e) {
		button.setState(ButtonState.UP);
	}

	@Override
	public void mouseReleased(final ComponentMouseEvent e) {
		// TODO: implement!
//		if (button.isInteractive() && button.isEnabled()) {
//			button.toggleSelected();
//		}
		button.setState(ButtonState.OVER);
		if (!button.isEnabled()) {
			button.setState(ButtonState.UP);
		}
	}

	@Override
	public void mouseClicked(final ComponentMouseEvent e) { }
}
