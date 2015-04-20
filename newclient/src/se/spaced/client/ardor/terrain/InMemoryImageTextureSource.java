package se.spaced.client.ardor.terrain;

import com.ardor3d.extension.terrain.client.TextureConfiguration;
import com.ardor3d.extension.terrain.client.TextureSource;
import com.ardor3d.extension.terrain.util.Tile;
import com.ardor3d.image.Texture;
import com.ardor3d.image.TextureStoreFormat;
import com.ardor3d.util.geom.BufferUtils;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.Map;
import java.util.Set;

public class InMemoryImageTextureSource implements TextureSource {
	private final Logger log = LoggerFactory.getLogger(getClass());
	private final int tileSize;
	private final Texture texture;

	private final ThreadLocal<ByteBuffer> tileDataPool = new ThreadLocal<ByteBuffer>() {
		@Override
		protected ByteBuffer initialValue() {
			return BufferUtils.createByteBufferOnHeap(tileSize * tileSize * 3);
		}
	};
	private final int availableClipmapLevels;
	private final int mapSize;

	public InMemoryImageTextureSource(
			final int tileSize,
			Texture texture,
			int availableClipmapLevels, int mapSize) {
		this.tileSize = tileSize;
		this.texture = texture;
		this.availableClipmapLevels = availableClipmapLevels;
		this.mapSize = mapSize;
	}

	@Override
	public TextureConfiguration getConfiguration() throws Exception {
		final Map<Integer, TextureStoreFormat> textureStoreFormat = Maps.newHashMap();
		textureStoreFormat.put(0, TextureStoreFormat.RGB8);

		return new TextureConfiguration(availableClipmapLevels, textureStoreFormat, tileSize, 0.25f, true, false);
	}

	@Override
	public Set<Tile> getValidTiles(
			int clipmapLevel, int tileX, int tileY, int numTilesX,
			int numTilesY) throws Exception {
		Set<Tile> validTiles = Sets.newHashSet();

		int levelSize = 1 << availableClipmapLevels - clipmapLevel - 1;
		for (int y = 0; y < numTilesY; y++) {
			for (int x = 0; x < numTilesX; x++) {
				final int xx = tileX + x;
				final int yy = tileY + y;
				if (xx >= 0 && xx * tileSize * levelSize < mapSize && yy >= 0 && yy * tileSize * levelSize < mapSize) {
					final Tile tile = new Tile(xx, yy);
					validTiles.add(tile);
				}
			}
		}

		return validTiles;
	}

	@Override
	public Set<Tile> getInvalidTiles(
			final int clipmapLevel, final int tileX, final int tileY, final int numTilesX,
			final int numTilesY) throws Exception {
		return null;
	}

	@Override
	public int getContributorId(final int clipmapLevel, final Tile tile) {
		return 0;
	}

	@Override
	public ByteBuffer getTile(final int clipmapLevel, final Tile tile) throws Exception {
		final int tileX = tile.getX();
		final int tileY = tile.getY();

		final ByteBuffer data = tileDataPool.get();
		ByteBuffer data1 = texture.getImage().getData(0);
		int textureWidth = texture.getImage().getWidth();
		int baseClipmapLevel = availableClipmapLevels - clipmapLevel - 1;
		int levelSize = 1 << baseClipmapLevel;

		int textureXStart = tileX * tileSize * levelSize;
		int textureYStart = tileY * tileSize * levelSize;
		log.debug("clipmapLevel {} textureStart ({}, {}) levelSize {}",
				new Integer[]{clipmapLevel, textureXStart, textureYStart, levelSize});

		for (int y = 0; y < tileSize; y++) {
			for (int x = 0; x < tileSize; x++) {
				int textureX = textureXStart + x * levelSize;
				int textureY = textureYStart + y * levelSize;
				final int toIndex = (x + y * tileSize) * 3;
				final int fromIndex = (textureX + textureY * textureWidth) * 3;


				try {
					data.put(toIndex, data1.get(fromIndex));
					data.put(toIndex + 1, data1.get(fromIndex + 1));
					data.put(toIndex + 2, data1.get(fromIndex + 2));
				} catch (IndexOutOfBoundsException e) {
					log.error("Terrain Texture source fail!", e);
					log.error("clipmapLevel {} textureStart ({}, {}) levelSize {}",
							new Integer[]{clipmapLevel, textureXStart, textureYStart, levelSize});
					log.error("fromIndex {} ", fromIndex);
				}
			}
		}
		return data;
	}


}
