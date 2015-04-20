package se.spaced.shared.world.terrain;

import java.io.IOException;

public interface HeightmapLoader {
	HeightMap loadHeightMap() throws IOException;
}
