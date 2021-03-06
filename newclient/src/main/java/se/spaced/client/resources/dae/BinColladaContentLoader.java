package se.spaced.client.resources.dae;

import com.ardor3d.extension.model.collada.jdom.data.ColladaStorage;
import com.ardor3d.util.export.binary.BinaryImporter;
import com.google.common.io.ByteSource;
import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.fearless.common.io.IOLocator;
import se.spaced.shared.model.xmo.ColladaContentLoader;
import se.spaced.shared.model.xmo.ColladaContents;
import se.spaced.shared.util.cache.Cache;
import se.spaced.shared.util.cache.CacheLoader;
import se.spaced.shared.util.cache.impl.ThreadSafeCache;

import java.io.IOException;

public class BinColladaContentLoader implements ColladaContentLoader, CacheLoader<String, ColladaContents> {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private static final String POSTFIX = ".bin";
	private final IOLocator locator;
	private final Cache<String, ColladaContents> contentsCache;

	@Inject
	public BinColladaContentLoader(IOLocator locator) {
		this.locator = locator;
		contentsCache = new ThreadSafeCache<String, ColladaContents>(this);
	}

	@Override
	public ColladaContents get(String file) {
		return contentsCache.get(file);
	}

	@Override
	public ColladaContents load(String file) {
		String colladaFile = file + POSTFIX;

		ColladaStorage colladaStorage = null;

		try {
			ByteSource inputSupplier = locator.getByteSource(colladaFile);
			logger.debug("Loading " + colladaFile + " from bin cache");
			colladaStorage = (ColladaStorage) new BinaryImporter().load(inputSupplier.openBufferedStream());

		} catch (IOException e) {
			logger.info("Failed to load bin collada for " + colladaFile, e);
		}

		return new ColladaContents(colladaStorage);
	}
}
