package se.spaced.prototyping.resources.simple;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileResource implements Resource {
	private File file;

	public FileResource(File file) {
		this.file = file;
	}

	@Override
	public InputStream getInputStream() throws IOException {
		return new FileInputStream(file);
	}

	@Override
	public String getType() {
		return file.getName().substring(file.getName().lastIndexOf("."));
	}

	public OutputStream getOutputStream() throws IOException {
		return new FileOutputStream(file);
	}
}
