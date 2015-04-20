package se.spaced.client.ardor.entity;

import com.ardor3d.math.ColorRGBA;

public class InteractionColors {
	private final ColorRGBA deadColor = new ColorRGBA(ColorRGBA.RED).multiplyLocal(0.2f);
	private final ColorRGBA targetColor = new ColorRGBA(ColorRGBA.WHITE).multiplyLocal(0.28f);
	private final ColorRGBA targetedPropColor = new ColorRGBA(ColorRGBA.RED).multiplyLocal(0.8f);

	public ColorRGBA getDeadColor() {
		return deadColor;
	}

	public ColorRGBA getTargetColor() {
		return targetColor;
	}

	public ColorRGBA getTargetedPropColor() {
		return targetedPropColor;
	}
}
