package se.ardortech;

import com.ardor3d.image.Texture;
import com.ardor3d.image.TextureStoreFormat;
import com.ardor3d.renderer.state.TextureState;
import com.ardor3d.scenegraph.Spatial;
import com.ardor3d.util.TextureKey;
import com.ardor3d.util.resource.ResourceSource;
import se.fearless.common.io.StreamLocator;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class TextureManagerImpl implements TextureManager {
	private final StreamLocator streamLocator;
	private final ExecutorService executorService;

	public TextureManagerImpl(StreamLocator streamLocator, ExecutorService executorService) {
		this.streamLocator = streamLocator;
		this.executorService = executorService;
	}

	@Override
	public Future<Texture> applyTexture(final String textureFile, final Spatial spatial) {
		TextureState txs = new TextureState();
		txs.setEnabled(false);
		spatial.setRenderState(txs);
		return executorService.submit(new TextureLoader(textureFile, new TextureApplyer(txs), true));
	}

	@Override
	public Future<Texture> loadTexture(String textureFile, TextureLoadCallback callback) {
		return loadTexture(textureFile, callback, true);
	}

	@Override
	public Future<Texture> loadTexture(String textureFile, TextureLoadCallback callback, boolean flip) {
		return executorService.submit(new TextureLoader(textureFile, callback, flip));
	}

	private class TextureLoader implements Callable<Texture> {
		private final String name;
		private final TextureLoadCallback callback;
		private final boolean flipped;

		private TextureLoader(String name, TextureLoadCallback callback, boolean flip) {
			this.name = name;
			this.callback = callback;
			this.flipped = flip;
		}

		@Override
		public Texture call() throws Exception {
			String type = name.substring(name.lastIndexOf('.'));
			ResourceSource source = new SpacedResource(name, streamLocator.getInputSupplier(name), type);
			final TextureKey textureKey = TextureKey.getKey(source, flipped, TextureStoreFormat.GuessNoCompressedFormat, Texture.MinificationFilter.Trilinear);
			Texture texture = com.ardor3d.util.TextureManager.loadFromKey(textureKey, null, null);
			onAfterTextureLoad(textureKey, texture);
			if (callback != null) {
				callback.loadedRequestedTexture(texture);
			}
			return texture;
		}

	}

	@Override
	public void onAfterTextureLoad(TextureKey source, Texture texture) {
	}

	private static class TextureApplyer implements TextureLoadCallback {
		private final TextureState txs;

		private  TextureApplyer(TextureState txs) {
			this.txs = txs;
		}

		@Override
		public void loadedRequestedTexture(Texture texture) {
			txs.setTexture(texture);
			txs.setEnabled(true);
			txs.setNeedsRefresh(true);
		}
	}
}
