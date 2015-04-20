package se.fearless;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class CodecUtils {
	private CodecUtils() {
	}

	public static InputStream getInputStream(ByteArrayOutputStream output) {
		return new ByteArrayInputStream(output.toByteArray());
	}
}
