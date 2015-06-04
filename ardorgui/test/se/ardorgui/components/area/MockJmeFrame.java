package se.ardorgui.components.area;

import java.awt.Dimension;

public class MockJmeFrame {
	private Dimension dimension;

	public MockJmeFrame(int x, int y, int width, int height) {
		dimension = new Dimension(width, height);
	}

	public void setSize(int width, int height) {
		dimension.setSize(width, height);
	}

	public Dimension getDimension() {
		return dimension;
	}
}
