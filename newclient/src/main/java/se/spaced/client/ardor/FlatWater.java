package se.spaced.client.ardor;

import com.ardor3d.image.Texture;
import com.ardor3d.math.ColorRGBA;
import com.ardor3d.math.Plane;
import com.ardor3d.math.Vector3;
import com.ardor3d.renderer.Camera;
import com.ardor3d.scenegraph.Node;
import com.ardor3d.scenegraph.shape.Quad;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import se.ardortech.TextureLoadCallback;
import se.ardortech.TextureManager;
import se.ardortech.water.WaterNode;
import se.fearlessgames.common.io.StreamLocator;
import se.spaced.client.environment.components.Sky;

import java.nio.FloatBuffer;
import java.util.concurrent.atomic.AtomicInteger;

@Singleton
public class FlatWater extends Water {
	private final Quad waterQuad = new Quad("waterQuad", 1, 1);
	private final TextureManager textureManager;

	private Texture normalMapTexture;
	private Texture dudvTexture;
	private Texture fallbackTexture;
	private final Node skyNode;
	private Texture foamTexture;
	private final StreamLocator streamLocator;

	@Inject
	public FlatWater(TextureManager textureManager, Sky sky, StreamLocator streamLocator) {
		this.textureManager = textureManager;
		this.streamLocator = streamLocator;
		skyNode = sky.getSkyNode();
	}

	@Override
	public void init() {


		//waterNode.setTranslation(0,seaLevel,0);

		// Setup textures to use for the water.
		AtomicInteger ready = new AtomicInteger(4);

		textureManager.loadTexture("/textures/water/normalmap3.dds",
				new TextureLoaderCallback(ready, new TextureLoadCallback() {


					@Override
					public void loadedRequestedTexture(Texture texture) {
						normalMapTexture = texture;
					}
				}));
		textureManager.loadTexture("/textures/water/dudvmap.png",
				new TextureLoaderCallback(ready, new TextureLoadCallback() {
					@Override
					public void loadedRequestedTexture(Texture texture) {
						dudvTexture = texture;
					}
				}));
		textureManager.loadTexture("/textures/water/water2.png",
				new TextureLoaderCallback(ready, new TextureLoadCallback() {
					@Override
					public void loadedRequestedTexture(Texture texture) {
						fallbackTexture = texture;
					}
				}));
		textureManager.loadTexture("/textures/water/oceanfoam.png",
				new TextureLoaderCallback(ready, new TextureLoadCallback() {
					@Override
					public void loadedRequestedTexture(Texture texture) {
						foamTexture = texture;
					}
				}));

	}

	@Override
	public void update(Camera cam, double dt) {
		super.update(cam, dt);
		if (waterNode == null) {
			return;
		}

		waterCamera.set(cam);
		final Vector3 transVec = new Vector3(cam.getLocation().getX(),
				waterNode.getWaterHeight(), cam.getLocation().getZ());

		setTextureCoords(0, transVec.getX(), -transVec.getZ(), textureScale);

		// vertex coords
		setVertexCoords(transVec.getX(), transVec.getY(), transVec.getZ());

		waterNode.update(dt);
	}

	private void setVertexCoords(final double x, final double y, final double z) {
		final FloatBuffer vertBuf = waterQuad.getMeshData().getVertexBuffer();
		vertBuf.clear();

		vertBuf.put((float) (x - farPlane)).put((float) y).put((float) (z - farPlane));
		vertBuf.put((float) (x - farPlane)).put((float) y).put((float) (z + farPlane));
		vertBuf.put((float) (x + farPlane)).put((float) y).put((float) (z + farPlane));
		vertBuf.put((float) (x + farPlane)).put((float) y).put((float) (z - farPlane));
	}

	private void setTextureCoords(final int buffer, double x, double y, double textureScale) {
		x *= textureScale * 0.011f;
		y *= textureScale * 0.011f;
		textureScale = farPlane * textureScale;
		FloatBuffer texBuf;
		texBuf = waterQuad.getMeshData().getTextureBuffer(buffer);
		texBuf.clear();
		texBuf.put((float) x).put((float) (textureScale + y));
		texBuf.put((float) x).put((float) y);
		texBuf.put((float) (textureScale + x)).put((float) y);
		texBuf.put((float) (textureScale + x)).put((float) (textureScale + y));
	}

	private class TextureLoaderCallback implements TextureLoadCallback {

		private final AtomicInteger ready;
		private final TextureLoadCallback callback;

		public TextureLoaderCallback(AtomicInteger ready, TextureLoadCallback callback) {
			this.ready = ready;
			this.callback = callback;
		}

		@Override
		public void loadedRequestedTexture(Texture texture) {
			callback.loadedRequestedTexture(texture);
			if (ready.decrementAndGet() == 0) {
				createWaterNode();
				onReady();
			}
		}
	}

	private void createWaterNode() {
		// Create a new WaterNode with refraction enabled.
		waterNode = new WaterNode(waterCamera, 2, true, true, normalMapTexture, dudvTexture, fallbackTexture, skyNode,
				foamTexture, streamLocator);

		waterNode.setWaterColorStart(new ColorRGBA(0.0f, 0.061f, 0.025f, 0.015f));
		waterNode.setWaterColorEnd(new ColorRGBA(-0.04f, -0.12f, -0.26f, 0.246f));

		// setting to default value just to show
		waterNode.setWaterPlane(new Plane(new Vector3(0.0, 1.0, 0.0), 0));

		// Hack the quad normals to point up in the y-axis. Since we are
		// manipulating the vertices as
		// we move this is more convenient than rotating the quad.
		final FloatBuffer normBuf = waterQuad.getMeshData().getNormalBuffer();
		normBuf.clear();
		normBuf.put(0).put(1).put(0);
		normBuf.put(0).put(1).put(0);
		normBuf.put(0).put(1).put(0);
		normBuf.put(0).put(1).put(0);
		waterNode.attachChild(waterQuad);
		waterNode.useFadeToFogColor(false);
	}
}
