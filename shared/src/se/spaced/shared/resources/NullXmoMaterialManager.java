package se.spaced.shared.resources;

import com.ardor3d.scenegraph.Spatial;

public class NullXmoMaterialManager implements XmoMaterialManager {
	public static final NullXmoMaterialManager INSTANCE = new NullXmoMaterialManager();

	@Override
	public void applyMaterial(String materialFile, Spatial spatial) {
	}

	@Override
	public void invalidateCache(String materialFile) {
	}

	@Override
	public void invalidateEntireCache() {
	}
}
