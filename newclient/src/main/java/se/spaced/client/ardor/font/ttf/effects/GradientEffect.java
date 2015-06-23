package se.spaced.client.ardor.font.ttf.effects;

import se.spaced.client.ardor.font.ttf.Glyph;
import se.spaced.client.ardor.font.ttf.TrueTypeFont;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class GradientEffect implements Effect {
	private final Color topColor;
	private final Color bottomColor;
	private final float scale;
	private final int offset;
	private boolean cyclic;
	private final TrueTypeFont trueTypeFont;

	public GradientEffect(Color topColor, Color bottomColor, float scale, int offset, boolean cyclic, TrueTypeFont trueTypeFont) {
		this.topColor = topColor;
		this.bottomColor = bottomColor;
		this.scale = scale;
		this.offset = offset;
		this.cyclic = cyclic;
		this.trueTypeFont = trueTypeFont;
	}


	@Override
	public void draw(BufferedImage image, Graphics2D g, Glyph glyph) {
		int ascent = trueTypeFont.getAscent();
		float height = (ascent) * scale;
		float top = -glyph.getYOffset() + trueTypeFont.getDescent() + offset + ascent / 2 - height / 2;
		g.setPaint(new GradientPaint(0, top, topColor, 0, top + height, bottomColor, cyclic));
		g.fill(glyph.getShape());
	}

	@Override
	public int getPaddingTop() {
		return 0;
	}

	@Override
	public int getPaddingLeft() {
		return 0;
	}

	@Override
	public int getPaddingBottom() {
		return 0;
	}

	@Override
	public int getPaddingRight() {
		return 0;
	}
}
