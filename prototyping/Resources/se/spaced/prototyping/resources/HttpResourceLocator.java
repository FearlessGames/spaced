package se.spaced.prototyping.resources;

// This class should load some sort of index from the http server so it doesn't have to do http requests for each file exists lookup
public class HttpResourceLocator implements ResourceLocator {
	@Override
	public Resource getResource(final String key) {
		return null;
	}

	@Override
	public WritableResource getWritableResource(final String key) {
		return null;
	}
}
