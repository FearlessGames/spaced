package se.ardorgui.components.area;

public interface ComponentArea {
	int getWidth();
	int getHeight();
	void setSize(int width, int height);
	void setWidth(int width);
	void setHeight(int height);
	boolean isInside(int x, int y);
}