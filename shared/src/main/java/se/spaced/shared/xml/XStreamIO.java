package se.spaced.shared.xml;

import com.google.common.io.ByteSink;
import com.google.common.io.ByteSource;
import com.google.common.io.CharSink;
import com.google.common.io.CharSource;
import com.google.inject.Inject;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.ConversionException;
import se.fearless.common.io.IOLocator;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;

public class XStreamIO implements XmlIO {
	private final XStream xStream;
	private final IOLocator ioLocator;

	@Inject
	public XStreamIO(final XStream xStream, final IOLocator ioLocator) {
		this.xStream = xStream;
		this.ioLocator = ioLocator;

	}

	@Override
	public <T> T load(final Class<T> classToLoad, final String path) throws XmlIOException {

		ByteSource byteSource = ioLocator.getByteSource(path);
		CharSource charSource = byteSource.asCharSource(StandardCharsets.UTF_8);

		try (Reader reader = charSource.openBufferedStream()) {
			return classToLoad.cast(xStream.fromXML(reader));
		} catch (ConversionException | IOException e) {
			throw new XmlIOException(e);
		} catch (ClassCastException e) {
			throw new XmlIOException("Failed to load class of type " + classToLoad.toString(), e);
		}
	}

	@Override
	public void save(final Object o, final String path) throws XmlIOException {
		ByteSink byteSink = ioLocator.getByteSink(path);
		CharSink charSink = byteSink.asCharSink(StandardCharsets.UTF_8);

		try (Writer writer = charSink.openBufferedStream()) {
			xStream.toXML(o, writer);
		} catch (IOException e) {
			throw new XmlIOException(e);
		}
	}
}
