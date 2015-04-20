package se.spaced.client.ardor.font.ttf;

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.font.GlyphMetrics;
import java.awt.font.GlyphVector;

public class Glyph {
	private final int codePoint;
	private final boolean missing;
	private short width;
	private short height;
	private short yOffset;
	private short xOffset;
	private final Shape shape;
	private final float advanceX;
	private final float advanceY;

	public Glyph(
			int codePoint,
			Rectangle bounds,
			GlyphVector vector,
			int index,
			int paddingTop,
			int paddingRight,
			int paddingBottom,
			int paddingLeft,
			int ascent,
			boolean missing) {
		this.codePoint = codePoint;
		this.missing = missing;

		GlyphMetrics metrics = vector.getGlyphMetrics(index);

		int lsb = (int) metrics.getLSB();
		if (lsb > 0) {
			lsb = 0;
		}
		int rsb = (int) metrics.getRSB();
		if (rsb > 0) {
			rsb = 0;
		}

		advanceX = metrics.getAdvanceX();
		advanceY = metrics.getAdvanceY();

		int glyphWidth = bounds.width - lsb - rsb;
		int glyphHeight = bounds.height;
		if (glyphWidth > 0 && glyphHeight > 0) {

			int glyphSpacing = 1; // Needed to prevent filtering problems.
			width = (short) (glyphWidth + paddingLeft + paddingRight + glyphSpacing);
			height = (short) (glyphHeight + paddingTop + paddingBottom + glyphSpacing);
			yOffset = (short) (ascent + bounds.y - paddingTop);

			//todo: add xOffset values
		}

		shape = vector.getGlyphOutline(index, -bounds.x + paddingLeft, -bounds.y + paddingTop);

	}

	public boolean isMissing() {
		return missing;
	}

	public int getCodePoint() {
		return codePoint;
	}

	public short getWidth() {
		return width;
	}

	public short getHeight() {
		return height;
	}

	public short getYOffset() {
		return yOffset;
	}

	public short getXOffset() {
		return xOffset;
	}

	public float getAdvanceY() {
		return advanceY;
	}

	public float getAdvanceX() {
		return advanceX;
	}

	public Shape getShape() {
		return shape;
	}

}
