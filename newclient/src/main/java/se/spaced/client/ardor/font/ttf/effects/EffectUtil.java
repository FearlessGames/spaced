package se.spaced.client.ardor.font.ttf.effects;

import se.spaced.client.ardor.font.ttf.TrueTypeFontRenderer;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class EffectUtil {

	private static final BufferedImage SCRATCH_IMAGE = new BufferedImage(TrueTypeFontRenderer.MAX_GLYPH_SIZE, TrueTypeFontRenderer.MAX_GLYPH_SIZE,
			BufferedImage.TYPE_INT_ARGB);

	private EffectUtil() {
	}

	public static BufferedImage getScratchImage() {
		Graphics2D g = (Graphics2D) SCRATCH_IMAGE.getGraphics();
		g.setComposite(AlphaComposite.Clear);
		g.fillRect(0, 0, TrueTypeFontRenderer.MAX_GLYPH_SIZE, TrueTypeFontRenderer.MAX_GLYPH_SIZE);
		g.setComposite(AlphaComposite.SrcOver);
		g.setColor(Color.white);
		return SCRATCH_IMAGE;
	}

}
