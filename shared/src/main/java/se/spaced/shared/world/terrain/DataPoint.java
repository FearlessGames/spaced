package se.spaced.shared.world.terrain;

import se.krka.kahlua.integration.annotations.LuaMethod;
import se.krka.kahlua.integration.expose.ReturnValues;

public class DataPoint {
	private final int xIndex;
	private final int zIndex;
	private final double x;
	private final double z;
	private final double height;

	public DataPoint(int xIndex, int zIndex, double x, double z, double height) {
		this.xIndex = xIndex;
		this.zIndex = zIndex;
		this.x = x;
		this.z = z;
		this.height = height;
	}

	@LuaMethod(name = "GetX")
	public double getX() {
		return x;
	}

	@LuaMethod(name = "GetZ")
	public double getZ() {
		return z;
	}

	public int getXIndex() {
		return xIndex;
	}

	public int getZIndex() {
		return zIndex;
	}

	@LuaMethod(name = "GetIndices")
	public void getIndices(ReturnValues ret) {
		ret.push(xIndex);
		ret.push(zIndex);
	}

	@LuaMethod(name = "GetHeight")
	public double getHeight() {
		return height;
	}

	public boolean inRange(double x, double z, double radiusSq) {
		double dx = this.x - x;
		double dz = this.z - z;
		return dx * dx + dz * dz <= radiusSq;
	}

	@Override
	public String toString() {
		return "DataPoint{" +
				"xIndex=" + xIndex +
				", zIndex=" + zIndex +
				", x=" + x +
				", z=" + z +
				", height=" + height +
				'}';
	}
}
