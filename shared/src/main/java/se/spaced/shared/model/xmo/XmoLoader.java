package se.spaced.shared.model.xmo;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import se.spaced.shared.util.cache.Cache;
import se.spaced.shared.util.cache.CacheLoader;
import se.spaced.shared.util.cache.CacheManager;
import se.spaced.shared.util.cache.impl.ThreadSafeCache;
import se.spaced.shared.xml.XmlIO;
import se.spaced.shared.xml.XmlIOException;

@Singleton
public class XmoLoader {
	private final Cache<String, XmoRoot> xmoFileCache;

	@Inject
	public XmoLoader(final XmlIO xmlIO, @Named("xmoCachedManager") CacheManager xmoCacheManager) {
		xmoFileCache = new ThreadSafeCache<String, XmoRoot>(new CacheLoader<String, XmoRoot>() {
			@Override
			public XmoRoot load(String path) {
				try {
					return xmlIO.load(XmoRoot.class, path);
				} catch (XmlIOException e) {
					throw new RuntimeException(e);
				}
			}
		});
		xmoCacheManager.addManagedCache(xmoFileCache);
	}


	public XmoRoot loadXmo(String xmoFile) throws XmlIOException {
		if (!xmoFile.endsWith(".xmo")) {
			xmoFile = xmoFile + ".xmo";
		}
		return xmoFileCache.get(xmoFile);
	}
}