package se.spaced.client.ardor.entity;

import com.ardor3d.image.Image;
import com.ardor3d.image.Texture;
import com.ardor3d.renderer.Camera;
import com.ardor3d.renderer.queue.RenderBucketType;
import com.ardor3d.renderer.state.BlendState;
import com.ardor3d.renderer.state.FogState;
import com.ardor3d.renderer.state.RenderState;
import com.ardor3d.renderer.state.TextureState;
import com.ardor3d.renderer.state.ZBufferState;
import com.ardor3d.scenegraph.Mesh;
import com.ardor3d.scenegraph.Spatial;
import com.ardor3d.scenegraph.hint.DataMode;
import com.ardor3d.scenegraph.hint.LightCombineMode;
import com.ardor3d.scenegraph.hint.TextureCombineMode;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import se.ardortech.TextureLoadCallback;
import se.ardortech.TextureManager;
import se.ardortech.meshgenerator.MeshFactory;
import se.ardortech.meshgenerator.shapes.PanelMeshDataGenerator;

import java.awt.Insets;
import java.util.concurrent.ExecutionException;

@Singleton
public class EntityIndicatorFactory {
	private final TextureManager textureManager;
	private final MeshFactory meshFactory = new MeshFactory();
	private final Camera camera;

	@Inject
	public EntityIndicatorFactory(TextureManager textureManager, Camera camera) {
		this.textureManager = textureManager;
		this.camera = camera;
	}

	public EntityIndicator create(String texturePath) {
		Texture texture = null;
		try {
			texture = textureManager.loadTexture(texturePath, new TextureLoadCallback() {
				@Override
				public void loadedRequestedTexture(final Texture texture) {
				}
			}).get();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}

		final Image image = texture.getImage();
		final int height = image.getHeight();
		final int width = image.getWidth();
		final int halfHeight = height / 2;
		final int halfWidth = width / 2;

		PanelMeshDataGenerator generator = new PanelMeshDataGenerator(new Insets(32, 32, 32, 32), null, 0, 0, width,
				height);

		final Mesh mesh = meshFactory.createMesh(generator);
		applyStates(mesh, texture);

		return new EntityIndicator(mesh, camera, generator);
	}

	private void applyStates(final Spatial spatial, Texture texture) {
		BlendState bs = new BlendState();
		bs.setBlendEnabled(true);
		bs.setSourceFunction(BlendState.SourceFunction.SourceAlpha);
		bs.setDestinationFunction(BlendState.DestinationFunction.One);
		spatial.setRenderState(bs);
		spatial.getSceneHints().setAllPickingHints(false);

		ZBufferState zb = new ZBufferState();
		zb.setEnabled(false);
		zb.setWritable(false);
		spatial.setRenderState(zb);

		final RenderState fs = new FogState();
		fs.setEnabled(false);
		spatial.setRenderState(fs);

		spatial.getSceneHints().setAllPickingHints(false);
		spatial.getSceneHints().setLightCombineMode(LightCombineMode.Off);
		spatial.getSceneHints().setRenderBucketType(RenderBucketType.Skip);
		spatial.getSceneHints().setTextureCombineMode(TextureCombineMode.Replace);
		spatial.getSceneHints().setDataMode(DataMode.Arrays);

		spatial.getSceneHints().setRenderBucketType(RenderBucketType.Ortho);

		TextureState textureState = new TextureState();
		textureState.setTexture(texture);
		textureState.setEnabled(true);
		spatial.setRenderState(textureState);
	}
}
