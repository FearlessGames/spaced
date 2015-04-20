package se.spaced.prototyping.resources;

public class CompositeResourceLocator implements ResourceLocator {
	private final ResourceLocator[] resourceLocators;

	public CompositeResourceLocator(final ResourceLocator... resourceLocators) {
		this.resourceLocators = resourceLocators;
	}

	@Override
	public Resource getResource(final String key) {
		for (ResourceLocator resourceLocator : resourceLocators) {
			Resource resource = resourceLocator.getResource(key);
			if(resource != null) {
				return resource;
			}
		}
		return null;
	}

	@Override
	public WritableResource getWritableResource(final String key) {
		for (ResourceLocator resourceLocator : resourceLocators) {
			WritableResource resource = resourceLocator.getWritableResource(key);
			if(resource != null) {
				return resource;
			}
		}
		return null;
	}
}
