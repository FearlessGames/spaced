package se.ardorgui.components;

import se.ardorgui.components.area.BasicComponentArea;
import se.ardorgui.components.base.ComponentContainer;
import se.ardorgui.view.views.ComponentContainerView;

public class GuiContainer extends ComponentContainer {

	public GuiContainer(final ComponentContainerView view, int screenWidth, int screenHeight) {
		super(view, new BasicComponentArea(0, 0));
		setPosition(screenWidth / 2, screenHeight / 2);
	}
}