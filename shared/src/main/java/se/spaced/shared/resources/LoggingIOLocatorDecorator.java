package se.spaced.shared.resources;

import com.google.common.io.ByteSink;
import com.google.common.io.ByteSource;
import org.slf4j.Logger;
import se.fearless.common.io.IOLocator;

import java.util.Iterator;

import static org.slf4j.LoggerFactory.getLogger;

public class LoggingIOLocatorDecorator implements IOLocator {
	private final Logger logger = getLogger(getClass());

	private final IOLocator ioLocator;

	public LoggingIOLocatorDecorator(IOLocator ioLocator) {
		this.ioLocator = ioLocator;
	}

	@Override
	public ByteSource getByteSource(String s) {
		logger.info("getByteSource {}", s);
		return ioLocator.getByteSource(s);
	}

	@Override
	public ByteSink getByteSink(String s) {
		logger.info("getByteSink {}", s);
		return ioLocator.getByteSink(s);
	}

	@Override
	public Iterator<String> listKeys() {
		return ioLocator.listKeys();
	}
}
