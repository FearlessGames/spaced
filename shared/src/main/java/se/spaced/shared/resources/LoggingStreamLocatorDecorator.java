package se.spaced.shared.resources;

import org.slf4j.Logger;
import se.fearless.common.io.StreamLocator;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.function.Supplier;

import static org.slf4j.LoggerFactory.getLogger;

public class LoggingStreamLocatorDecorator implements StreamLocator {
	private final Logger logger = getLogger(getClass());

	private final StreamLocator streamLocator;

	public LoggingStreamLocatorDecorator(StreamLocator streamLocator) {
		this.streamLocator = streamLocator;
	}

	@Override
	public Supplier<InputStream> getInputStreamSupplier(String s) {
		logger.info("getInputSupplier {}", s);
		return streamLocator.getInputStreamSupplier(s);
	}

	@Override
	public Supplier<OutputStream> getOutputStreamSupplier(String s) {
		logger.info("getOutputSupplier {}", s);
		return streamLocator.getOutputStreamSupplier(s);
	}

	@Override
	public Iterator<String> listKeys() {
		return streamLocator.listKeys();
	}
}
