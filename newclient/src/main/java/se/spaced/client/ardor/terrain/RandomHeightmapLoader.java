package se.spaced.client.ardor.terrain;

import com.ardor3d.extension.terrain.heightmap.MidPointHeightMapGenerator;
import se.spaced.shared.world.terrain.AbstractHeightmapLoader;
import se.spaced.shared.world.terrain.HeightMap;

import java.io.IOException;

public class RandomHeightmapLoader extends AbstractHeightmapLoader {
	public RandomHeightmapLoader(String fileName, int size, double widthScale, double heightScale) {
		super(fileName, size, widthScale, heightScale);
	}

	@Override
	public HeightMap loadHeightMap() throws IOException {
		final MidPointHeightMapGenerator raw = new MidPointHeightMapGenerator(size, 1.0f);
		float[] rawHeightData = raw.getHeightData();
		data = new double[rawHeightData.length];
		for (int i = 0; i < rawHeightData.length; i++) {
			data[i] = rawHeightData[i];
		}
		return HeightMap.fromArray(size, widthScale, heightScale, data);
	}

}
