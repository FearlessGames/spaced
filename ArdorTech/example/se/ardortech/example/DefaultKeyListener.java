package se.ardortech.example;

import com.ardor3d.input.Key;
import com.google.inject.Inject;
import se.ardortech.Main;
import se.ardortech.input.KeyListener;
import se.ardortech.render.DebugRender;
import se.ardortech.render.ScreenshotRender;

public class DefaultKeyListener implements KeyListener {
	private final DebugRender debugRender;
	private final ScreenshotRender screenshotRender;
	private final BaseExampleScene scene;
    private final Main main;

	@Inject
	public DefaultKeyListener(DebugRender debugRender, ScreenshotRender screenshotRender, BaseExampleScene scene, Main main) {
		this.main = main;
		this.debugRender = debugRender;
		this.screenshotRender = screenshotRender;
		this.scene = scene;
	}

	@Override
	public boolean onKey(char character, Key keyCode, boolean pressed) {
		if (pressed) {
			if (keyCode == Key.ESCAPE) {
				main.exit();
			} else if (keyCode == Key.B) {
				debugRender.toggleBounds();
			} else if (keyCode == Key.N) {
				debugRender.toggleNormals();
			} else if (keyCode == Key.F12) {
				screenshotRender.takeScreenShot();
			} else if (keyCode == Key.L) {
				scene.toggleLight();
			} else if (keyCode == Key.W) {
				scene.toggleWireframe();
			}
		}
		return false;
	}
}
