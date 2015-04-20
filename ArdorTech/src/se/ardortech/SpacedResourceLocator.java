package se.ardortech;

import com.ardor3d.util.resource.ResourceLocator;
import com.ardor3d.util.resource.ResourceSource;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import se.fearlessgames.common.io.StreamLocator;

@Singleton
public class SpacedResourceLocator implements ResourceLocator {
	private final StreamLocator streamLocator;

	@Inject
	public SpacedResourceLocator(StreamLocator streamLocator) {
		this.streamLocator = streamLocator;
	}

	@Override
	public ResourceSource locateResource(String resourceName) {
		String type = "";
		if (resourceName != null) {
			final int dot = resourceName.lastIndexOf('.');
			if (dot >= 0) {
				type = resourceName.substring(dot);
			}
		}
		return new SpacedResource(resourceName, streamLocator.getInputSupplier(resourceName), type);
	}
}
