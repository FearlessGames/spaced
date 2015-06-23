package se.spaced.client.resources.zone;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.ardortech.math.SpacedVector3;
import se.fearlessgames.common.lifetime.ExecutorServiceLifetimeAdapter;
import se.fearlessgames.common.lifetime.LifetimeManager;
import se.spaced.shared.concurrency.SimpleThreadFactory;
import se.spaced.shared.resources.zone.Zone;
import se.spaced.shared.util.ListenerDispatcher;
import se.spaced.shared.util.cache.CacheManager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RootZoneServiceImpl implements RootZoneService {
	private final Logger log = LoggerFactory.getLogger(getClass());
	private String fileName;
	private final ZoneActivationService zoneActivationService;
	private final ZoneXmlReader zoneXmlReader;
	private final CacheManager xmoCacheManager;
	private final ExecutorService reloadExecutor;
	private final ListenerDispatcher<RootZoneServiceListener> listeners = ListenerDispatcher.create(RootZoneServiceListener.class);

	@Inject
	public RootZoneServiceImpl(
			ZoneActivationService zoneActivationService,
			ZoneXmlReader zoneXmlReader,
			@Named("xmoCachedManager") CacheManager xmoCacheManager,
			LifetimeManager lifetimeManager) {
		this.zoneActivationService = zoneActivationService;
		this.zoneXmlReader = zoneXmlReader;
		this.xmoCacheManager = xmoCacheManager;

		reloadExecutor = Executors.newFixedThreadPool(1, SimpleThreadFactory.withPrefix("reloadThread-"));
		lifetimeManager.addListener(new ExecutorServiceLifetimeAdapter(reloadExecutor));
	}

	@Override
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	@Override
	public void reload(final SpacedVector3 cameraPosition) {
		reloadExecutor.execute(new Runnable() {
			@Override
			public void run() {
				xmoCacheManager.invalidateAll();
				Zone rootZone = zoneXmlReader.loadRootZone(fileName);
				double loadDistance = 1000;
				zoneActivationService.setRootZone(rootZone, cameraPosition, loadDistance);
				listeners.trigger().onReload(rootZone);
			}
		});

	}

	@Override
	public void addListener(RootZoneServiceListener listener) {
		listeners.addListener(listener);
	}
}
