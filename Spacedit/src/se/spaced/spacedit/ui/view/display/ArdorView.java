package se.spaced.spacedit.ui.view.display;

import se.fearlessgames.common.ui.Action;

import java.awt.Dimension;

public interface ArdorView {
	Dimension getSize();

	void setCanvasSize(Dimension size);

	void setResizeAction(Action action);
}
