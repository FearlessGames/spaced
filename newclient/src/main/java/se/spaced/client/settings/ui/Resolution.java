package se.spaced.client.settings.ui;

import org.lwjgl.opengl.DisplayMode;

public class Resolution implements Comparable<Resolution> {
	private final DisplayMode displayMode;

	Resolution(DisplayMode displayMode) {
		this.displayMode = displayMode;
	}

	public int getHeight() {
		return displayMode.getHeight();
	}

	public int getWidth() {
		return displayMode.getWidth();
	}

	@Override
	public String toString() {
		return displayMode.getWidth() + "x" + displayMode.getHeight();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		Resolution that = (Resolution) o;

		return displayMode.getWidth() == that.displayMode.getWidth() &&
				displayMode.getHeight() == that.displayMode.getHeight();
	}

	@Override
	public int hashCode() {
		return displayMode != null ? displayMode.getHeight() * 31 + displayMode.getWidth() : 0;
	}

	@Override
	public int compareTo(Resolution o) {
		return new DisplayModeComparator().compare(displayMode, o.displayMode);
	}
}
