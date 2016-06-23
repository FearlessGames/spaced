package se.spaced.shared.world.terrain;

import se.fearless.common.io.StreamLocator;

import java.io.IOException;
import java.io.InputStream;

public class Freeworld3dLoader extends AbstractHeightmapLoader {
	private final StreamLocator streamLocator;

	public Freeworld3dLoader(
			String fileName, int size, float widthScale, float heightScale, StreamLocator streamLocator) {
		super(fileName, size, widthScale, heightScale);
		this.streamLocator = streamLocator;
	}

	@Override
	public HeightMap loadHeightMap() throws IOException {
		data = new double[size * size];
		InputStream ins = streamLocator.getInputStreamSupplier(fileName).get();

		// freeworld3d seems to be exporting as 513x513x16bit
		// ("When the terrain is exported to 16bit, the height values are scaled to be between 0 and 2 ^ 16, roughly 65K.")

		try {
			for (int x = 0; x < size; x++) {
				for (int y = 0; y < size; y++) {
					int value = ins.read() | ins.read() << 8;
					data[y + x * size] = (float) value / (65535f);
				}
				ins.read(); //read a short and ignore it
				ins.read();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return HeightMap.fromArray(size, widthScale, heightScale, data);
	}

}
