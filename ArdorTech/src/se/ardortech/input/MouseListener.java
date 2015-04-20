package se.ardortech.input;

public interface MouseListener {
	void onMove(final int deltaX, final int deltaY, final int newX, final int newY);

	boolean onButton(ClientMouseButton button, final boolean pressed, final int x, final int y);

	void onWheel(final int wheelDelta, final int x, final int y);
}