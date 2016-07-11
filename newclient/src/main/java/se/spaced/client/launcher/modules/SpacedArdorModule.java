package se.spaced.client.launcher.modules;

import com.ardor3d.extension.model.collada.jdom.ColladaImporter;
import com.ardor3d.extension.ui.skin.Skin;
import com.ardor3d.framework.*;
import com.ardor3d.framework.lwjgl.LwjglCanvas;
import com.ardor3d.framework.lwjgl.LwjglCanvasRenderer;
import com.ardor3d.input.FocusWrapper;
import com.ardor3d.input.KeyboardWrapper;
import com.ardor3d.input.MouseWrapper;
import com.ardor3d.input.PhysicalLayer;
import com.ardor3d.input.logical.LogicalLayer;
import com.ardor3d.math.ColorRGBA;
import com.ardor3d.math.Vector3;
import com.ardor3d.math.type.ReadOnlyVector3;
import com.ardor3d.renderer.Camera;
import com.ardor3d.renderer.state.CullState;
import com.ardor3d.renderer.state.RenderState;
import com.ardor3d.renderer.state.WireframeState;
import com.ardor3d.renderer.state.ZBufferState;
import com.ardor3d.scenegraph.Node;
import com.ardor3d.scenegraph.hint.DataMode;
import com.ardor3d.util.Timer;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import se.ardorgui.SpacedSkin;
import se.ardortech.Main;
import se.ardortech.SpacedResourceLocator;
import se.ardortech.TextureManager;
import se.ardortech.pick.Picker;
import se.ardortech.render.module.RendererSettings;
import se.fearless.common.io.IOLocator;
import se.fearless.common.lifetime.ExecutorServiceLifetimeAdapter;
import se.fearless.common.lifetime.LifetimeManager;
import se.fearless.common.time.TimeProvider;
import se.fearless.common.uuid.UUIDFactory;
import se.fearless.common.uuid.UUIDFactoryImpl;
import se.spaced.client.ardor.GameInputListener;
import se.spaced.client.ardor.Spaced;
import se.spaced.client.ardor.SpacedScene;
import se.spaced.client.ardor.SpacedUpdater;
import se.spaced.client.ardor.effect.EffectSystem;
import se.spaced.client.ardor.effect.SimpleEffectSystem;
import se.spaced.client.ardor.entity.AuraVisualiser;
import se.spaced.client.ardor.entity.AuraVisualiserProvider;
import se.spaced.client.ardor.entity.EntityIndicator;
import se.spaced.client.ardor.entity.EntityIndicatorFactory;
import se.spaced.client.ardor.ui.SpacedGui;
import se.spaced.client.ardor.ui.SpacedGuiImpl;
import se.spaced.client.environment.EnvironmentSystem;
import se.spaced.client.environment.ZoneEnvironmentProvider;
import se.spaced.client.environment.components.Fog;
import se.spaced.client.environment.components.Sky;
import se.spaced.client.environment.components.SkyBox;
import se.spaced.client.environment.components.Sun;
import se.spaced.client.environment.settings.FogSetting;
import se.spaced.client.environment.time.GameTimeManager;
import se.spaced.client.model.UserCharacter;
import se.spaced.client.physics.BulletPhysics;
import se.spaced.client.physics.PhysicsWorld;
import se.spaced.client.resources.zone.ZoneActivationService;
import se.spaced.client.sound.music.AmbientSystem;
import se.spaced.client.tools.areacreator.AreaCreatorView;
import se.spaced.client.tools.areacreator.AreaDisplayHandler;
import se.spaced.client.tools.areacreator.XmlAreaFormatter;
import se.spaced.client.tools.areacreator.impl.AreaCreatorViewImpl;
import se.spaced.client.tools.areacreator.impl.AreaDisplayHandlerArdorImpl;
import se.spaced.client.tools.areacreator.impl.XmlAreaFormatterImpl;
import se.spaced.client.tools.spd.SpdView;
import se.spaced.client.tools.spd.SpdViewImpl;
import se.spaced.shared.concurrency.SimpleThreadFactory;
import se.spaced.shared.model.xmo.XmoEntityFactory;
import se.spaced.shared.model.xmo.XmoEntityFactoryImpl;
import se.spaced.shared.resources.XmoMaterialManager;
import se.spaced.shared.resources.XmoMaterialManagerImpl;
import se.spaced.shared.resources.XmoTextureManager;
import se.spaced.shared.tools.ClipBoarder;
import se.spaced.shared.tools.ClipBoarderImpl;
import se.spaced.shared.util.cache.CacheManager;

