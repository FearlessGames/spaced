package se.spaced.client.ardor.terrain;

import se.spaced.shared.world.terrain.AbstractHeightmapLoader;
import se.spaced.shared.world.terrain.HeightMap;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public class PngLoader extends AbstractHeightmapLoader {

	public PngLoader(String fileName, int size, double widthScale, double heightScale) {
		super(fileName, size, widthScale, heightScale);
	}

	@Override
	public HeightMap loadHeightMap() throws IOException {

		InputStream ins = getClass().getResourceAsStream(fileName);
		data = new double[size * size];

		BufferedImage image = ImageIO.read(ins);

		for (int y = 0; y < image.getHeight(); ++y) {
			for (int x = 0; x < image.getWidth(); ++x) {
				int color = image.getRGB(x, y);
				long r = color & 0xFFL;
				data[x + y * size] =  r / 255.0;
			}
		}
		return HeightMap.fromArray(size, widthScale, heightScale, data);
	}
}
