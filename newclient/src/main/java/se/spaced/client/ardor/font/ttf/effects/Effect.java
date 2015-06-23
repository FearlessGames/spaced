package se.spaced.client.ardor.font.ttf.effects;


import se.spaced.client.ardor.font.ttf.Glyph;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public interface Effect {
	void draw(BufferedImage image, Graphics2D g, Glyph glyph);

	int getPaddingTop();

	int getPaddingLeft();

	int getPaddingBottom();

	int getPaddingRight();
}
