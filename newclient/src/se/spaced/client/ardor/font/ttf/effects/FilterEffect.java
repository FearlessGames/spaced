package se.spaced.client.ardor.font.ttf.effects;

import se.spaced.client.ardor.font.ttf.Glyph;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;


public class FilterEffect implements Effect {

	private final BufferedImageOp filter;

	public FilterEffect(BufferedImageOp filter) {
		this.filter = filter;
	}


	@Override
	public void draw(BufferedImage image, Graphics2D g, Glyph glyph) {
		BufferedImage scratchImage = EffectUtil.getScratchImage();
		filter.filter(image, scratchImage);
		image.getGraphics().drawImage(scratchImage, 0, 0, null);
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
