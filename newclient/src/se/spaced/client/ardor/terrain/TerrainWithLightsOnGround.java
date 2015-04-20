package se.spaced.client.ardor.terrain;

import com.ardor3d.extension.terrain.client.Terrain;
import com.ardor3d.extension.terrain.client.TerrainCache;
import com.ardor3d.extension.terrain.client.TerrainConfiguration;
import com.ardor3d.math.Vector4;
import com.ardor3d.renderer.Camera;
import com.ardor3d.renderer.Renderer;

import java.util.List;

public class TerrainWithLightsOnGround extends Terrain {

	private Vector4 diffuseLight = new Vector4();
	private Vector4 ambientLight = new Vector4();

	public TerrainWithLightsOnGround(
			Camera camera,
			List<TerrainCache> cacheList,
			int clipSideSize,
			TerrainConfiguration terrainConfiguration) {
		super(camera, cacheList, clipSideSize, terrainConfiguration);
	}

	@Override
	public void updateShader(final Renderer r) {
		if (getGeometryClipmapShader() != null) {
			getGeometryClipmapShader().setUniform("diffuse", diffuseLight);
			getGeometryClipmapShader().setUniform("ambient", ambientLight);
		}
		super.updateShader(r);
	}


	public void setDiffuseLight(Vector4 diffuseLight) {
		this.diffuseLight = diffuseLight;
	}

	public void setAmbientLight(Vector4 ambientLight) {
		this.ambientLight = ambientLight;
	}
}
