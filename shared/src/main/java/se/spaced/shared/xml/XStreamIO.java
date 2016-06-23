package se.spaced.shared.xml;

import com.google.common.base.Charsets;
import com.google.inject.Inject;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.ConversionException;
import se.fearless.common.io.InputReaderSupplier;
import se.fearless.common.io.OutputStreamWriterSupplier;
import se.fearless.common.io.StreamLocator;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.function.Supplier;

public class XStreamIO implements XmlIO {
	private final XStream xStream;
	private final StreamLocator streamLocator;

	@Inject
	public XStreamIO(final XStream xStream, final StreamLocator streamLocator) {
		this.xStream = xStream;
		this.streamLocator = streamLocator;

	}

	@Override
	public <T> T load(final Class<T> classToLoad, final String path) throws XmlIOException {

		Supplier<InputStream> inputSupplier = streamLocator.getInputStreamSupplier(path);
		Supplier<InputStreamReader> is = InputReaderSupplier.asInputReaderSupplier(inputSupplier, StandardCharsets.UTF_8);

		try (Reader reader = is.get()) {
			return classToLoad.cast(xStream.fromXML(reader));
		} catch (ConversionException | IOException e) {
			throw new XmlIOException(e);
		} catch (ClassCastException e) {
			throw new XmlIOException("Failed to load class of type " + classToLoad.toString(), e);
		}
	}

	@Override
	public void save(final Object o, final String path) throws XmlIOException {
		Supplier<? extends OutputStream> outputStreamSupplier = streamLocator.getOutputStreamSupplier(path);
		Supplier<OutputStreamWriter> os = OutputStreamWriterSupplier.asOutputStreamWriter(outputStreamSupplier.get(), Charsets.UTF_8);

		try (Writer writer = os.get()) {
			xStream.toXML(o, writer);
		} catch (IOException e) {
			throw new XmlIOException(e);
		}
	}
}
