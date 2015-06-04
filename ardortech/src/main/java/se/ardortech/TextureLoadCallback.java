package se.ardortech;

import com.ardor3d.image.Texture;

public interface TextureLoadCallback {
	void loadedRequestedTexture(Texture texture);
}
