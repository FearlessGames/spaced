package se.spaced.shared.world.terrain;

import se.ardortech.math.SpacedVector3;

public class WorldGround {
	private static final double DEFAULT_SEALEVEL = -192.0;
	private static final SpacedVector3 X_STEP = new SpacedVector3(0.1, 0, 0);
	private static final SpacedVector3 Z_STEP = new SpacedVector3(0, 0, 0.1);

	private final HeightMap heightMap;
	private final double seaLevel;

	public WorldGround(HeightMap heightMap, double seaLevel) {
		this.heightMap = heightMap;
		this.seaLevel = seaLevel;
	}

	public WorldGround(HeightMap heightMap) {
		this(heightMap, DEFAULT_SEALEVEL);
	}

	public double getHeight(double x, double z) {
		return heightMap.getHeight(x, z) + getSeaLevelAdjustment();
	}

	public double getSeaLevelAdjustment() {
		return seaLevel;
	}

}
