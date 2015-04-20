package se.spaced.prototyping.resources;

import java.io.InputStream;

public interface Resource {
	InputStream getInputStream();
	String getKey();
	String getType();
}
