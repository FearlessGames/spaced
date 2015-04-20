package se.spaced.client.ardor.font;

import com.ardor3d.extension.ui.UIComponent;
import com.ardor3d.extension.ui.text.StyleConstants;
import com.ardor3d.extension.ui.text.font.FontProvider;
import com.ardor3d.extension.ui.text.font.UIFont;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.spaced.client.ardor.font.ttf.FontFamily;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class SpacedFontProvider implements FontProvider {
	private static final String DEFAULT_FONT_FAMILY = "arial";
	private final Logger log = LoggerFactory.getLogger(getClass());

	private final Map<String, FontFamily> fontFamilies = Maps.newHashMap();

	@Inject
	public SpacedFontProvider() {
	}

	public void registerFontFamily(FontFamily fontFamily) {
		fontFamilies.put(fontFamily.getKey(), fontFamily);
	}


	@Override
	public UIFont getClosestMatchingFont(final Map<String, Object> currentStyles, final AtomicReference<Double> scale) {
		final boolean isBold = currentStyles.containsKey(StyleConstants.KEY_BOLD) ? (Boolean) currentStyles
				.get(StyleConstants.KEY_BOLD) : false;
		final boolean isItalic = currentStyles.containsKey(StyleConstants.KEY_ITALICS) ? (Boolean) currentStyles
				.get(StyleConstants.KEY_ITALICS) : false;
		final int size = currentStyles.containsKey(StyleConstants.KEY_SIZE) ? (Integer) currentStyles
				.get(StyleConstants.KEY_SIZE) : UIComponent.getDefaultFontSize();

		final String family = currentStyles.containsKey(StyleConstants.KEY_FAMILY) ? currentStyles.get(
				StyleConstants.KEY_FAMILY).toString() : DEFAULT_FONT_FAMILY;


		String key = FontFamily.getKey(family, isBold, isItalic);
		if (!fontFamilies.containsKey(key)) {
			log.warn("Unable to find " + key + " using default font instead!");
			key = FontFamily.getKey(DEFAULT_FONT_FAMILY, false, false);
		}

		scale.set(1d);
		FontFamily fontFamily = fontFamilies.get(key);
		return fontFamily.getOrCreateFont(size);

	}


}
