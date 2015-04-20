package se.fearless.bender.services;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class WGet {
	private static final int BUFFER_SIZE = 1024;

	String getContent(URL url) throws IOException {
		byte[] buffer = new byte[BUFFER_SIZE];
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		InputStream is = url.openStream();
		int len;
		while ((len = is.read(buffer)) >= 0) {
			baos.write(buffer, 0, len);
		}
		is.close();

		return new String(baos.toByteArray());
	}
}