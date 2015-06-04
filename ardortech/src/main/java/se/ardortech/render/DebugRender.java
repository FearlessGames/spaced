package se.ardortech.render;

import com.ardor3d.renderer.Renderer;
import com.ardor3d.scenegraph.Spatial;
import com.ardor3d.util.geom.Debugger;
import com.google.inject.Singleton;

@Singleton
public class DebugRender {
	private boolean showBounds;
	private boolean showNormals;

	public void toggleBounds() {
		showBounds = !showBounds;
	}

	public void toggleNormals() {
		showNormals = !showNormals;
	}

	public void render(Spatial spatial, final Renderer renderer) {
		if (showBounds) {
			Debugger.drawBounds(spatial, renderer, true);
		}

		if (showNormals) {
			Debugger.drawNormals(spatial, renderer);
			Debugger.drawTangents(spatial, renderer);
		}
	}
}