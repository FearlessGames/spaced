package se.spaced.shared.xml;

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import com.google.common.io.InputSupplier;
import com.google.common.io.OutputSupplier;
import com.google.inject.Inject;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.ConversionException;
import se.fearless.common.io.StreamLocator;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;

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
		InputSupplier<InputStreamReader> is = CharStreams.newReaderSupplier(streamLocator.getInputSupplier(path), Charsets.UTF_8);

		try (Reader reader = is.getInput()) {
			return classToLoad.cast(xStream.fromXML(reader));
		} catch (ConversionException | IOException e) {
			throw new XmlIOException(e);
		} catch (ClassCastException e) {
			throw new XmlIOException("Failed to load class of type " + classToLoad.toString(), e);
		}
	}

	@Override
	public void save(final Object o, final String path) throws XmlIOException {
		OutputSupplier<OutputStreamWriter> os = CharStreams.newWriterSupplier(streamLocator.getOutputSupplier(path), Charsets.UTF_8);

		try (Writer writer = os.getOutput()) {
			xStream.toXML(o, writer);
		} catch (IOException e) {
			throw new XmlIOException(e);
		}
	}
}
