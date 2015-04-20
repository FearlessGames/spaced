package se.spaced.client.ardor.ui.api;

import com.ardor3d.extension.ui.text.StyleConstants;
import com.ardor3d.extension.ui.text.TextFactory;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import se.krka.kahlua.integration.annotations.LuaMethod;
import se.spaced.client.ardor.font.SpacedFontProvider;
import se.spaced.client.ardor.font.ttf.FontEffectFactory;
import se.spaced.client.ardor.font.ttf.FontFamily;
import se.spaced.client.ardor.font.ttf.TrueTypeFileLoader;
import se.spaced.client.ardor.font.ttf.effects.Effect;

import java.awt.Font;
import java.util.Map;

@Singleton
public class FontApi {

	private final SpacedFontProvider fontProvider;
	private final TrueTypeFileLoader trueTypeFileLoader;
	private final FontEffectFactory fontEffectFactory;

	@Inject
	public FontApi(SpacedFontProvider fontProvider, TrueTypeFileLoader trueTypeFileLoader) {
		this.fontProvider = fontProvider;
		this.trueTypeFileLoader = trueTypeFileLoader;
		TextFactory.INSTANCE.setFontProvider(fontProvider);
		fontEffectFactory = new FontEffectFactory();
	}

	@LuaMethod(name = "CreateFontFamily", global = true)
	public FontFamily createFontFamily(String resource, String name, boolean bold, boolean italic) {
		Font baseFont = trueTypeFileLoader.getFont(resource);
		FontFamily fontFamily = new FontFamily(name, bold, italic, baseFont);
		fontProvider.registerFontFamily(fontFamily);
		return fontFamily;
	}

	@LuaMethod(name = "CreateFontFamilyWithEffects", global = true)
	public FontFamily createFontFamily(String resource, String name, boolean bold, boolean italic, Effect... effects) {
		Font baseFont = trueTypeFileLoader.getFont(resource);
		FontFamily fontFamily = new FontFamily(name, bold, italic, baseFont, effects);
		fontProvider.registerFontFamily(fontFamily);
		return fontFamily;
	}

	public Map<String, Object> createFontStyle(String name, int size, boolean bold, boolean italic) {
		Map<String, Object> styles = Maps.newHashMap();
		styles.put(StyleConstants.KEY_FAMILY, name);
		styles.put(StyleConstants.KEY_SIZE, size);
		styles.put(StyleConstants.KEY_BOLD, bold);
		styles.put(StyleConstants.KEY_ITALICS, italic);
		return styles;
	}

	@LuaMethod(name = "GetFontEffectFactory", global = true)
	public FontEffectFactory getFontEffectFactory() {
		return fontEffectFactory;
	}

}