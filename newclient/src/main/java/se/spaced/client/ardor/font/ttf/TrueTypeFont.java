package se.spaced.client.ardor.font.ttf;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Rectangle;
import java.awt.font.GlyphVector;
import java.awt.font.TextAttribute;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TrueTypeFont {
	private static final int MAX_GLYPH_CODE = 0x10FFFF;
	private static final RenderContextProvider RENDER_CONTEXT_PROVIDER = new RenderContextProvider();


	private final int size;
	private final boolean bold;
	private final boolean italic;
	private final Font font;
	private final int ascent;
	private final int descent;
	private final int leading;
	private final int spaceWidth;

	private int paddingTop;
	private int paddingLeft;
	private int paddingBottom;
	private int paddingRight;

	private final List<Glyph> glyphs = new ArrayList<Glyph>();


	public TrueTypeFont(Font baseFont, int size, boolean bold, boolean italic) {
		this.size = size;
		this.bold = bold;
		this.italic = italic;

		Map attributes = baseFont.getAttributes();
		attributes.put(TextAttribute.SIZE, (float) size);
		attributes.put(TextAttribute.WEIGHT, bold ? TextAttribute.WEIGHT_BOLD : TextAttribute.WEIGHT_REGULAR);
		attributes.put(TextAttribute.POSTURE, italic ? TextAttribute.POSTURE_OBLIQUE : TextAttribute.POSTURE_REGULAR);

		font = baseFont.deriveFont(attributes);

		FontMetrics metrics = RENDER_CONTEXT_PROVIDER.getGraphics().getFontMetrics(font);
		ascent = metrics.getAscent();
		descent = metrics.getDescent();
		leading = metrics.getLeading();

		char[] chars = " ".toCharArray();
		GlyphVector vector = font.layoutGlyphVector(RENDER_CONTEXT_PROVIDER.getFontRenderContext(), chars, 0, chars.length, Font.LAYOUT_LEFT_TO_RIGHT);
		Rectangle bounds = vector.getGlyphLogicalBounds(0).getBounds();
		spaceWidth = bounds.width;

	}

	public TrueTypeFont(Font baseFont, int size, boolean bold, boolean italic, int paddingTop, int paddingLeft, int paddingBottom, int paddingRight) {
		this(baseFont, size, bold, italic);
		this.paddingTop = paddingTop;
		this.paddingLeft = paddingLeft;
		this.paddingBottom = paddingBottom;
		this.paddingRight = paddingRight;
	}


	public void addAsciiGlyphs() {
		addGlyphs(32, 255);
	}

	public void addGlyphs(int startCodePoint, int endCodePoint) {
		for (int codePoint = startCodePoint; codePoint <= endCodePoint; codePoint++) {
			addGlyphs(new String(Character.toChars(codePoint)));
		}
	}

	public void addGlyphs(String text) {
		if (text == null) {
			throw new IllegalArgumentException("text cannot be null.");
		}

		char[] chars = text.toCharArray();
		GlyphVector vector = font.layoutGlyphVector(RENDER_CONTEXT_PROVIDER.getFontRenderContext(), chars, 0, chars.length, Font.LAYOUT_LEFT_TO_RIGHT);
		for (int i = 0, n = vector.getNumGlyphs(); i < n; i++) {
			int codePoint = text.codePointAt(vector.getGlyphCharIndex(i));
			Rectangle bounds = getGlyphBounds(vector, i, codePoint);

			Glyph glyph = createGlyph(vector.getGlyphCode(i), codePoint, bounds, vector, i);
			if (!glyph.isMissing()) {
				glyphs.add(glyph);
			}
		}
	}

	private Glyph createGlyph(int glyphCode, int codePoint, Rectangle bounds, GlyphVector vector, int index) {
		boolean missing = false;
		if (glyphCode < 0 || glyphCode >= MAX_GLYPH_CODE || !font.canDisplay(codePoint)) {
			missing = true;
		}

		return new Glyph(codePoint, bounds, vector, index, paddingTop, paddingRight, paddingBottom, paddingLeft, ascent, missing);
	}

	private Rectangle getGlyphBounds(GlyphVector vector, int index, int codePoint) {
		Rectangle bounds = vector.getGlyphPixelBounds(index, RENDER_CONTEXT_PROVIDER.getFontRenderContext(), 0, 0);
		if (codePoint == ' ') {
			bounds.width = spaceWidth;
		}
		return bounds;
	}

	public List<Glyph> getGlyphs() {
		return glyphs;
	}

	public int getSize() {
		return size;
	}

	public boolean isBold() {
		return bold;
	}

	public boolean isItalic() {
		return italic;
	}

	public Font getFont() {
		return font;
	}

	public int getAscent() {
		return ascent;
	}

	public int getDescent() {
		return descent;
	}

	public int getLeading() {
		return leading;
	}

	public int getSpaceWidth() {
		return spaceWidth;
	}

	public int getPaddingTop() {
		return paddingTop;
	}

	public int getPaddingLeft() {
		return paddingLeft;
	}

	public int getPaddingBottom() {
		return paddingBottom;
	}

	public int getPaddingRight() {
		return paddingRight;
	}

}
