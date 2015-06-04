package se.ardortech;

import com.ardor3d.image.Texture;
import com.ardor3d.image.Texture2D;
import com.ardor3d.scenegraph.Spatial;
import com.ardor3d.util.TextureKey;

import java.util.concurrent.Future;

public class NullTextureManager implements TextureManager {
	public static final NullTextureManager INSTANCE = new NullTextureManager();

	private static final Texture TEXTURE = new Texture2D();
	private static final ImmediateFuture<Texture> FUTURE = new ImmediateFuture<Texture>(TEXTURE);

	@Override
	public Future<Texture> applyTexture(String textureFile, Spatial spatial) {
		return FUTURE;
	}

	@Override
	public Future<Texture> loadTexture(String textureFile, TextureLoadCallback callback) {
		callback.loadedRequestedTexture(TEXTURE);
		return FUTURE;
	}

	@Override
	public Future<Texture> loadTexture(String textureFile, TextureLoadCallback callback, boolean flip) {
		callback.loadedRequestedTexture(TEXTURE);
		return FUTURE;
	}

	@Override
	public void onAfterTextureLoad(TextureKey source, Texture texture) {
	}
}
