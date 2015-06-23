package se.spaced.client.ardor.terrain;

import com.ardor3d.extension.terrain.client.TerrainDataProvider;
import com.ardor3d.extension.terrain.client.TerrainSource;
import com.ardor3d.extension.terrain.client.TextureSource;
import com.ardor3d.image.Texture;
import com.ardor3d.math.type.ReadOnlyVector3;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import se.ardortech.math.SpacedVector3;
import se.spaced.shared.world.terrain.HeightMap;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ArrayTextureTerrainDataProvider implements TerrainDataProvider {
	private static final int TILE_SIZE = 128;

	private final List<HeightMap> heightMaps;
	private final int size;
	private final ReadOnlyVector3 scale;
	private final Texture texture;
	// TODO: calculate clipLevelCount through size and tileSize
	private final int clipLevels;
	private final HeightmapTerrainSource terrainSource;

	public ArrayTextureTerrainDataProvider(
			HeightMap map, Texture texture, int clipLevels) {
		this.size = map.getSize();
		this.scale = new SpacedVector3(map.getWidthScale(), map.getHeightScale(), map.getWidthScale());
		this.texture = texture;
		this.clipLevels = clipLevels;

		heightMaps = Lists.newArrayList();
		heightMaps.add(map);

		for (int i = 1; i <= this.clipLevels; i++) {
			heightMaps.add(HeightMap.fromHeightMap(heightMaps.get(i - 1), 2));
		}

		Collections.reverse(heightMaps);
		terrainSource = new HeightmapTerrainSource(TILE_SIZE, heightMaps, scale, 0.0f, 1.0f);
	}

	@Override
	public Map<Integer, String> getAvailableMaps() throws Exception {
		final Map<Integer, String> maps = Maps.newHashMap();
		maps.put(0, "ArrayBasedMap");

		return maps;
	}

	@Override
	public TerrainSource getTerrainSource(final int mapId) {
		return terrainSource;
	}

	@Override
	public TextureSource getTextureSource(final int mapId) {
		return new InMemoryImageTextureSource(TILE_SIZE, texture, clipLevels, (int) (size * scale.getXf()));
	}

	@Override
	public TextureSource getNormalMapSource(int i) {
		return null;
	}

}

