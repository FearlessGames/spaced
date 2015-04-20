package se.spaced.spacedit.launcher.modules;

import com.ardor3d.extension.model.collada.jdom.ColladaImporter;
import com.ardor3d.framework.CanvasRenderer;
import com.ardor3d.framework.Scene;
import com.ardor3d.framework.Updater;
import com.ardor3d.framework.lwjgl.LwjglAwtCanvas;
import com.ardor3d.input.MouseManager;
import com.ardor3d.util.Timer;
import se.spaced.shared.util.AbstractMockModule;

import java.awt.Component;

public class MockModule extends AbstractMockModule {
	@Override
	protected void configure() {
		bindMock(Timer.class);
		bindMock(Updater.class);
		bindMock(CanvasRenderer.class);
		bindMock(Scene.class);
		bindMock(MouseManager.class);
		bindMock(ColladaImporter.class);
		bindMock(Component.class);
		bindMock(LwjglAwtCanvas.class);
	}

}
