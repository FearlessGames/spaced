package se.spaced.client.settings.ui;

import org.lwjgl.opengl.DisplayMode;

import java.util.Comparator;

class DisplayModeComparator implements Comparator<DisplayMode> {
	@Override
	public int compare(DisplayMode o1, DisplayMode o2) {
		int widthDiff = o1.getWidth() - o2.getWidth();
		if (widthDiff == 0) {
			int heightDiff = o1.getHeight() - o2.getHeight();
			if (heightDiff == 0) {
				int bppDiff = o1.getBitsPerPixel() - o2.getBitsPerPixel();
				if (bppDiff == 0) {
					return o1.getFrequency() - o2.getFrequency();
				}
				return bppDiff;
			}
			return heightDiff;
		}
		return widthDiff;
	}
}
