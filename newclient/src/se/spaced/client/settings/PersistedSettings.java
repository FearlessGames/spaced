package se.spaced.client.settings;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.spaced.shared.xml.XmlIO;
import se.spaced.shared.xml.XmlIOException;

public class PersistedSettings<T> {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final XmlIO xmlIO;
	private final String fileName;
	private final Class<T> clazz;

	public PersistedSettings(XmlIO xmlIO, String fileName, Class<T> clazz) {
		this.xmlIO = xmlIO;
		this.fileName = fileName;
		this.clazz = clazz;
	}

	private T settings;

	public T get() {
		return settings;
	}

	public void set(T settings) {
		this.settings = settings;
	}

	public void save() {
		try {
			xmlIO.save(settings, fileName);
		} catch (XmlIOException e) {
			logger.error("Failed to save " + clazz + " file", e);
		}
	}

	public void load() {
		try {
			settings = xmlIO.load(clazz, fileName);
		} catch (XmlIOException e) {
			logger.info("Failed to load " + clazz);
			//todo
		}
	}
}
