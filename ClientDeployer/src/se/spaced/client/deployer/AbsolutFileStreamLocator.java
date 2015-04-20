package se.spaced.client.deployer;

import com.google.common.io.Files;
import com.google.common.io.InputSupplier;
import com.google.common.io.OutputSupplier;
import se.fearlessgames.common.io.StreamLocator;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;

public class AbsolutFileStreamLocator implements StreamLocator {
	@Override
	public InputSupplier<? extends InputStream> getInputSupplier(final String key) {
		return Files.newInputStreamSupplier(new File(key));
	}

	@Override
	public OutputSupplier<? extends OutputStream> getOutputSupplier(final String key) {
		return Files.newOutputStreamSupplier(new File(key));
	}

	@Override
	public Iterator<String> listKeys() {
		return new ArrayList<String>().iterator();
	}
}
