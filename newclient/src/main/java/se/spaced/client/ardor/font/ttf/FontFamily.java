package se.spaced.client.ardor.font.ttf;

import com.ardor3d.extension.ui.text.font.UIFont;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import se.krka.kahlua.integration.annotations.LuaMethod;
import se.spaced.client.ardor.font.ttf.effects.ColorEffect;
import se.spaced.client.ardor.font.ttf.effects.Effect;

import java.awt.Color;
import java.awt.Font;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class FontFamily {
	private final String name;
	private final boolean bold;
	private final boolean italic;
	private final Font fontFile;
	private final Map<Integer, UIFont> fontMap = Maps.newHashMap();
	private final List<Effect> effects = Lists.newArrayList();

	public FontFamily(String name, boolean bold, boolean italic, Font fontFile) {
		this.name = name;
		this.bold = bold;
		this.italic = italic;
		this.fontFile = fontFile;
		effects.add(new ColorEffect(Color.WHITE));
	}

	public FontFamily(String name, boolean bold, boolean italic, Font fontFile, Effect... effects) {
		this.name = name;
		this.bold = bold;
		this.italic = italic;
		this.fontFile = fontFile;
		Collections.addAll(this.effects, effects);
	}

	@LuaMethod(name = "GetName")
	public String getName() {
		return name;
	}

	public boolean isBold() {
		return bold;
	}

	public boolean isItalic() {
		return italic;
	}

	public String getKey() {
		return getKey(name, bold, italic);
	}

	public static String getKey(String name, boolean bold, boolean italic) {
		return name + ":" + bold + ":" + italic;
	}

	public UIFont getOrCreateFont(int size) {
		if (!fontMap.containsKey(size)) {
			UIFont uiFont = new TrueTypeFontBuilder(fontFile, size, bold, italic).
					addAscii().
					addEffects(effects).
					createFont();
			fontMap.put(size, uiFont);
		}

		return fontMap.get(size);
	}
}
