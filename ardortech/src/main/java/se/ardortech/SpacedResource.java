package se.ardortech;

import com.ardor3d.util.export.InputCapsule;
import com.ardor3d.util.export.OutputCapsule;
import com.ardor3d.util.resource.ResourceSource;
import com.google.common.io.ByteSource;

import java.io.IOException;
import java.io.InputStream;

public class SpacedResource implements ResourceSource {
	private final String key;
	private final ByteSource byteSource;
	private final String type;

	public SpacedResource(String key, ByteSource byteSource, String type) {
		this.key = key;
		this.byteSource = byteSource;
		this.type = type;
	}

	@Override
	public String toString() {
		return "SpacedResource{" +
				"key='" + key + '\'' +
				", byteSource=" + byteSource +
				", type='" + type + '\'' +
				'}';
	}

	@Override
	public String getName() {
		return null;
	}

	@Override
	public String getType() {
		return type;
	}

	@Override
	public ResourceSource getRelativeSource(String s) {
		return null;
	}

	@Override
	public InputStream openStream() throws IOException {
		return byteSource.openStream();
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

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		SpacedResource that = (SpacedResource) o;

		if (key != null ? !key.equals(that.key) : that.key != null) {
			return false;
		}
		return type != null ? type.equals(that.type) : that.type == null;
	}

	@Override
	public int hashCode() {
		int result = key != null ? key.hashCode() : 0;
		result = 31 * result + (type != null ? type.hashCode() : 0);
		return result;
	}


}
