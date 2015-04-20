package se.spaced.prototyping.resources.simple;

import java.io.IOException;
import java.io.InputStream;

public interface Resource {
	public InputStream getInputStream() throws IOException;

	public String getType();
}
