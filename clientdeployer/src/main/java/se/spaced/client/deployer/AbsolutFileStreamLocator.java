package se.spaced.client.deployer;

import com.google.common.io.ByteSink;
import com.google.common.io.ByteSource;
import com.google.common.io.Files;
import se.fearless.common.io.IOLocator;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

public class AbsolutFileStreamLocator implements IOLocator {
	@Override
	public ByteSource getByteSource(final String key) {
		return Files.asByteSource(new File(key));
	}

	@Override
	public ByteSink getByteSink(final String key) {
		return Files.asByteSink(new File(key));
	}

	@Override
	public Iterator<String> listKeys() {
		return new ArrayList<String>().iterator();
	}
}
