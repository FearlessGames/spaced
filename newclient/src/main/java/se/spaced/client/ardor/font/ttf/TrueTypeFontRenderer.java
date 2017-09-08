package se.spaced.client.ardor.font.ttf;

import com.ardor3d.extension.ui.text.CharacterDescriptor;
import com.google.common.collect.Maps;
import se.spaced.client.ardor.font.ttf.effects.Effect;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.util.List;
import java.util.Map;

public class TrueTypeFontRenderer {
	public static final int MAX_GLYPH_SIZE = 256;
	public static final int TYPE = BufferedImage.TYPE_INT_ARGB;

	private final List<Glyph> glyphs;
	private final List<Effect> effects;

	private int imageHeight;
	private int imageWidth;

	private BufferedImage texture;


	private final Map<Character, CharacterDescriptor> charDescriptors = Maps.newHashMap();


	public TrueTypeFontRenderer(List<Glyph> glyphs, List<Effect> effects) {
		this.glyphs = glyphs;
		this.effects = effects;
	}

	public void render() {
		calculateAndSetImageWidth();
		calculateAndSetImageHeight();

		texture = new BufferedImage(imageWidth, imageHeight, TYPE);
		Graphics2D graphics = texture.createGraphics();
		graphics.setColor(new Color(0, 0, 0, 0));
		//glyphsGraphics.fillRect(0, 0, MAX_GLYPH_SIZE, MAX_GLYPH_SIZE);
		graphics.setComposite(AlphaComposite.Src);

		renderGlyphs(graphics);

		graphics.dispose();
	}


	private void calculateAndSetImageWidth() {
		int totalWidth = 0;
		for (Glyph glyph : glyphs) {
			int width = Math.min(MAX_GLYPH_SIZE, glyph.getWidth());
			int height = Math.min(MAX_GLYPH_SIZE, glyph.getHeight());
			if (width == 0) {
				continue;
			}

			totalWidth = (int) (totalWidth + Math.sqrt(Math.pow(width, 2) * Math.pow(height, 2)));
		}

		this.imageWidth = nextPow2((int) Math.sqrt(totalWidth));
	}

	private void calculateAndSetImageHeight() {
		if (imageWidth == 0) {
			throw new RuntimeException("Calculated Image width is 0!");
		}

		int totalHeight = 0;
		int currentWidth = 0;
		int maxRowHeight = 0;

		for (Glyph glyph : glyphs) {
			int width = Math.min(MAX_GLYPH_SIZE, glyph.getWidth());
			int height = Math.min(MAX_GLYPH_SIZE, glyph.getHeight());

			if (width == 0) {
				continue;
			}

			maxRowHeight = Math.max(height, maxRowHeight);

			currentWidth += width;
			if (currentWidth >= imageWidth) {
				totalHeight += maxRowHeight;
				currentWidth = width;
				maxRowHeight = 0;
			}
		}
		totalHeight += maxRowHeight;

		imageHeight = nextPow2(totalHeight);
	}

	private int nextPow2(int v) {
		v--;
		v |= v >> 1;
		v |= v >> 2;
		v |= v >> 4;
		v |= v >> 8;
		v |= v >> 16;
		v++;

		return v;
	}


	private void renderGlyphs(Graphics2D graphics) {
		GlyphRenderer glyphRenderer = new GlyphRenderer();
		int x = 0;
		int y = 0;
		int maxRowHeight = 0;
		for (Glyph glyph : glyphs) {
			int width = Math.min(MAX_GLYPH_SIZE, glyph.getWidth());
			int height = Math.min(MAX_GLYPH_SIZE, glyph.getHeight());

			if (width == 0) {
				if (glyph.getAdvanceX() != 0) {
					addCharDescription(x, y, glyph, width, height);
				}
				continue;
			}

			maxRowHeight = Math.max(height, maxRowHeight);

			if (x + width >= imageWidth) {
				y += maxRowHeight;
				x = 0;
				maxRowHeight = 0;
			}


			BufferedImage glyphImage = glyphRenderer.render(glyph, width, height);

			graphics.drawImage(glyphImage, x, y, null);

			addCharDescription(x, y, glyph, width, height);

			x += width;

		}

		glyphRenderer.dispose();
	}

	private void addCharDescription(int x, int y, Glyph glyph, int width, int height) {
		CharacterDescriptor characterDescriptor = new CharacterDescriptor(
				x, y, width, height,
				(int) glyph.getAdvanceX(), glyph.getXOffset(), glyph.getYOffset(), 1, null);
		charDescriptors.put((char) glyph.getCodePoint(), characterDescriptor);
	}

	public BufferedImage getTexture() {
		return texture;
	}

	public Map<Character, CharacterDescriptor> getCharDescriptors() {
		return charDescriptors;
	}

	public int getMaxGlypthHeight() {
		int maxGlypthHeight = 0;
		for (Glyph glyph : glyphs) {
			maxGlypthHeight = Math.max(Math.min(MAX_GLYPH_SIZE, glyph.getHeight()), maxGlypthHeight);
		}
		return maxGlypthHeight;
	}

	private class GlyphRenderer {
		private final BufferedImage glyphImage;
		private final Graphics2D glyphsGraphics;

		private GlyphRenderer() {
			glyphImage = new BufferedImage(MAX_GLYPH_SIZE, MAX_GLYPH_SIZE, TYPE);
			glyphsGraphics = glyphImage.createGraphics();
			glyphsGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			glyphsGraphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			glyphsGraphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
		}

		private BufferedImage render(Glyph glyph, int width, int height) {
			glyphsGraphics.setComposite(AlphaComposite.Clear);
			glyphsGraphics.setColor(new Color(0, 0, 0, 0));
			glyphsGraphics.fillRect(0, 0, MAX_GLYPH_SIZE, MAX_GLYPH_SIZE);
			glyphsGraphics.setComposite(AlphaComposite.Src);

			for (Effect effect : effects) {
				effect.draw(glyphImage, glyphsGraphics, glyph);
			}

			WritableRaster raster = glyphImage.getRaster();
			BufferedImage bufferedImage = new BufferedImage(width, height, TYPE);
			bufferedImage.setData(raster);
			return bufferedImage;
		}

		private void dispose() {
			glyphsGraphics.dispose();
		}
	}


	public int getImageWidth() {
		return imageWidth;
	}

	public int getImageHeight() {
		return imageHeight;
	}
}
