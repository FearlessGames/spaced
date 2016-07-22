package se.spaced.client.deployer;

import org.slf4j.Logger;

import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

public class ResourceDeployer {
	private final Logger logger = getLogger(getClass());
	private final String targetBasePath;
	private final List<String> resourceBases;
	private final List<IndexedResource> indexedResources = new ArrayList<IndexedResource>();
	private final FileUtil fileUtil = new FileUtil();
	private final DaeDeployer daeDeployer;

	public static void main(String[] args) throws ResourceException {
		String targetBasePath = args[0];
		List<String> resourceBases = Arrays.asList(args).subList(1, args.length);

		ResourceDeployer resourceDeployer = new ResourceDeployer(targetBasePath, resourceBases);
		resourceDeployer.indexAndCopyResources();
	}

	public ResourceDeployer(String targetBasePath, List<String> resourceBases) {
		this.targetBasePath = targetBasePath;
		this.resourceBases = resourceBases;
		daeDeployer = new DaeDeployer();
	}

	public void indexAndCopyResources() throws ResourceException {

		File targetDir = new File(targetBasePath);
		targetDir.mkdirs();

		for (String resourceBase : resourceBases) {
			File directory = new File(resourceBase);
			index(resourceBase, directory);
		}

		buildIndexFile();
	}


	private void buildIndexFile() throws ResourceException {
		File indexTempFile = new File(targetBasePath + File.separator + "index.txt");
		try {
			FileWriter fw = new FileWriter(indexTempFile, false);
			for (IndexedResource indexedResource : indexedResources) {
				fw.write(indexedResource.getPath() + ":" +
						indexedResource.getSize() + ":" +
						Long.toHexString(indexedResource.getCrc32()) +
						"\r\n");
			}
			fw.close();

			long checkSum = fileUtil.calcChecksum(indexTempFile);

			fw = new FileWriter(new File(targetBasePath + File.separator + "index.crc"), false);
			fw.write(Long.toHexString(checkSum));
			fw.close();

		} catch (IOException e) {
			throw new ResourceException("Failed to create index file", e);
		}
	}


	private void index(String resourceBase, File directory) throws ResourceException {
		File[] files = directory.listFiles(new IgnoreSvnFileFilter());

		if (files == null) {
			logger.info("No files in directory {}", directory.getAbsolutePath());
		} else {
			for (File file : files) {
				if (file.isDirectory()) {
					index(resourceBase, file);
				} else {
					indexFile(resourceBase, file);
				}
			}
		}
	}

	private void indexFile(String resourceBase, File file) throws ResourceException {
		String relativeFile = file.getPath().substring(resourceBase.length() + 1);
		File targetFile = new File(targetBasePath + File.separator + relativeFile);
		File targetDir = new File(targetFile.getParent());
		targetDir.mkdirs();

		try {

			if (("dae").equalsIgnoreCase(fileUtil.getFileExtension(file))) {
				daeDeployer.deploy(file, targetFile, relativeFile, indexedResources);
			} else {
				long crc32 = fileUtil.copyAndCalcChecksum(file, targetFile);
				IndexedResource indexResource = new IndexedResource(relativeFile, crc32, file.length());
				if (indexedResources.contains(indexResource)) {
					logger.warn("WARNING - COLLISION IN RESOURCES - " + indexResource.getPath());
				}
				indexedResources.add(indexResource);
			}
		} catch (IOException e) {
			throw new ResourceException("IOException when trying to copy " + file, e);
		}
	}

	public List<IndexedResource> getIndexedResources() {
		return indexedResources;
	}

	private static class IgnoreSvnFileFilter implements FilenameFilter {
		@Override
		public boolean accept(File dir, String name) {
			return !name.equals(".svn");
		}
	}
}
