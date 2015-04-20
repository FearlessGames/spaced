package se.spaced.spacedit.ui.view.propertylist;

import javax.swing.JSlider;

public class ScaleSlider extends JSlider {
	private final int conversionRatio;

	public ScaleSlider(double min, double max, double start, int conversionRatio) {
		super((int) min * conversionRatio, (int) max * conversionRatio, (int) start * conversionRatio);
		this.conversionRatio = conversionRatio;
	}

	public double getDoubleValue() {
		return (double) super.getValue() / conversionRatio;	 //To change body of overridden methods use File | Settings | File Templates.
	}
}
