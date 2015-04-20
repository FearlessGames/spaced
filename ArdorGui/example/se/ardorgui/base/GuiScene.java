package se.ardorgui.base;

import com.ardor3d.framework.NativeCanvas;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import se.ardorgui.components.GuiContainer;
import se.ardortech.example.BaseExampleScene;
import se.ardortech.render.DebugRender;
import se.ardortech.render.ScreenshotRender;

@Singleton
public class GuiScene extends BaseExampleScene {
	protected GuiContainer gui;
	protected final NativeCanvas canvas;

	@Inject
	public GuiScene(
			DebugRender debugRender,
			ScreenshotRender screenshotRender,
			NativeCanvas canvas) {
		super(debugRender, screenshotRender);
		this.canvas = canvas;
	}

	public void releaseGui() {
		if (gui != null) {
			gui.releaseResources();
			gui = null;
		}
	}

	@Override
	protected void setUp() {
		releaseGui();
		root.detachAllChildren();
		setUpGui();
		root.updateGeometricState(0.0f, true);
	}

	public void toggleVisible() {
		if (gui.isLocalVisible()) {
			gui.hide();
		} else {
			gui.show();
		}
	}

	public void toggleEnabled() {
		if (gui.isLocalEnabled()) {
			gui.disable();
		} else {
			gui.enable();
		}
	}

	protected void setUpGui() {}
}