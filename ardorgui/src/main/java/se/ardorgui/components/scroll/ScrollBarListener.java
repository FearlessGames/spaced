package se.ardorgui.components.scroll;

public interface ScrollBarListener {
	void valueChanged(ScrollBar scrollBar);
	void maxChanged(ScrollBar scrollBar);
	void sizeChanged(ScrollBar scrollBar);
}