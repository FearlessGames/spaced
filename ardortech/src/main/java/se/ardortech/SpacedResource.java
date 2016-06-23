package se.ardortech;

import com.ardor3d.util.export.InputCapsule;
import com.ardor3d.util.export.OutputCapsule;
import com.ardor3d.util.resource.ResourceSource;

import java.io.IOException;
import java.io.InputStream;
import java.util.function.Supplier;

public class SpacedResource implements ResourceSource {
	private final String key;
	private final Supplier<InputStream> inputSupplier;
	private final String type;

	public SpacedResource(String key, Supplier<InputStream> inputSupplier, String type) {
		this.key = key;
		this.inputSupplier = inputSupplier;
		this.type = type;
	}

	@Override
	public String toString() {
		return "SpacedResource{" +
				"key='" + key + '\'' +
				", inputSupplier=" + inputSupplier +
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
		return inputSupplier.get();
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
		if (type != null ? !type.equals(that.type) : that.type != null) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		int result = key != null ? key.hashCode() : 0;
		result = 31 * result + (type != null ? type.hashCode() : 0);
		return result;
	}


}
