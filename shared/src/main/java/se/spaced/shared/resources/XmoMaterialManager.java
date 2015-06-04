package se.spaced.shared.resources;

import com.ardor3d.scenegraph.Spatial;

public interface XmoMaterialManager {
	void applyMaterial(String materialFile, Spatial spatial);
	void invalidateCache(String materialFile);
	void invalidateEntireCache();
}
