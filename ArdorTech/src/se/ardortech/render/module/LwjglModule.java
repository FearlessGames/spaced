package se.ardortech.render.module;

import com.ardor3d.framework.DisplaySettings;
import com.ardor3d.framework.NativeCanvas;
import com.ardor3d.framework.Scene;
import com.ardor3d.framework.lwjgl.LwjglCanvas;
import com.ardor3d.framework.lwjgl.LwjglCanvasRenderer;
import com.ardor3d.input.FocusWrapper;
import com.ardor3d.input.KeyboardWrapper;
import com.ardor3d.input.MouseManager;
import com.ardor3d.input.MouseWrapper;
import com.ardor3d.input.lwjgl.LwjglKeyboardWrapper;
import com.ardor3d.input.lwjgl.LwjglMouseManager;
import com.ardor3d.input.lwjgl.LwjglMouseWrapper;
import com.ardor3d.renderer.Camera;
import com.ardor3d.renderer.TextureRendererFactory;
import com.ardor3d.renderer.lwjgl.LwjglTextureRendererProvider;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.Singleton;

/**
 * Guice configuration module for use with a single native LWJGL window.
 */
public class LwjglModule extends AbstractModule {
	private final DisplaySettings settings;

	public LwjglModule(RendererSettings rendererSettings) {
		this.settings = asDisplaySettings(rendererSettings);
		if (rendererSettings.getWindowMode() == WindowMode.UNDECORATED) {
			System.setProperty("org.lwjgl.opengl.Window.undecorated", "true");
		}

	}

	@Override
	protected void configure() {
		bind(KeyboardWrapper.class).to(LwjglKeyboardWrapper.class).in(Scopes.SINGLETON);
		bind(MouseWrapper.class).to(LwjglMouseWrapper.class).in(Scopes.SINGLETON);
		bind(FocusWrapper.class).to(LwjglCanvas.class).in(Scopes.SINGLETON);
		bind(NativeCanvas.class).to(LwjglCanvas.class).in(Scopes.SINGLETON);
		bind(MouseManager.class).to(LwjglMouseManager.class).in(Scopes.SINGLETON);

		TextureRendererFactory.INSTANCE.setProvider(new LwjglTextureRendererProvider());
	}

	@Provides
	@Singleton
	public LwjglCanvasRenderer getCanvasRendere(Scene scene, Camera camera) {
		LwjglCanvasRenderer canvasRenderer = new LwjglCanvasRenderer(scene);

		// TODO: This is an ugly hack (Inject a camera)
		if (camera.getWidth() != 0) {
			canvasRenderer.setCamera(camera);
		}
		return canvasRenderer;
	}

	@Provides
	@Singleton
	public LwjglCanvas getLwjglCanvas(LwjglCanvasRenderer canvasRenderer) {
		return new LwjglCanvas(settings, canvasRenderer);
	}

	private DisplaySettings asDisplaySettings(RendererSettings rendererSettings) {
		return new DisplaySettings(
				rendererSettings.getWidth(),
				rendererSettings.getHeight(),
				rendererSettings.getColorDepth(),
				rendererSettings.getFrequency(),
				rendererSettings.getAlphaBits(),
				rendererSettings.getDepthBits(),
				rendererSettings.getStencilBits(),
				rendererSettings.getSamples(),
				rendererSettings.isFullScreen(),
				rendererSettings.isStereo());
	}
}