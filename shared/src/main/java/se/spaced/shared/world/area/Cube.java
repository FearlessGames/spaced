package se.spaced.shared.world.area;

import se.ardortech.math.SpacedRotation;
import se.ardortech.math.SpacedVector3;

public class Cube implements Geometry {
	private final SpacedVector3 corner;
	private final int width;
	private final int height;
	private final int depth;
	private final SpacedRotation rotation;

	public Cube(SpacedVector3 corner, int width, int height, int depth, SpacedRotation rotation) {
		this.corner = corner;
		this.width = width;
		this.height = height;
		this.depth = depth;
		this.rotation = rotation;
	}

	public SpacedVector3 getCorner() {
		return corner;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public int getDepth() {
		return depth;
	}

	public SpacedRotation getRotation() {
		return rotation;
	}
}
