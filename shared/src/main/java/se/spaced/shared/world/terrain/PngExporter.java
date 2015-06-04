package se.spaced.shared.world.terrain;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.io.OutputStream;

public class PngExporter implements HeightMapExporter {
	public RenderedImage createImage(HeightMap heightMap, int width, int height, double min, double max) {
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics = image.createGraphics();
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				graphics.setColor(getColor(heightMap.getRawHeight(x, y)));
				graphics.fillRect(x, y, 1, 1);
			}
		}
		return image;
	}

	private Color getColor(double v) {
		return Color.getHSBColor(0.7f, 0.3f, (float)v);
	}

	public void writeImage(RenderedImage image, OutputStream out) throws IOException {
		ImageIO.write(image, "png", out);
	}

	@Override
	public void export(HeightMap heightMap, OutputStream out) throws IOException {
		RenderedImage image = createImage(heightMap,
				heightMap.getSize(),
				heightMap.getSize(),
				0,
				1.0);
		writeImage(image, out);
	}
}
