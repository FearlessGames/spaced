package se.ardorgui.components.area;

import java.awt.*;

// TODO: What should this class be used for?
public class OutsetComponentArea extends BasicComponentArea {
	private Insets outsets = null;

	public OutsetComponentArea(final int width, final int height) {
		super(width, height);
	}

	@Override
	public boolean isInside(final int x, final int y) {
		int outsetLeft = 0;
		int outsetRight = 0;
		int outsetTop = 0;
		int outsetBottom = 0;
		if (outsets != null) {
			outsetLeft = outsets.left;
			outsetRight = outsets.right;
			outsetTop = outsets.top;
			outsetBottom = outsets.bottom;
		}

		return x > -super.getWidth() / 2 + outsetLeft &&
				x < +super.getWidth() / 2 - outsetRight &&
				y > -super.getHeight() / 2 + outsetBottom &&
				y < +super.getHeight() / 2 - outsetTop;
	}

	@Override
	public int getWidth() {
		int outsetLeft = 0;
		int outsetRight = 0;
		if (outsets != null) {
			outsetLeft = outsets.left;
			outsetRight = outsets.right;
		}

		return super.getWidth() - (outsetLeft + outsetRight);
	}

	@Override
	public int getHeight() {
		int outsetTop = 0;
		int outsetBottom = 0;
		if (outsets != null) {
			outsetTop = outsets.top;
			outsetBottom = outsets.bottom;
		}

		return super.getHeight() - (outsetTop + outsetBottom);
	}


	public Insets getOutsets() {
		return outsets;
	}

	public void setOutsets(final Insets outsets) {
		this.outsets = outsets;
	}
}
