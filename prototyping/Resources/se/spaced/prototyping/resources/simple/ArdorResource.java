package se.spaced.prototyping.resources.simple;

import com.ardor3d.util.export.InputCapsule;
import com.ardor3d.util.export.OutputCapsule;
import com.ardor3d.util.resource.ResourceSource;

import java.io.IOException;
import java.io.InputStream;

public class ArdorResource implements ResourceSource {
	private final Resource resource;

	public ArdorResource(Resource resource) {
		this.resource = resource;
	}

	@Override
	public String getName() {
		return "spacedResource";
	}

	@Override
	public String getType() {
		return resource.getType();
	}

	@Override
	public ResourceSource getRelativeSource(String s) {
		return null;
	}

	@Override
	public InputStream openStream() throws IOException {
		return resource.getInputStream();
	}

	@Override
	public void write(OutputCapsule outputCapsule) throws IOException {
	}

	@Override
	public void read(InputCapsule inputCapsule) throws IOException {
	}

	@Override
	public Class<?> getClassTag() {
		return null;
	}
}
