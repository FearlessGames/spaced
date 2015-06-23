package se.spaced.client.environment.components;

import com.ardor3d.image.Texture;
import com.ardor3d.renderer.Camera;
import com.ardor3d.renderer.state.ZBufferState;
import com.ardor3d.scenegraph.Node;
import com.ardor3d.scenegraph.extension.Skybox;
import se.ardortech.TextureLoadCallback;
import se.ardortech.TextureManager;

import javax.inject.Inject;

public class SkyBox implements Sky {

	private Skybox skybox;
	private final TextureManager textureManager;
	public static final String PATH_PREFIX = "/textures/skybox/";

	@Inject
	public SkyBox(TextureManager textureManager) {
		this.textureManager = textureManager;
		skybox = new Skybox("skybox", 8000, 8000, 8000);
		ZBufferState zBufferState = new ZBufferState();
		zBufferState.setEnabled(true);
		skybox.setRenderState(zBufferState);
	}

	@Override
	public void update(Camera cam) {
		skybox.setTranslation(cam.getLocation());
	}

	@Override
	public void init(Node node) {
		node.attachChild(skybox);
		loadTexture(PATH_PREFIX + "box1_top3.png", Skybox.Face.Up);
		loadTexture(PATH_PREFIX + "box1_bottom4.png", Skybox.Face.Down);
		loadTexture(PATH_PREFIX + "box1_left2.png", Skybox.Face.West);
		loadTexture(PATH_PREFIX + "box1_right1.png", Skybox.Face.East);
		loadTexture(PATH_PREFIX + "box1_back6.png", Skybox.Face.South);
		loadTexture(PATH_PREFIX + "box1_front5.png", Skybox.Face.North);
	}

	private void loadTexture(String path, final Skybox.Face face) {
		textureManager.loadTexture(path, new TextureLoadCallback() {
			@Override
			public void loadedRequestedTexture(Texture texture) {
				skybox.setTexture(face, texture);
			}
		});
	}

	@Override
	public Node getSkyNode() {
		return skybox;
	}
}
