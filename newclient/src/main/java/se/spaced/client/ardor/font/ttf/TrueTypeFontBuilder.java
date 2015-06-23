package se.spaced.client.ardor.font.ttf;

import com.ardor3d.extension.ui.text.CharacterDescriptor;
import com.ardor3d.extension.ui.text.font.UIFont;
import com.ardor3d.image.Image;
import com.ardor3d.image.Texture;
import com.ardor3d.image.Texture2D;
import com.ardor3d.image.util.AWTImageLoader;
import com.ardor3d.util.TextureKey;
import se.spaced.client.ardor.font.ttf.effects.ColorEffect;
import se.spaced.client.ardor.font.ttf.effects.Effect;

import java.awt.Color;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class TrueTypeFontBuilder {
	private final List<Effect> effects = new ArrayList<Effect>();
	private List<String> selectedGlyphs = new ArrayList<String>();
	private final Font baseFont;
	private final int size;
	private final boolean bold;
	private final boolean italic;

	private int paddingTop;
	private int paddingLeft;
	private int paddingBottom;
	private int paddingRight;

	public TrueTypeFontBuilder(Font baseFont, int size, boolean bold, boolean italic) {
		this.baseFont = baseFont;
		this.size = size;
		this.bold = bold;
		this.italic = italic;
	}

	public TrueTypeFontBuilder addEffect(Effect effect) {
		effects.add(effect);
		return this;
	}

	public TrueTypeFontBuilder addEffects(Collection<Effect> effects) {
		this.effects.addAll(effects);
		return this;
	}

	public TrueTypeFontBuilder addGlyps(String text) {
		selectedGlyphs.add(text);
		return this;
	}

	public TrueTypeFontBuilder useDefaultEffects() {
		effects.add(new ColorEffect(Color.WHITE));
		return this;
	}

	public TrueTypeFontBuilder addAscii() {
		return addGlyphs(32, 255);
	}

	public TrueTypeFontBuilder addGlyphs(int startCodePoint, int endCodePoint) {
		for (int codePoint = startCodePoint; codePoint <= endCodePoint; codePoint++) {
			selectedGlyphs.add(new String(Character.toChars(codePoint)));
		}
		return this;
	}

	public TrueTypeFontBuilder setPadding(int paddingTop, int paddingLeft, int paddingBottom, int paddingRight) {
		this.paddingTop = paddingTop;
		this.paddingLeft = paddingLeft;
		this.paddingBottom = paddingBottom;
		this.paddingRight = paddingRight;
		return this;
	}

	public UIFont createFont() {

		int paddingTop = this.paddingTop;
		int paddingLeft = this.paddingLeft;
		int paddingBottom = this.paddingBottom;
		int paddingRight = this.paddingRight;

		for (Effect effect : effects) {
			paddingTop += effect.getPaddingTop();
			paddingLeft += effect.getPaddingLeft();
			paddingBottom += effect.getPaddingBottom();
			paddingRight += effect.getPaddingRight();
		}

		TrueTypeFont font = new TrueTypeFont(baseFont, size, bold, italic, paddingTop, paddingLeft, paddingBottom, paddingRight);


		for (String selectedGlyph : selectedGlyphs) {
			font.addGlyphs(selectedGlyph);
		}

		TrueTypeFontRenderer renderer = new TrueTypeFontRenderer(font.getGlyphs(), effects);
		renderer.render();
		BufferedImage texture = renderer.getTexture();
		Map<Character, CharacterDescriptor> charDescriptors = renderer.getCharDescriptors();

		Image image = AWTImageLoader.makeArdor3dImage(texture, false);
		Texture2D tex = new Texture2D();

		tex.setImage(image);

		tex.setTextureKey(TextureKey.getRTTKey(Texture.MinificationFilter.Trilinear));
		tex.setMagnificationFilter(Texture.MagnificationFilter.Bilinear);
		tex.setMinificationFilter(Texture.MinificationFilter.Trilinear);


		UIFont uiFont = new UIFont(tex,
				charDescriptors,
				renderer.getMaxGlypthHeight(),
				font.getSize());

		return uiFont;
	}


}
