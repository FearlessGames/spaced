package se.spaced.shared.resources;

import com.google.common.io.InputSupplier;
import com.google.common.io.OutputSupplier;
import org.slf4j.Logger;
import se.fearless.common.io.StreamLocator;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;

import static org.slf4j.LoggerFactory.getLogger;

public class LoggingStreamLocatorDecorator implements StreamLocator {
	private final Logger logger = getLogger(getClass());

	private final StreamLocator streamLocator;

	public LoggingStreamLocatorDecorator(StreamLocator streamLocator) {
		this.streamLocator = streamLocator;
	}

	@Override
	public InputSupplier<? extends InputStream> getInputSupplier(String s) {
		logger.info("getInputSupplier {}", s);
		return streamLocator.getInputSupplier(s);
	}

	@Override
	public OutputSupplier<? extends OutputStream> getOutputSupplier(String s) {
		logger.info("getOutputSupplier {}", s);
		return streamLocator.getOutputSupplier(s);
	}

	@Override
	public Iterator<String> listKeys() {
		return streamLocator.listKeys();
	}
}
