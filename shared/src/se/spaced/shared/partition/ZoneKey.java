package se.spaced.shared.partition;

public class ZoneKey {
	private final int x;
	private final int y;
	private final int z;

	public ZoneKey(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getZ() {
		return z;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof ZoneKey)) {
			return false;
		}

		ZoneKey zone = (ZoneKey) o;

		if (x != zone.x) {
			return false;
		}
		if (y != zone.y) {
			return false;
		}
		if (z != zone.z) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		int result = x;
		result = 31 * result + y;
		result = 31 * result + z;
		return result;
	}
}
