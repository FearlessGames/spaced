package se.spaced.client.ardor.font.ttf;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.image.BufferedImage;

public class RenderContextProvider {
	private final FontRenderContext fontRenderContext;
	private final Graphics2D graphics;

	public RenderContextProvider() {
		BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);

		graphics = image.createGraphics();
		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		graphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);

		fontRenderContext = graphics.getFontRenderContext();
	}

	public FontRenderContext getFontRenderContext() {
		return fontRenderContext;
	}

	public Graphics2D getGraphics() {
		return graphics;
	}
}
