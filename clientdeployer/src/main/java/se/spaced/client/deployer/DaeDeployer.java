package se.spaced.client.deployer;

import com.ardor3d.extension.model.collada.jdom.ColladaImporter;
import com.ardor3d.extension.model.collada.jdom.data.ColladaStorage;
import com.ardor3d.util.export.Savable;
import com.ardor3d.util.export.binary.BinaryExporter;
import com.ardor3d.util.resource.ResourceLocator;
import org.slf4j.Logger;
import se.ardortech.SpacedResourceLocator;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Collection;

import static org.slf4j.LoggerFactory.getLogger;


public class DaeDeployer {
	private final Logger logger = getLogger(getClass());
	private static final String POSTFIX = ".bin";

	private final ColladaImporter colladaImporter;
	private final FileUtil fileUtil;


	public DaeDeployer() {
		fileUtil = new FileUtil();
		ResourceLocator spacedResourceLocator = new SpacedResourceLocator(new AbsolutFileStreamLocator());
		colladaImporter = new ColladaImporter().
				setModelLocator(spacedResourceLocator).
				setLoadTextures(false);
	}

	public void deploy(
			File file,
			File targetFile,
			String relativeFile,
			Collection<IndexedResource> indexedResources) throws IOException {
		ColladaStorage colladaStorage = colladaImporter.load(file.getPath());
		deploy(targetFile, relativeFile, indexedResources, colladaStorage, POSTFIX);
	}

	private void deploy(
			File targetFile,
			String relativeFile,
			Collection<IndexedResource> indexedResources,
			Savable savable, String postfix) throws IOException {
		byte[] nodeContent = export(savable);
		relativeFile += postfix;
		File nodeTargetFile = new File(targetFile.getPath() + postfix);
		long nodeCrc32 = fileUtil.writeCalcChecksum(nodeContent, nodeTargetFile);
		IndexedResource nodeResource = new IndexedResource(relativeFile, nodeCrc32, nodeTargetFile.length());
		if (indexedResources.contains(nodeResource)) {
			logger.warn("WARNING - COLLISION IN RESOURCES - " + nodeResource.getPath());
		}
		indexedResources.add(nodeResource);
	}

	byte[] export(Savable savable) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		new BinaryExporter().save(savable, baos);
		return baos.toByteArray();
	}
}
