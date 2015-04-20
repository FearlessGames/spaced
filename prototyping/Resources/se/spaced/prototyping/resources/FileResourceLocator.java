package se.spaced.prototyping.resources;

import java.io.File;

public class FileResourceLocator implements ResourceLocator {
	private final String rootDir;

	public FileResourceLocator(final String rootDir) {
		this.rootDir = rootDir;
	}

	@Override
	public Resource getResource(final String key) {
		return internalGetResource(key);
	}

	@Override
	public WritableResource getWritableResource(final String key) {
		return internalGetResource(key);
	}

	private WritableResource internalGetResource(final String key) {
		final File file = new File(rootDir, key);
		if(file.exists()) {
			return new FileResource(file);
		}

		return null;
	}
}