import java.security.SecureRandom;
import java.util.concurrent.*;

public final class SpacedArdorModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(SpacedGui.class).to(SpacedGuiImpl.class);
		bind(Main.class).to(Spaced.class).in(Scopes.SINGLETON);
		bind(Scene.class).to(SpacedScene.class).in(Scopes.SINGLETON);
		bind(Updater.class).to(SpacedUpdater.class).in(Scopes.SINGLETON);
		bind(EffectSystem.class).to(SimpleEffectSystem.class).in(Scopes.SINGLETON);
		bind(Timer.class).in(Scopes.SINGLETON);
		bind(GameInputListener.class).in(Scopes.SINGLETON);
		bind(Picker.class).in(Scopes.SINGLETON);
		bind(XmoEntityFactory.class).to(XmoEntityFactoryImpl.class).in(Scopes.SINGLETON);
		bind(XmoMaterialManager.class).to(XmoMaterialManagerImpl.class).in(Scopes.SINGLETON);
		bind(CacheManager.class).annotatedWith(Names.named("xmoCachedManager")).to(CacheManager.class).in(Scopes.SINGLETON);
		bind(SpdView.class).to(SpdViewImpl.class).in(Scopes.SINGLETON);
		bind(AreaCreatorView.class).to(AreaCreatorViewImpl.class).in(Scopes.SINGLETON);
		bind(ClipBoarder.class).to(ClipBoarderImpl.class).in(Scopes.SINGLETON);
		bind(AuraVisualiser.class).toProvider(AuraVisualiserProvider.class).in(Scopes.SINGLETON);
		bind(PhysicsWorld.class).to(BulletPhysics.class);
		bind(AreaDisplayHandler.class).to(AreaDisplayHandlerArdorImpl.class).in(Scopes.SINGLETON);
		bind(XmlAreaFormatter.class).to(XmlAreaFormatterImpl.class).in(Scopes.SINGLETON);
		bind(Sky.class).to(SkyBox.class).in(Scopes.SINGLETON);
	}

	@Provides
	@Singleton
	@Named("rootNode")
	public Node getRootNode() {
		final Node root = new Node("rootNode");

		final ZBufferState zbs = new ZBufferState();
		zbs.setEnabled(true);
		zbs.setFunction(ZBufferState.TestFunction.LessThanOrEqualTo);
		root.setRenderState(zbs);

		final CullState cullState = new CullState();
		cullState.setCullFace(CullState.Face.Back);
		root.setRenderState(cullState);

		RenderState wireframeState = new WireframeState();
		wireframeState.setEnabled(false);
		root.setRenderState(wireframeState);

		return root;
	}

	@Provides
	@Singleton
	@Named("entityNode")
	public Node getEntityNode() {
		final Node node = new Node("entityNode");
		node.getSceneHints().setDataMode(DataMode.VBO);
		return node;
	}

	@Provides
	@Singleton
	@Named("propsNode")
	public Node getPropsNode() {
		final Node node = new Node("propsNode");
		node.getSceneHints().setDataMode(DataMode.VBO);
		return node;
	}

	@Provides
	@Singleton
	public UUIDFactory getUUIDFactory(TimeProvider timeProvider) {
		return new UUIDFactoryImpl(timeProvider, new SecureRandom());
	}

	@Provides
	@Singleton
	public Canvas getCanvas(DisplaySettings displaySettings, LwjglCanvasRenderer canvasRenderer) {
		return new LwjglCanvas(displaySettings, canvasRenderer);
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
	public TextureManager getTextureManager(
			IOLocator ioLocator,
			LifetimeManager lifetimeManager,
			@Named("xmoCachedManager") CacheManager xmoCacheManager) {
		ExecutorService executorService = Executors.newFixedThreadPool(2,
				SimpleThreadFactory.withPrefix("textureLoader-"));
		lifetimeManager.addListener(new ExecutorServiceLifetimeAdapter(executorService));
		return new XmoTextureManager(ioLocator, executorService, xmoCacheManager);

	}

	@Provides
	@Singleton
	Fog getFog() {
		final float fogPlane = 1200.f;
		final ColorRGBA fogColor = new ColorRGBA(0.53f, 0.75f, 1.0f, 1.0f);
		float density = 1.0f;
		float start = 400f;

		FogSetting defaultFogSetting = new FogSetting(fogColor, fogPlane, density, start);
		return new Fog(defaultFogSetting);
	}

	@Provides
	@Named("targetIndicator")
	public EntityIndicator getTargetIndicator(EntityIndicatorFactory factory) {
		return factory.create("textures/hud/targetindicator2.png");
	}

	@Provides
	@Named("hoverIndicator")
	public EntityIndicator getHoverIndicator(EntityIndicatorFactory factory) {
		return factory.create("textures/hud/hoverindicator.png");
	}

	@Provides
	@Singleton
	public EnvironmentSystem getEnvironmentSystem(
			Sun sun,
			Fog fog,
			Sky sky,
			GameTimeManager gameTimeManager,
			AmbientSystem soundPlayer,
			TimeProvider timeProvider,
			UserCharacter userCharacter,
			ZoneActivationService zoneActivationService, ZoneEnvironmentProvider zoneEnvironmentProvider) {
		sun.setAxis(new Vector3(1.0, 0.3, 0.0));
		sun.setPosition(new Vector3(0, -1, 0.1));
		EnvironmentSystem es = new EnvironmentSystem(gameTimeManager,
				sun,
				fog,
				timeProvider,
				sky,
				soundPlayer,
				userCharacter,
				zoneActivationService, zoneEnvironmentProvider);
		return es;
	}

	@Provides
	@Singleton
	public Sun getSun(GameTimeManager gameTimeManager) {
		ColorRGBA diffuse = new ColorRGBA(0.92f, 0.82f, 0.68f, 0.84f);
		ColorRGBA ambient = new ColorRGBA(0.47f, 0.42f, 0.99f, 0.78f);
		Vector3 direction = new Vector3(0.f, 1.f, 0.f);
		return new Sun(diffuse, ambient, direction, gameTimeManager);
	}

	@Provides
	@Singleton
	public ColladaImporter getColladaImporter(SpacedResourceLocator spacedResourceLocator) {
		return new ColladaImporter().
				setModelLocator(spacedResourceLocator).
				setLoadTextures(false);
	}


	@Provides
	@Singleton
	public FrameHandler getFrameHandler(Timer timer) {
		return new FrameHandler(timer);
	}

	@Provides
	@Singleton
	public PhysicalLayer getPhysicalLayer(
			KeyboardWrapper keyboardWrapper, MouseWrapper mouseWrapper, FocusWrapper focusWrapper) {
		return new PhysicalLayer(keyboardWrapper, mouseWrapper, focusWrapper);
	}

	@Provides
	@Singleton
	public LogicalLayer getLogicalLayer() {
		return new LogicalLayer();
	}


	@Provides
	@Singleton
	public Skin getSkin(TextureManager textureManager) {
		return new SpacedSkin(textureManager);
	}

	@Provides
	@Singleton
	public ThreadPoolExecutor getThreadPoolExecutor() {
		return new ThreadPoolExecutor(2,
				2,
				0L,
				TimeUnit.MILLISECONDS,
				new LinkedBlockingQueue<Runnable>(),
				new ThreadFactoryBuilder()
						.setThreadFactory(Executors.defaultThreadFactory()).setDaemon(true)
						.setNameFormat("SpacedThreadExecutor-%s").build());

	}


}
