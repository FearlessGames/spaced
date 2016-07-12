package se.spaced.client.deployer;

public class IndexedResource {
	private final String path;
	private final long crc32;
	private final long size;

	public IndexedResource(String path, long crc32, long size) {
		this.path = path;
		this.crc32 = crc32;
		this.size = size;
	}

	public String getPath() {
		return path;
	}

	public long getCrc32() {
		return crc32;
	}

	public long getSize() {
		return size;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		IndexedResource that = (IndexedResource) o;

		if (path != null ? !path.equals(that.path) : that.path != null) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		return path != null ? path.hashCode() : 0;
	}
}
