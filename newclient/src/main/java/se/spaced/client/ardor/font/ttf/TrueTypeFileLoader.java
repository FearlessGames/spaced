package se.spaced.client.ardor.font.ttf;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import se.fearless.common.io.StreamLocator;
import se.spaced.shared.util.cache.Cache;
import se.spaced.shared.util.cache.CacheLoader;
import se.spaced.shared.util.cache.impl.ThreadSafeCache;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.function.Supplier;

@Singleton
public class TrueTypeFileLoader {
	private final Cache<String, Font> fileCache;

	@Inject
	public TrueTypeFileLoader(final StreamLocator streamLocator) {
		fileCache = new ThreadSafeCache<String, Font>(new CacheLoader<String, Font>() {
			@Override
			public Font load(String s) {
				Supplier<InputStream> inputSupplier = streamLocator.getInputStreamSupplier(s);
				try {
					return Font.createFont(Font.TRUETYPE_FONT, inputSupplier.get());
				} catch (FontFormatException | IOException e) {
					throw new RuntimeException("Failed to load " + s, e);
				}
			}
		});
	}

	public Font getFont(String resourceName) {
		return fileCache.get(resourceName);
	}
}
