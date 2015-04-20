package se.spaced.client.settings.ui;

class ColorMode implements Comparable<ColorMode> {
	private final int colors;

	ColorMode(int colors) {
		this.colors = colors;
	}

	int getValue() {
		return colors;
	}

	@Override
	public String toString() {
		return colors + " bits";
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof ColorMode)) {
			return false;
		}

		ColorMode colorMode = (ColorMode) o;

		if (colors != colorMode.colors) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		return colors;
	}

	@Override
	public int compareTo(ColorMode o) {
		return colors - o.colors;
	}
}
