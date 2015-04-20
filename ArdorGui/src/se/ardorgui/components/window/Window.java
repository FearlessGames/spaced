package se.ardorgui.components.window;

import se.ardorgui.components.area.ComponentArea;
import se.ardorgui.components.base.Component;
import se.ardorgui.components.base.ComponentContainer;
import se.ardorgui.components.base.ComponentContainerViewInterface;
import se.ardorgui.components.base.ComponentListenerAdapter;
import se.ardorgui.components.button.Button;
import se.ardorgui.components.label.Label;
import se.ardorgui.components.listeners.ComponentClickMouseListener;
import se.ardorgui.input.events.ComponentMouseEvent;

public class Window extends ComponentContainer {
	private final WindowListeners windowListeners;

	// TODO: This should be created from a factory, not inside the class!!
	public Window(final ComponentContainerViewInterface view, final ComponentArea area, final ComponentContainer headerPanel, final Label header, final ComponentContainer closeButtonContainer, final Button closeButton) {
		super(view, area);
		windowListeners = new WindowListeners();

		headerPanel.setPosition(0, getArea().getHeight() / 2 - headerPanel.getArea().getHeight() / 2);
		addComponent(headerPanel);

		header.setPosition(-getArea().getWidth() / 2 + 10, 3);
		headerPanel.addComponent(header);

		closeButtonContainer.setPosition(getArea().getWidth() / 2 - 16, getArea().getHeight() / 2 - 16);
		addComponent(closeButtonContainer);
		closeButton.getInputListeners().getMouseListeners().add(new ComponentClickMouseListener() {
			@Override
			public void doClick(final ComponentMouseEvent e) {
				close();
			}
		});

		getListeners().add(new ComponentListenerAdapter() {
			@Override
			public void onResize(final Component component) {
				headerPanel.setSize(getArea().getWidth(), 32);
				headerPanel.setPosition(0, getArea().getHeight() / 2 - headerPanel.getArea().getHeight() / 2);
				closeButtonContainer.setPosition(getArea().getWidth() / 2 - 16, getArea().getHeight() / 2 - 16);
				header.setPosition(-getArea().getWidth() / 2 + 10, 3);
			}
		});
	}

	public final WindowListeners getWindowListeners() {
		return windowListeners;
	}

	public void close() {
		removeFromParent();
		windowListeners.onClose();
	}
}