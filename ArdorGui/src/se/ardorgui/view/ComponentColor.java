package se.ardorgui.view;

import com.ardor3d.math.ColorRGBA;

import java.io.Serializable;

public class ComponentColor implements Serializable {
	private static final long serialVersionUID = 1L;

	private ColorRGBA baseColor;				// The color of the component
	private final ColorRGBA stateColor;			// The state of the component
	private final ColorRGBA resultColor;		// The JmeObject color

	public ComponentColor(final ColorRGBA baseColor, final ColorRGBA resultColor) {
		this.resultColor = resultColor;
		stateColor = new ColorRGBA(ColorRGBA.WHITE);
		setColor(baseColor);
	}

	public ColorRGBA setDisabled() {
		stateColor.setRed(0.3f);
		stateColor.setGreen(0.3f);
		stateColor.setBlue(0.3f);
		updateResultColor();
		return resultColor;
	}

	public ColorRGBA setEnabled() {

		stateColor.setRed(1.0f);
		stateColor.setGreen(1.0f);
		stateColor.setBlue(1.0f);
		updateResultColor();
		return resultColor;
	}

	private void updateResultColor() {
		resultColor.setRed(baseColor.getRed() * stateColor.getRed());
		resultColor.setGreen(baseColor.getGreen() * stateColor.getGreen());
		resultColor.setBlue(baseColor.getBlue() * stateColor.getBlue());
	}

	public ColorRGBA setFade(final float fade) {
		stateColor.setAlpha(fade);
		updateResultAlpha();
		return resultColor;
	}

	private void updateResultAlpha() {
		resultColor.setAlpha(baseColor.getAlpha() * stateColor.getAlpha());
	}

	public ColorRGBA setColor(final ColorRGBA color) {
		baseColor = color;
		updateResultColor();
		updateResultAlpha();
		return resultColor;
	}
}