package se.spaced.client.ardor.terrain;


import com.ardor3d.extension.terrain.client.TerrainConfiguration;
import com.ardor3d.extension.terrain.client.TerrainSource;
import com.ardor3d.extension.terrain.util.Tile;
import com.ardor3d.math.type.ReadOnlyVector3;
import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.spaced.shared.world.terrain.HeightMap;

import java.util.BitSet;
import java.util.List;
import java.util.Set;

public class HeightmapTerrainSource implements TerrainSource {
	private final Logger log = LoggerFactory.getLogger(getClass());
	private final int tileSize;
	private final List<HeightMap> heightMaps;
	private final ReadOnlyVector3 scale;
	private final float heightMin;
	private final float heightMax;

	private final ThreadLocal<float[]> tileDataPool = new ThreadLocal<float[]>() {
		@Override
		protected float[] initialValue() {
			return new float[tileSize * tileSize];
		}
	};
	private final BitSet valid;

	public HeightmapTerrainSource(int tileSize, List<HeightMap> heightMaps, ReadOnlyVector3 scale,
											float heightMin, final float heightMax) {
		this.tileSize = tileSize;
		this.heightMaps = heightMaps;
		this.scale = scale;
		this.heightMin = heightMin;
		this.heightMax = heightMax;
		valid = new BitSet(heightMaps.size());
		valid.set(0, heightMaps.size() - 1);
	}

	@Override
	public TerrainConfiguration getConfiguration() throws Exception {
		return new TerrainConfiguration(heightMaps.size(), tileSize, scale, heightMin, heightMax, true);
	}

	@Override
	public Set<Tile> getValidTiles(final int clipmapLevel, final int tileX, final int tileY, final int numTilesX,
											 final int numTilesY) throws Exception {
		final Set<Tile> validTiles = Sets.newHashSet();

		final int heightMapSize = heightMaps.get(clipmapLevel).getSize();
		for (int y = 0; y < numTilesY; y++) {
			for (int x = 0; x < numTilesX; x++) {
				final int xx = tileX + x;
				final int yy = tileY + y;
				if (xx >= 0 && xx * tileSize <= heightMapSize && yy >= 0 && yy * tileSize <= heightMapSize) {
					final Tile tile = new Tile(xx, yy);
					validTiles.add(tile);
				}
			}
		}

		return validTiles;
	}

	@Override
	public Set<Tile> getInvalidTiles(final int clipmapLevel, final int tileX, final int tileY, final int numTilesX,
												final int numTilesY) throws Exception {
		if (!valid.get(clipmapLevel)) {
			Set<Tile> tiles = getValidTiles(clipmapLevel, tileX, tileY, numTilesX, numTilesY);
			valid.set(clipmapLevel);
			log.info("Invalidated tiles {}", clipmapLevel);
			return tiles;
		}
		return null;
	}

	@Override
	public int getContributorId(final int clipmapLevel, final Tile tile) {
		return 0;
	}

	@Override
	public float[] getTile(final int clipmapLevel, final Tile tile) throws Exception {
		final int tileX = tile.getX();
		final int tileY = tile.getY();
		HeightMap heightMap = heightMaps.get(clipmapLevel);

		final float[] data = tileDataPool.get();
		for (int y = 0; y < tileSize; y++) {
			for (int x = 0; x < tileSize; x++) {
				final int index = x + y * tileSize;

				final int heightX = tileX * tileSize + x;
				final int heightY = tileY * tileSize + y;
				data[index] = getHeight(heightMap, heightX, heightY);
			}
		}
		return data;
	}

	private float getHeight(HeightMap heightMap, final int x, final int y) {
		if (x < 0 || x >= heightMap.getSize() || y < 0 || y >= heightMap.getSize()) {
			return 0;
		}

		return (float) heightMap.getHeightData().at(x, y).getHeight();
	}

	public void invalidate() {
		valid.set(0, valid.size() - 1, false);
	}
}

