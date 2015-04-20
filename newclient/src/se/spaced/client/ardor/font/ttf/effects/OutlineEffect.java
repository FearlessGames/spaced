package se.spaced.client.ardor.font.ttf.effects;

import se.spaced.client.ardor.font.ttf.Glyph;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.image.BufferedImage;


public class OutlineEffect implements Effect {
	private final Color color;
	private final Stroke stroke;
	private final int width;

	public OutlineEffect(int width, Color color) {
		this.color = color;
		this.stroke = new BasicStroke(width, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_BEVEL);
		this.width = width;
	}

	public OutlineEffect(Color color, Stroke stroke, int width) {
		this.color = color;
		this.stroke = stroke;
		this.width = width;
	}

	@Override
	public void draw(BufferedImage image, Graphics2D g, Glyph glyph) {
		g = (Graphics2D) g.create();
		g.setStroke(stroke);
		g.setColor(color);
		g.draw(glyph.getShape());
		g.dispose();
	}

	@Override
	public int getPaddingTop() {
		return width;
	}

	@Override
	public int getPaddingLeft() {
		return width;
	}

	@Override
	public int getPaddingBottom() {
		return width;
	}

	@Override
	public int getPaddingRight() {
		return width;
	}

}
