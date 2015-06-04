package se.spaced.client.resources.dae;

import com.ardor3d.extension.model.collada.jdom.ColladaImporter;
import com.ardor3d.extension.model.collada.jdom.data.ColladaStorage;
import com.ardor3d.util.export.binary.BinaryExporter;
import com.ardor3d.util.export.binary.BinaryImporter;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.spaced.shared.model.xmo.ColladaContentLoader;
import se.spaced.shared.model.xmo.ColladaContents;
import se.spaced.shared.util.cache.Cache;
import se.spaced.shared.util.cache.CacheLoader;
import se.spaced.shared.util.cache.CacheManager;
import se.spaced.shared.util.cache.impl.ThreadSafeCache;

import java.io.File;
import java.io.IOException;

public class CachingColladaContentLoader implements ColladaContentLoader, CacheLoader<String, ColladaContents> {
	private static final String BASE_PATH = "./resources/";
	private static final String CACHE_PATH = System.getProperty("user.home") + "/.spaced/cache/";
	private static final String POSTFIX = ".bin";

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final Cache<String, ColladaContents> daeContentsCache;
	private final ColladaImporter colladaImporter;

	@Inject
	public CachingColladaContentLoader(
			@Named("xmoCachedManager") CacheManager xmoCacheManager,
			ColladaImporter colladaImporter) {
		this.colladaImporter = colladaImporter;
		daeContentsCache = new ThreadSafeCache<String, ColladaContents>(this);

		xmoCacheManager.addManagedCache(daeContentsCache);
	}

	@Override
	public ColladaContents get(String file) {
		return daeContentsCache.get(file);
	}

	@Override
	public ColladaContents load(String path) {
		File daeFile = new File(BASE_PATH + path);
		File binFile = new File(CACHE_PATH + path + POSTFIX);


		long daeFileTimeStamp = daeFile.lastModified();
		long cacheFileTimeStamp = binFile.lastModified();
		if (!daeFile.exists()) {
			return loadFromDae(path);
		}
		if (daeFileTimeStamp != cacheFileTimeStamp) {
			return loadAndBuildCache(path, daeFileTimeStamp, binFile);
		} else {
			try {
				return loadFromCache(binFile);
			} catch (Exception e) {
				// This happens if ardor changes the binary format.
				return loadAndBuildCache(path, daeFileTimeStamp, binFile);
			}
		}
	}

	private ColladaContents loadFromDae(String path) {

		try {
			ColladaStorage colladaStorage = colladaImporter.load(path);
			return new ColladaContents(colladaStorage);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}

	private ColladaContents loadAndBuildCache(String path, long daeFileTimeStamp, File binFile) {
		logger.debug("Loading " + path + " from collada dae file");

		ColladaContents colladaContents = loadFromDae(path);

		try {
			new BinaryExporter().save(colladaContents.getColladaStorage(), binFile);
			binFile.setLastModified(daeFileTimeStamp);
		} catch (IOException e) {
			logger.error("Failed to persist bin cache for node " + path, e);
		}

		return colladaContents;
	}

	private ColladaContents loadFromCache(File binFile) {
		ColladaStorage contents = null;
		try {
			if (binFile.exists()) {
				logger.debug("Loading " + binFile + " from cache");
				contents = (ColladaStorage) new BinaryImporter().load(binFile);
			}
		} catch (IOException e) {
			logger.error("Failed to load bin cache for " + binFile, e);
		}

		return new ColladaContents(contents);
	}

}
