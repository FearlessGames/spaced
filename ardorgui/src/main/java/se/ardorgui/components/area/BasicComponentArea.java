package se.ardorgui.components.area;

public class BasicComponentArea implements ComponentArea {
	private int width = 0;
	private int height = 0;
	private int halfWidth = 0;
	private int halfHeight = 0;

	public BasicComponentArea(final int width, final int height) {
		setWidth(width);
		setHeight(height);
	}

	@Override
	public boolean isInside(final int x, final int y) {
		return x > -halfWidth &&
				x < +halfWidth &&
				y > -halfHeight &&
				y < +halfHeight;
	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public void setSize(final int width, final int height) {
		setWidth(width);
		setHeight(height);
	}

	@Override
	public void setWidth(final int width) {
		this.width = width;
		halfWidth = width / 2;
	}

	@Override
	public int getHeight() {
		return height;
	}

	@Override
	public void setHeight(final int height) {
		this.height = height;
		halfHeight = height / 2;
	}

	@Override
	public String toString() {
		return BasicComponentArea.class.getSimpleName() + "(width:" + width + " height:" + height + ")";
	}
}