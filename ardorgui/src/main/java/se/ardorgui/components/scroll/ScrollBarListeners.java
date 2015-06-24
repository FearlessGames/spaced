package se.ardorgui.components.scroll;

import se.ardorgui.components.listeners.ListenerContainer;

public class ScrollBarListeners extends ListenerContainer<ScrollBarListener> {
	public void valueChanged(ScrollBar scrollBar) {
		for (ScrollBarListener listener : listeners) {
			listener.valueChanged(scrollBar);
		}
	}

	public void maxChanged(ScrollBar scrollBar) {
		for (ScrollBarListener listener : listeners) {
			listener.maxChanged(scrollBar);
		}
	}

	public void sizeChanged(ScrollBar scrollBar) {
		for (ScrollBarListener listener : listeners) {
			listener.sizeChanged(scrollBar);
		}
	}
}