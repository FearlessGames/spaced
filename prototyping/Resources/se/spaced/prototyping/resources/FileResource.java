package se.spaced.prototyping.resources;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class FileResource implements WritableResource {
	private final File file;

	public FileResource(final File file) {
		this.file = file;
	}

	@Override
	public OutputStream getOutputStream() {
		try {
			return new FileOutputStream(file);
		} catch (FileNotFoundException e) {
			// This should not happen unless someone deletes the file during runtime
			throw new RuntimeException(e);
		}
	}

	@Override
	public InputStream getInputStream() {
		try {
			return new FileInputStream(file);
		} catch (FileNotFoundException e) {
			// This should not happen unless someone deletes the file during runtime
			throw new RuntimeException(e);
		}
	}

	@Override
	public String getKey() {
		return file.getName();
	}

	@Override
	public String getType() {
		return file.getName().substring(file.getName().lastIndexOf("."));
	}
}
