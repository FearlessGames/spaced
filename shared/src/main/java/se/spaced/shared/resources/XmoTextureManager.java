package se.spaced.shared.resources;

import com.ardor3d.image.Texture;
import com.ardor3d.util.TextureKey;
import com.ardor3d.util.TextureManager;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import se.ardortech.TextureManagerImpl;
import se.fearlessgames.common.io.StreamLocator;
import se.spaced.shared.util.cache.Cache;
import se.spaced.shared.util.cache.CacheLoader;
import se.spaced.shared.util.cache.CacheManager;
import se.spaced.shared.util.cache.impl.ThreadSafeCache;

import java.util.concurrent.ExecutorService;

public class XmoTextureManager extends TextureManagerImpl {
	private final Cache<TextureKey, Void> textureKeyCache;

	@Inject
	public XmoTextureManager(StreamLocator streamLocator, ExecutorService executorService, @Named("xmoCachedManager") CacheManager xmoCacheManager) {
		super(streamLocator, executorService);

		textureKeyCache = new TextureCache(new CacheLoader<TextureKey, Void>() {
			@Override
			public Void load(TextureKey textureKey) {
				return null;
			}
		});

		//xmoCacheManager.addManagedCache(textureKeyCache); //TODO: for now do not add it ot the cache manager, ie; do not clear its textures
	}

	@Override
	public void onAfterTextureLoad(TextureKey source, Texture texture) {
		textureKeyCache.get(source);
	}

	private static class TextureCache extends ThreadSafeCache<TextureKey, Void> {
		private TextureCache(CacheLoader<TextureKey, Void> textureKeyVoidCacheLoader) {
			super(textureKeyVoidCacheLoader);
		}

		@Override
		public void invalidate(TextureKey textureKey) {
			TextureManager.removeFromCache(textureKey);
			TextureManager.cleanExpiredTextures(null);
			super.invalidate(textureKey);
		}

		@Override
		public void invalidateAll() {
			for (TextureKey textureKey : map.keySet()) {
				TextureManager.removeFromCache(textureKey);
				TextureKey.clearKey(textureKey);
			}
			TextureManager.cleanExpiredTextures(null);
			map.clear();
		}
	}
}
