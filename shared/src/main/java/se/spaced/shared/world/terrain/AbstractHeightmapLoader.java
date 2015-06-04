package se.spaced.shared.world.terrain;

public abstract class AbstractHeightmapLoader implements HeightmapLoader {
	protected final String fileName;
	protected final int size;
	protected final double widthScale;
	protected final double heightScale;

	protected double[] data;

	protected AbstractHeightmapLoader(String fileName, int size, double widthScale, double heightScale) {
		this.size = size;
		this.fileName = fileName;
		this.widthScale = widthScale;
		this.heightScale = heightScale;
	}

	public String getFileName() {
		return fileName;
	}

	public int getSize() {
		return size;
	}

	public double[] getData() {
		return data;
	}


}
