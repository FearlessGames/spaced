package se.spaced.client.ardor.font.ttf;

import com.google.common.io.ByteSource;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import se.fearless.common.io.IOLocator;
import se.spaced.shared.util.cache.Cache;
import se.spaced.shared.util.cache.CacheLoader;
import se.spaced.shared.util.cache.impl.ThreadSafeCache;

import java.awt.*;
import java.io.IOException;

@Singleton
public class TrueTypeFileLoader {
	private final Cache<String, Font> fileCache;

	@Inject
	public TrueTypeFileLoader(final IOLocator streamLocator) {
		fileCache = new ThreadSafeCache<String, Font>(new CacheLoader<String, Font>() {
			@Override
			public Font load(String s) {
				ByteSource byteSource = streamLocator.getByteSource(s);
				try {
					return Font.createFont(Font.TRUETYPE_FONT, byteSource.openBufferedStream());
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
