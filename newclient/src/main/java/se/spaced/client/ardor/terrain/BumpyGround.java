package se.spaced.client.ardor.terrain;

import com.ardor3d.extension.terrain.client.Terrain;
import com.ardor3d.extension.terrain.client.TerrainDataProvider;
import com.ardor3d.image.Texture;
import com.ardor3d.image.TextureStoreFormat;
import com.ardor3d.math.Vector4;
import com.ardor3d.renderer.Camera;
import com.ardor3d.renderer.queue.RenderBucketType;
import com.ardor3d.util.TextureManager;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.ardortech.math.SpacedVector3;
import se.fearlessgames.common.io.StreamLocator;
import se.spaced.shared.world.terrain.HeightMap;
import se.spaced.shared.world.terrain.WorldGround;

import java.util.concurrent.ThreadPoolExecutor;

@Singleton
public class BumpyGround {
	private final Logger log = LoggerFactory.getLogger(getClass());

	private final boolean updateTerrain = true;

	// Clipmap parameters and a list of clipmaps that are used.
	private int clipLevelCount = 5;

	private final HeightMap heightMap;
	private StreamLocator streamLocator;
	private final int size;

	private final String texturePath = "terrains/landsend/terrain/txlandsend4096.jpg";
	private final String detailTexturePath = "terrains/portpeak/terrain/detail.jpg";

	private final String vertexShaderPath = "shaders/spacedGeometryClipmapShader.vert";
	private final String fragmentShaderPath = "shaders/spacedGeometryClipmapShader.frag";


	private TerrainWithLightsOnGround terrain;
	private Camera terrainCamera;
	private WorldGround worldGround;
	private final ThreadPoolExecutor executor;
	private TerrainDataProvider terrainDataProvider;


	@Inject
	public BumpyGround(HeightMap heightMap, StreamLocator streamLocator, ThreadPoolExecutor executor) {
		this.heightMap = heightMap;
		this.streamLocator = streamLocator;
		this.executor = executor;
		this.size = heightMap.getSize();
	}

	protected BumpyGround(HeightMap heightMap, int clipLevelCount, ThreadPoolExecutor executor) {
		this.heightMap = heightMap;
		this.executor = executor;
		this.size = heightMap.getSize();
		this.clipLevelCount = clipLevelCount;
	}

	public Terrain init() {
		try {
			worldGround = new WorldGround(heightMap);
			//final TextureState ts = loadTextures();

			terrainCamera = new Camera(1, 1);

			// Create the monster terrain engine
			final Texture colorTexture = TextureManager.load(texturePath,
					Texture.MinificationFilter.Trilinear,
					TextureStoreFormat.GuessNoCompressedFormat,
					false);

			terrainDataProvider = new ArrayTextureTerrainDataProvider(heightMap,
					colorTexture, clipLevelCount);
			terrain = new TerrainWithLightsOnGroundBuilder(terrainDataProvider, terrainCamera, executor).build();


			terrain.setVertexShader(streamLocator.getInputSupplier(vertexShaderPath));
			terrain.setPixelShader(streamLocator.getInputSupplier(fragmentShaderPath));

			// Uncomment for debugging
			//terrain.getTextureClipmap().setShowDebug(true);
			//terrain.reloadShader();

			terrain.setTranslation(2, getSeaLevelAdjustment(), 2);
			terrain.setHeightRange(-190f, 290f);
			terrain.getSceneHints().setRenderBucketType(RenderBucketType.PreBucket);

		} catch (final Exception ex1) {
			log.error("Failed to load terrain", ex1);
		}

		return terrain;
	}

	public void update(Camera camera, Vector4 ambientLight, Vector4 diffuseLight) {
		if (updateTerrain) {
			terrainCamera.set(camera);
			terrain.setAmbientLight(ambientLight);
			terrain.setDiffuseLight(diffuseLight);
		}
	}

	public double getHeight(SpacedVector3 pos) {
		return worldGround.getHeight(pos.getX(), pos.getZ());
	}

	public float getSeaLevelAdjustment() {
		return (float) worldGround.getSeaLevelAdjustment();
	}

	public void invalidateTerrain() {
		HeightmapTerrainSource terrainSource = (HeightmapTerrainSource) terrainDataProvider.getTerrainSource(0);
		terrainSource.invalidate();
		terrain.updateGeometricState(0.0, true);
	}
}
