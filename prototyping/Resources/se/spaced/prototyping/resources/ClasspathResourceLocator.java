package se.spaced.prototyping.resources;

import java.io.InputStream;

public class ClasspathResourceLocator implements ResourceLocator {
	@Override
	public Resource getResource(final String key) {
		InputStream inputStream = getClass().getResourceAsStream(key);
		if(inputStream != null) {
			return new BasicResource(key, inputStream);
		}

		return null;
	}

	@Override
	public WritableResource getWritableResource(final String key) {
		// Classpath resources can't be written to
		return null;
	}
}
