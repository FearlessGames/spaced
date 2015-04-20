package se.ardortech.render;

import com.ardor3d.image.util.ScreenShotImageExporter;
import com.ardor3d.renderer.Renderer;
import com.ardor3d.util.screen.ScreenExporter;
import com.google.inject.Singleton;

@Singleton
public class ScreenshotRender {
	private final ScreenShotImageExporter screenShotExp = new ScreenShotImageExporter();
	private boolean doShot;

	public void takeScreenShot() {
		doShot = true;
	}

	public void render(final Renderer renderer) {
		if (doShot) {
			ScreenExporter.exportCurrentScreen(renderer, screenShotExp);
			doShot = false;
		}
	}
}