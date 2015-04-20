package se.spaced.spacedit.launcher.modules;

import com.ardor3d.extension.model.collada.jdom.ColladaImporter;
import com.ardor3d.framework.Canvas;
import com.ardor3d.framework.CanvasRenderer;
import com.ardor3d.framework.DisplaySettings;
import com.ardor3d.framework.FrameHandler;
import com.ardor3d.framework.Scene;
import com.ardor3d.framework.Updater;
import com.ardor3d.framework.lwjgl.LwjglAwtCanvas;
import com.ardor3d.framework.lwjgl.LwjglCanvasRenderer;
import com.ardor3d.input.MouseManager;
import com.ardor3d.input.PhysicalLayer;
import com.ardor3d.input.awt.AwtFocusWrapper;
import com.ardor3d.input.awt.AwtKeyboardWrapper;
import com.ardor3d.input.awt.AwtMouseManager;
import com.ardor3d.input.awt.AwtMouseWrapper;
import com.ardor3d.input.logical.LogicalLayer;
import com.ardor3d.scenegraph.Node;
import com.ardor3d.util.Timer;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import se.spaced.spacedit.ardor.DefaultScene;
import se.spaced.spacedit.ardor.DefaultUpdater;
import se.spaced.spacedit.ardor.Sun;

import java.awt.Component;

public class ArdorAwtModule extends AbstractModule {
	@Override
	protected void configure() {
		bind(Timer.class).in(Scopes.SINGLETON);
		bind(Updater.class).to(DefaultUpdater.class).in(Scopes.SINGLETON);
		bind(Canvas.class).to(LwjglAwtCanvas.class).in(Scopes.SINGLETON);
		bind(CanvasRenderer.class).to(LwjglCanvasRenderer.class).in(Scopes.SINGLETON);
		bind(Scene.class).to(DefaultScene.class).in(Scopes.SINGLETON);
		bind(MouseManager.class).to(AwtMouseManager.class).in(Scopes.SINGLETON);
		bind(ColladaImporter.class).in(Scopes.SINGLETON);
		bind(Component.class).to(LwjglAwtCanvas.class).in(Scopes.SINGLETON);

	}

	@Provides
	@Singleton
	public LwjglAwtCanvas getCanvas(DisplaySettings displaySettings, LwjglCanvasRenderer canvasRenderer) throws Exception {
		LwjglAwtCanvas canvas = new LwjglAwtCanvas(displaySettings, canvasRenderer);
		return canvas;
	}

	@Provides
	@Singleton
	public DisplaySettings getDisplaySettings() {
		return new DisplaySettings(1, 1, 0, 0, 0, 16, 0, 0, false, false);
	}

	@Provides
	@Singleton
	public PhysicalLayer getPhysicalLayer(LwjglAwtCanvas awtCanvas, MouseManager mouseManager) {
		final AwtMouseWrapper mouseWrapper = new AwtMouseWrapper(awtCanvas, mouseManager);
		final AwtKeyboardWrapper keyboardWrapper = new AwtKeyboardWrapper(awtCanvas);
		final AwtFocusWrapper focusWrapper = new AwtFocusWrapper(awtCanvas);
		return new PhysicalLayer(keyboardWrapper, mouseWrapper, focusWrapper);
	}

	@Provides
	@Singleton
	@Named("root")
	public Node getRootNode() {
		return new Node("root");
	}

	@Provides
	@Singleton
	public DefaultScene getScene(@Named("root") Node rootNode) {
		return new DefaultScene(rootNode, new Sun());
	}

	@Provides
	@Singleton
	public FrameHandler getFrameHandler(Timer timer, Updater updater, Canvas canvas) {
		FrameHandler frameHandler = new FrameHandler(timer);
		updater.init();	// TODO: Are we really suppose to call this?
		frameHandler.addUpdater(updater);
		frameHandler.addCanvas(canvas);
		return frameHandler;
	}

	@Provides
	@Singleton
	public LogicalLayer getLogicalLayer(Canvas canvas, PhysicalLayer physicalLayer) {
		LogicalLayer logicalLayer = new LogicalLayer();
		logicalLayer.registerInput(canvas, physicalLayer);
		return logicalLayer;
	}
}
