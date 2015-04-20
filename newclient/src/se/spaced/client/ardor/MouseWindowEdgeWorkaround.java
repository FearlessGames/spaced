package se.spaced.client.ardor;

import com.ardor3d.framework.DisplaySettings;
import com.ardor3d.input.MouseManager;

public class MouseWindowEdgeWorkaround {
	private final MouseManager mouseManager;

	private final int width;
	private final int height;

	private int grabX;
	private int grabY;

	private boolean grabbed;

	private int lastX;
	private int lastY;
	private int lastDeltaX;
	private int lastDeltaY;

	public MouseWindowEdgeWorkaround(MouseManager mouseManager, DisplaySettings displaySettings) {
		this.mouseManager = mouseManager;
		width = displaySettings.getWidth();
		height = displaySettings.getHeight();
	}

	public void grab(int x, int y) {
		if (grabbed) {
			lastDeltaX = x - lastX;
			lastDeltaY = y - lastY;

			if (x >= width - 100 || x <= 100 || y >= height - 100 || y <= 100) {
				x = width / 2;
				y = height / 2;
				mouseManager.setPosition(x, y);

				// setPosition does not take effect immediately, so set delta = 0 until it's done
				lastDeltaX = 0;
				lastDeltaY = 0;
			}
		} else {
			grabX = x;
			grabY = y;

			lastDeltaX = 0;
			lastDeltaY = 0;
			grabbed = true;
		}
		lastX = x;
		lastY = y;
	}

	public void release() {
		if (grabbed) {
			mouseManager.setPosition(grabX, grabY);
			grabbed = false;
		}
	}

	public int getLastDeltaX() {
		return lastDeltaX;
	}

	public int getLastDeltaY() {
		return lastDeltaY;
	}
}
