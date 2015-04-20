package se.spaced.client.environment.components;

import com.ardor3d.renderer.state.FogState;
import com.ardor3d.scenegraph.Node;
import com.google.inject.Singleton;
import se.spaced.client.environment.settings.FogSetting;

@Singleton
public class Fog {
	private FogState fs;
	private FogSetting currentFogSetting; //needed for future interpolation
	private int interpolatedTimeSpan = 10;

	public void setCurrentSettings(FogSetting fogSetting) {
		this.currentFogSetting = fogSetting;
		applyFogSettingsOnState(fogSetting);
	}

	private void applyFogSettingsOnState(FogSetting fogSetting) {
		fs.setStart(fogSetting.getStart());
		fs.setEnd(fogSetting.getEnd());
		fs.setColor(fogSetting.getColor());
		fs.setDensityFunction(FogState.DensityFunction.Linear);
		fs.setDensity(fogSetting.getDensity());
		fs.setQuality(FogState.Quality.PerVertex);
	}

	public void init(Node rootNode) {
		rootNode.setRenderState(fs);
	}


	public Fog(FogSetting fogSetting) {
		this.currentFogSetting = fogSetting;
		fs = new FogState();
		applyFogSettingsOnState(fogSetting);
	}
}
