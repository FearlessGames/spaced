package se.spaced.client.launcher.modules;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.Singleton;
import se.fearlessgames.common.io.StreamLocator;
import se.spaced.client.model.Prop;
import se.spaced.client.physics.PhysicsWorld;
import se.spaced.client.resources.zone.PropXmoEntityCreator;
import se.spaced.client.resources.zone.RootZoneService;
import se.spaced.client.resources.zone.RootZoneServiceImpl;
import se.spaced.client.resources.zone.ScenegraphService;
import se.spaced.client.resources.zone.ScenegraphServiceImpl;
import se.spaced.client.resources.zone.ScenegraphZoneActivationListener;
import se.spaced.client.resources.zone.ZoneActivationListener;
import se.spaced.client.resources.zone.ZoneActivationService;
import se.spaced.client.resources.zone.ZoneActivationServiceImpl;
import se.spaced.client.resources.zone.ZoneChangedListener;
import se.spaced.client.resources.zone.ZoneChangedListenerImpl;
import se.spaced.client.resources.zone.ZoneDebugShapeService;
import se.spaced.client.resources.zone.ZoneDebugShapeServiceImpl;
import se.spaced.client.resources.zone.ZoneValidator;
import se.spaced.client.resources.zone.ZoneValidatorImpl;
import se.spaced.client.resources.zone.ZoneXmlFileHandler;
import se.spaced.client.resources.zone.ZoneXmlReader;
import se.spaced.client.resources.zone.ZoneXmlWriter;
import se.spaced.shared.model.xmo.XmoEntityFactory;
import se.spaced.shared.util.ListenerDispatcher;
import se.spaced.shared.util.MultiThreadedQueueRunner;
import se.spaced.shared.util.QueueRunner;
import se.spaced.shared.world.terrain.HeightMap;
import se.spaced.shared.world.terrain.HeightmapLoader;
import se.spaced.shared.world.terrain.RawHeightMapLoader;

import java.io.IOException;

public class ZoneModule extends AbstractModule {
	@Override
	protected void configure() {
		bind(ZoneActivationService.class).to(ZoneActivationServiceImpl.class).in(Scopes.SINGLETON);
		bind(ZoneValidator.class).to(ZoneValidatorImpl.class).in(Scopes.SINGLETON);
		bind(ZoneXmlReader.class).to(ZoneXmlFileHandler.class).in(Scopes.SINGLETON);
		bind(ZoneXmlWriter.class).to(ZoneXmlFileHandler.class).in(Scopes.SINGLETON);

		bind(ScenegraphService.class).to(ScenegraphServiceImpl.class).in(Scopes.SINGLETON);

		bind(ZoneChangedListener.class).to(ZoneChangedListenerImpl.class).in(Scopes.SINGLETON);
		bind(ZoneDebugShapeService.class).to(ZoneDebugShapeServiceImpl.class).in(Scopes.SINGLETON);
		bind(RootZoneService.class).to(RootZoneServiceImpl.class).in(Scopes.SINGLETON);
	}

	@Provides
	@Singleton
	public QueueRunner<Prop, Void> getXmoEntityCreator(
			ScenegraphService scenegraphService, XmoEntityFactory xmoEntityFactory, PhysicsWorld physicsWorld) {
		return new MultiThreadedQueueRunner<Prop, Void>(5,
				new PropXmoEntityCreator(scenegraphService, xmoEntityFactory, physicsWorld)
		);
	}

	@Provides
	public ZoneActivationListener getZoneActivationListener(
			ListenerDispatcher<ZoneActivationListener> dispatcher,
			ScenegraphZoneActivationListener scenegraphZoneActivationListener) {
		dispatcher.addListener(scenegraphZoneActivationListener);
		return dispatcher.trigger();
	}

	@Provides
	@Singleton
	public HeightMap getHeightMap(StreamLocator streamLocator) throws IOException {
		HeightmapLoader loader = new RawHeightMapLoader(4096f,
				476f,
				streamLocator.getInputSupplier("/terrains/landsend/terrain/heightmap16bit.raw"));
		return loader.loadHeightMap();
	}

}
