package se.spaced.prototyping.resources;

public interface ResourceLocator {
	Resource getResource(String key);
	WritableResource getWritableResource(String key);
}
