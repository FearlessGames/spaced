package se.ardorgui.lua.base;

import com.ardor3d.framework.DisplaySettings;
import com.ardor3d.framework.FrameHandler;
import com.ardor3d.framework.NativeCanvas;
import com.ardor3d.framework.lwjgl.LwjglCanvas;
import com.ardor3d.input.PhysicalLayer;
import com.ardor3d.input.lwjgl.LwjglControllerWrapper;
import com.ardor3d.input.lwjgl.LwjglKeyboardWrapper;
import com.ardor3d.input.lwjgl.LwjglMouseWrapper;
import com.ardor3d.math.Vector3;
import com.ardor3d.math.type.ReadOnlyVector3;
import com.ardor3d.renderer.Camera;
import com.ardor3d.util.Timer;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import se.ardortech.TextureManager;
import se.ardortech.TextureManagerImpl;
import se.ardortech.render.module.RendererSettings;
import se.fearlessgames.common.io.ClasspathStreamLocator;
import se.fearlessgames.common.io.FileStreamLocator;
import se.fearlessgames.common.io.MultiStreamLocator;
import se.fearlessgames.common.io.StreamLocator;
import se.spaced.shared.concurrency.SimpleThreadFactory;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GuiTestArdorGuiModule extends AbstractModule {

	public GuiTestArdorGuiModule() {
	}

	@Override
	protected void configure() {
	}


	@Provides
	public DisplaySettings getDisplaySettings() {
		return new DisplaySettings(1024, 768, 24, 4);
	}

	@Provides
	RendererSettings getRendererSettings() {
		return new RendererSettings();
	}

	@Provides
	@Singleton
	public Camera getCamera(RendererSettings settings) {
		Camera camera = new Camera(settings.getWidth(), settings.getHeight());
		camera.setFrustumPerspective(settings.getFov(),
				(float) settings.getWidth() / (float) settings.getHeight(),
				1,
				1000);
		camera.setFrustumNear(0.5);
		camera.setFrustumFar(16220);
		camera.setProjectionMode(Camera.ProjectionMode.Perspective);

		final ReadOnlyVector3 loc = new Vector3(0.0f, 0.0f, 10.0f);
		final ReadOnlyVector3 left = new Vector3(-1.0f, 0.0f, 0.0f);
		final ReadOnlyVector3 up = new Vector3(0.0f, 1.0f, 0.0f);
		final ReadOnlyVector3 dir = new Vector3(0.0f, 0f, -1.0f);
		/** Move our camera to a correct place and orientation. */
		camera.setFrame(loc, left, up, dir);

		return camera;
	}

	@Provides
	@Named("rttCamera")
	public Camera getRttCamera(RendererSettings settings) {
		Camera camera = new Camera(settings.getWidth(), settings.getHeight());
		camera.setFrustumPerspective((10),
				(float) (512) / (float) (512),
				1,
				1000);
		camera.setFrustumNear(0.5);
		camera.setFrustumFar(16220);
		camera.setProjectionMode(Camera.ProjectionMode.Perspective);

		final ReadOnlyVector3 loc = new Vector3(0.0f, 1.1f, 8.5f);
		final ReadOnlyVector3 left = new Vector3(-1.0f, 0.0f, 0.0f);
		final ReadOnlyVector3 up = new Vector3(0.0f, 1.0f, 0.0f);
		final ReadOnlyVector3 dir = new Vector3(0.0f, 0.0f, -1.0f);
		/** Move our camera to a correct place and orientation. */
		camera.setFrame(loc, left, up, dir);

		return camera;
	}

	@Provides
	@Singleton
	public FrameHandler getFrameHandler(Timer timer) {
		return new FrameHandler(timer);
	}

	@Provides
	@Singleton
	public TextureManager getTextureManager(StreamLocator streamLocator) {
		ExecutorService executorService = Executors.newFixedThreadPool(2,
				SimpleThreadFactory.withPrefix("textureLoader-"));
		return new TextureManagerImpl(streamLocator, executorService);
	}

	@Provides
	@Singleton
	public StreamLocator getStreamLocator() {
		return new MultiStreamLocator(
				new FileStreamLocator(new File(System.getProperty("user.dir") + "/resources")),
				new FileStreamLocator(new File("resources/textures")),
				new ClasspathStreamLocator()
		);
	}

	@Provides
	@Singleton
	public PhysicalLayer getPhysicalLayer(
			NativeCanvas nativeCanvas,
			LwjglKeyboardWrapper keyboardWrapper,
			LwjglMouseWrapper mouseWrapper,
			LwjglControllerWrapper controllerWrapper) {
		return new PhysicalLayer(keyboardWrapper, mouseWrapper, controllerWrapper, (LwjglCanvas) nativeCanvas);
	}

}
