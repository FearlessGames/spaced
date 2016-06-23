package se.spaced.client.resources.dae;

import com.ardor3d.extension.model.collada.jdom.data.ColladaStorage;
import com.ardor3d.util.export.binary.BinaryImporter;
import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.fearless.common.io.StreamLocator;
import se.spaced.shared.model.xmo.ColladaContentLoader;
import se.spaced.shared.model.xmo.ColladaContents;
import se.spaced.shared.util.cache.Cache;
import se.spaced.shared.util.cache.CacheLoader;
import se.spaced.shared.util.cache.impl.ThreadSafeCache;

import java.io.IOException;
import java.io.InputStream;
import java.util.function.Supplier;

public class BinColladaContentLoader implements ColladaContentLoader, CacheLoader<String, ColladaContents> {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private static final String POSTFIX = ".bin";
	private final StreamLocator streamLocator;
	private final Cache<String, ColladaContents> contentsCache;

	@Inject
	public BinColladaContentLoader(StreamLocator streamLocator) {
		this.streamLocator = streamLocator;
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
			Supplier<InputStream> inputSupplier = streamLocator.getInputStreamSupplier(colladaFile);
			logger.debug("Loading " + colladaFile + " from bin cache");
			colladaStorage = (ColladaStorage) new BinaryImporter().load(inputSupplier.get());

		} catch (IOException e) {
			logger.info("Failed to load bin collada for " + colladaFile, e);
		}

		return new ColladaContents(colladaStorage);
	}
}
