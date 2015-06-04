package se.spaced.shared.world.terrain;

import java.io.IOException;
import java.io.OutputStream;

public interface HeightMapExporter {
	void export(HeightMap heightMap, OutputStream out) throws IOException;
}
