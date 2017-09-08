package se.spaced.client.ardor.terrain;

import com.ardor3d.extension.terrain.client.*;
import com.ardor3d.extension.terrain.util.BresenhamYUpGridTracer;
import com.ardor3d.extension.terrain.util.TerrainGridCachePanel;
import com.ardor3d.extension.terrain.util.TextureGridCachePanel;
import com.ardor3d.math.Vector3;
import com.ardor3d.renderer.Camera;
import com.google.common.collect.Lists;

import javax.swing.*;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

public class TerrainWithLightsOnGroundBuilder {
	/**
	 * The Constant logger.
	 */

	public static final int MAX_PICK_CHECKS = 500;

	private final TerrainDataProvider terrainDataProvider;
	private final Camera camera;

	private int clipmapTerrainCount = 20;
	private int clipmapTerrainSize = 127; // pow2 - 1
	private int clipmapTextureCount = 20;
	private int clipmapTextureSize = 128;

	private boolean showDebugPanels = false;
	private final ThreadPoolExecutor executor;

	public TerrainWithLightsOnGroundBuilder(final TerrainDataProvider terrainDataProvider, final Camera camera, ThreadPoolExecutor executor) {
		this.terrainDataProvider = terrainDataProvider;
		this.camera = camera;
		this.executor = executor;
	}

	private final List<TextureSource> extraTextureSources = Lists.newArrayList();

	public void addTextureConnection(final TextureSource textureSource) {
		extraTextureSources.add(textureSource);
	}

	public TerrainWithLightsOnGround build() throws Exception {
		final Map<Integer, String> availableMaps = terrainDataProvider.getAvailableMaps();
		final int mapId = availableMaps.keySet().iterator().next();

		final TerrainSource terrainSource = terrainDataProvider.getTerrainSource(mapId);
		final TerrainWithLightsOnGround terrain = buildTerrainSystem(terrainSource);

		final TextureSource textureSource = terrainDataProvider.getTextureSource(mapId);
		if (textureSource != null) {
			terrain.addTextureClipmap(buildTextureSystem(textureSource));

			for (final TextureSource extraSource : extraTextureSources) {
				terrain.addTextureClipmap(buildTextureSystem(extraSource));
			}
		}

		return terrain;
	}

	private TerrainWithLightsOnGround buildTerrainSystem(final TerrainSource terrainSource) throws Exception {
		final TerrainConfiguration terrainConfiguration = terrainSource.getConfiguration();

		final int clipmapLevels = terrainConfiguration.getTotalNrClipmapLevels();
		final int clipLevelCount = Math.min(clipmapLevels, clipmapTerrainCount);

		final int tileSize = terrainConfiguration.getCacheGridSize();

		int cacheSize = (clipmapTerrainSize + 1) / tileSize + 4;
		cacheSize += (cacheSize & 1) ^ 1;

		final List<TerrainCache> cacheList = Lists.newArrayList();
		TerrainCache parentCache = null;

		final int baseLevel = Math.max(clipmapLevels - clipLevelCount, 0);
		int level = clipLevelCount - 1;

		for (int i = baseLevel; i < clipmapLevels; i++) {
			final TerrainCache gridCache = new TerrainGridCache(parentCache, cacheSize, terrainSource, tileSize,
					clipmapTerrainSize, terrainConfiguration, level--, i, executor);

			parentCache = gridCache;
			cacheList.add(gridCache);
		}
		Collections.reverse(cacheList);

		final TerrainWithLightsOnGround terrain = new TerrainWithLightsOnGround(camera,
				cacheList,
				clipmapTerrainSize,
				terrainConfiguration);

		terrain.makePickable(BresenhamYUpGridTracer.class, MAX_PICK_CHECKS, new Vector3(1, 0, 1));

		if (showDebugPanels) {
			final TerrainGridCachePanel panel = new TerrainGridCachePanel(cacheList, cacheSize);
			final JFrame frame = new JFrame("Terrain Cache Debug");
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.getContentPane().add(panel);
			frame.setBounds(10, 10, panel.getSize().width, panel.getSize().height);
			frame.setVisible(true);
		}

		return terrain;
	}

	private TextureClipmap buildTextureSystem(final TextureSource textureSource) throws Exception {
		final TextureConfiguration textureConfiguration = textureSource.getConfiguration();

		final int clipmapLevels = textureConfiguration.getTotalNrClipmapLevels();
		final int textureClipLevelCount = Math.min(clipmapLevels, clipmapTextureCount);

		final int tileSize = textureConfiguration.getCacheGridSize();

		int cacheSize = (clipmapTextureSize + 1) / tileSize + 4;
		cacheSize += (cacheSize & 1) ^ 1;

		final List<TextureCache> cacheList = Lists.newArrayList();
		TextureCache parentCache = null;
		final int baseLevel = Math.max(clipmapLevels - textureClipLevelCount, 0);
		int level = textureClipLevelCount - 1;

		for (int i = baseLevel; i < clipmapLevels; i++) {
			final TextureCache gridCache = new TextureGridCache(parentCache, cacheSize, textureSource, tileSize,
					clipmapTextureSize, textureConfiguration, level--, i, executor);

			parentCache = gridCache;
			cacheList.add(gridCache);
		}
		Collections.reverse(cacheList);

		final TextureClipmap textureClipmap = new TextureClipmap(cacheList, clipmapTextureSize, textureConfiguration);

		if (showDebugPanels) {
			final TextureGridCachePanel panel = new TextureGridCachePanel(cacheList, cacheSize);
			final JFrame frame = new JFrame("Texture Cache Debug");
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.getContentPane().add(panel);
			frame.setBounds(10, 120, panel.getSize().width, panel.getSize().height);
			frame.setVisible(true);
		}

		return textureClipmap;
	}

	public TerrainWithLightsOnGroundBuilder setClipmapTerrainCount(final int clipmapTerrainCount) {
		this.clipmapTerrainCount = clipmapTerrainCount;
		return this;
	}

	public TerrainWithLightsOnGroundBuilder setClipmapTerrainSize(final int clipmapTerrainSize) {
		this.clipmapTerrainSize = clipmapTerrainSize;
		return this;
	}

	public TerrainWithLightsOnGroundBuilder setClipmapTextureCount(final int clipmapTextureCount) {
		this.clipmapTextureCount = clipmapTextureCount;
		return this;
	}

	public TerrainWithLightsOnGroundBuilder setClipmapTextureSize(final int clipmapTextureSize) {
		this.clipmapTextureSize = clipmapTextureSize;
		return this;
	}

	public TerrainWithLightsOnGroundBuilder setShowDebugPanels(final boolean showDebugPanels) {
		this.showDebugPanels = showDebugPanels;
		return this;
	}
}
