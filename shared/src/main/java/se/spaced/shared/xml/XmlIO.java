package se.spaced.shared.xml;

public interface XmlIO {

	<T> T load(final Class<T> classToLoad, final String path) throws XmlIOException;

	void save(final Object o, final String path) throws XmlIOException;
}
