package se.ardortech;

import com.ardor3d.image.Texture;
import com.ardor3d.scenegraph.Spatial;
import com.ardor3d.util.TextureKey;

import java.util.concurrent.Future;

public interface TextureManager {
	Future<Texture> applyTexture(String textureFile, Spatial spatial);

	Future<Texture> loadTexture(String textureFile, TextureLoadCallback callback);
	Future<Texture> loadTexture(String textureFile, TextureLoadCallback callback, boolean flip);

	public void onAfterTextureLoad(TextureKey source, Texture texture);
}
