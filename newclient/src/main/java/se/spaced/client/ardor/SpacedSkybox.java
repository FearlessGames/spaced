package se.spaced.client.ardor;

import com.ardor3d.image.Texture;
import com.ardor3d.image.TextureStoreFormat;
import com.ardor3d.scenegraph.extension.Skybox;
import com.ardor3d.util.TextureManager;

public class SpacedSkybox {

	private static final float SKYBOX_OFFSET_FROM_PLANE = 1.0f;

	public static Skybox create(float x, float y, float z) {
		// Must be further away then clipping plane
		Skybox skybox = new Skybox("skybox",
				x + SKYBOX_OFFSET_FROM_PLANE,
				y + SKYBOX_OFFSET_FROM_PLANE,
				z + SKYBOX_OFFSET_FROM_PLANE);

		skybox.setTexture(Skybox.Face.North, loadTexture("skybox/Skybox6sN.png"));
		skybox.setTexture(Skybox.Face.West, loadTexture("skybox/Skybox6sW.png"));
		skybox.setTexture(Skybox.Face.South, loadTexture("skybox/Skybox6sS.png"));
		skybox.setTexture(Skybox.Face.East, loadTexture("skybox/Skybox6sE.png"));
		skybox.setTexture(Skybox.Face.Up, loadTexture("skybox/Skybox6sU.png"));
		skybox.setTexture(Skybox.Face.Down, loadTexture("skybox/Skybox6sD.png"));

		return skybox;
	}

	private static Texture loadTexture(String path) {
		return TextureManager.load(path,
				Texture.MinificationFilter.Trilinear,
				TextureStoreFormat.GuessNoCompressedFormat,
				true);
	}
}