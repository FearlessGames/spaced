package se.spaced.client.ardor;

import com.ardor3d.input.Key;
import com.google.inject.Inject;
import se.ardortech.Main;
import se.ardortech.input.ClientMouseButton;
import se.ardortech.input.KeyListener;
import se.ardortech.input.MouseListener;

public class LoginInputListener implements MouseListener, KeyListener {
	private final Main main;

	@Inject
	public LoginInputListener(Main main) {
		this.main = main;
	}

	@Override
	public boolean onButton(ClientMouseButton mouseButton, final boolean pressed, final int x, final int y) {
		return false;
	}

	@Override
	public void onMove(final int deltaX, final int deltaY, final int newX, final int newY) {
	}

	@Override
	public void onWheel(final int wheelDelta, final int x, final int y) {
	}

	@Override
	public boolean onKey(char character, Key keyCode, boolean pressed) {
		if (pressed) {
			// TODO: This should be moved to a lua bind
			if (keyCode == Key.ESCAPE) {
				main.exit();
			}
		}
		return false;
	}
}