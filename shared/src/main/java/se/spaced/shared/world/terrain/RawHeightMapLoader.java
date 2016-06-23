package se.spaced.shared.world.terrain;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.function.Supplier;

public class RawHeightMapLoader implements HeightmapLoader {

	private final float widthMapSize;
	private final float heightScale;
	private final Supplier<InputStream> inputSupplier;

	public RawHeightMapLoader(float widthMapSize, float heightScale, Supplier<InputStream> inputSupplier) {
		this.widthMapSize = widthMapSize;
		this.heightScale = heightScale;
		this.inputSupplier = inputSupplier;

	}

	@Override
	public HeightMap loadHeightMap() throws IOException {

		InputStream ins = inputSupplier.get();
		DataInputStream dis = new DataInputStream(ins);
		int header = dis.readUnsignedShort();
		if (header != RawDataHeightMapExporter.HEADER_MARKER) {
			throw new HeightMapLoadException(String.format("Bad header marker data. Expected %d but got %d", RawDataHeightMapExporter.HEADER_MARKER, header));
		}

		int size = dis.readUnsignedShort();
		double[] data = new double[size * size];
		try {
			for (int i = 0; i < data.length; i++) {
				data[i] = dis.readUnsignedShort() / (65535f);
			}
		} catch (IOException e) {
			throw	new HeightMapLoadException("Failed to load heightmap data ", e);
		}

		return HeightMap.fromArray(size, widthMapSize, heightScale, data);
	}

}
