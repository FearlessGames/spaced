package se.spaced.client.ardor.font.ttf.effects;


import se.spaced.client.ardor.font.ttf.Glyph;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class ColorEffect implements Effect {
	private final Color color;

	public ColorEffect(Color color) {
		this.color = color;
	}

	@Override
	public void draw(BufferedImage image, Graphics2D g, Glyph glyph) {
		g.setColor(color);
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
