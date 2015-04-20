package se.spaced.prototyping.resources;

import java.io.InputStream;

public class BasicResource implements Resource {
	private final String key;
	private final InputStream inputStream;

	public BasicResource(final String key, final InputStream inputStream) {
		this.key = key;
		this.inputStream = inputStream;
	}

	@Override
	public String getType() {
		return key.substring(key.lastIndexOf("."));
	}

	@Override
	public String getKey() {
		return key;
	}

	@Override
	public InputStream getInputStream() {
		return inputStream;
	}
}