package se.spaced.client.resources.zone;

import org.junit.Before;
import org.junit.Test;
import se.ardortech.math.SpacedVector3;
import se.fearless.common.lifetime.LifetimeManager;
import se.fearless.common.lifetime.LifetimeManagerImpl;
import se.mockachino.CallHandler;
import se.mockachino.MethodCall;
import se.spaced.shared.resources.zone.Zone;
import se.spaced.shared.util.cache.CacheManager;

import java.util.concurrent.CountDownLatch;

import static se.mockachino.Mockachino.*;
import static se.mockachino.matchers.Matchers.any;
import static se.mockachino.matchers.Matchers.anyDouble;

public class RootZoneServiceImplTest {

	private ZoneActivationService zoneActivationService;
	private RootZoneService rootZoneService;
	private CacheManager xmoCacheManager;
	private ZoneXmlReader zoneXmlReader;

	@Before
	public void setUp() throws Exception {
		zoneActivationService = mock(ZoneActivationService.class);
		zoneXmlReader = mock(ZoneXmlReader.class);
		xmoCacheManager = mock(CacheManager.class);
		LifetimeManager lifetimeManager = new LifetimeManagerImpl();
		rootZoneService = new RootZoneServiceImpl(zoneActivationService, zoneXmlReader, xmoCacheManager, lifetimeManager);
	}

	@Test
	public void invalidateCacheOnReload() throws Exception {
		final CountDownLatch activationIsDone = new CountDownLatch(1);

		stubAnswer(new CallHandler() {
			@Override
			public Object invoke(Object obj, MethodCall call) throws Throwable {
				activationIsDone.countDown();
				return null;
			}
		}).on(zoneActivationService).setRootZone(any(Zone.class), any(SpacedVector3.class), anyDouble());
		rootZoneService.setFileName("fooZone");

		rootZoneService.reload(SpacedVector3.ZERO);

		activationIsDone.await();

		verifyOnce().on(xmoCacheManager).invalidateAll();

	}

	@Test
	public void useFileNameOnReload() throws Exception {
		final CountDownLatch activationIsDone = new CountDownLatch(1);

		stubAnswer(new CallHandler() {
			@Override
			public Object invoke(Object obj, MethodCall call) throws Throwable {
				activationIsDone.countDown();
				return null;
			}
		}).on(zoneActivationService).setRootZone(any(Zone.class), any(SpacedVector3.class), anyDouble());
		rootZoneService.setFileName("fooZone");

		rootZoneService.reload(SpacedVector3.ZERO);

		activationIsDone.await();

		verifyOnce().on(zoneXmlReader).loadRootZone("fooZone");
	}

	@Test
	public void listenerCallback() throws Exception {
		RootZoneServiceListener listener = mock(RootZoneServiceListener.class);
		rootZoneService.addListener(listener);

		final CountDownLatch listenerCalled = new CountDownLatch(1);
		RootZoneServiceListener waitingListener = new RootZoneServiceListener() {
			@Override
			public void onReload(Zone rootZone) {
				listenerCalled.countDown();
			}
		};
		rootZoneService.addListener(waitingListener);

		final CountDownLatch activationIsDone = new CountDownLatch(1);
		final Zone root = mock(Zone.class);
		stubAnswer(new CallHandler() {
			@Override
			public Object invoke(Object obj, MethodCall call) throws Throwable {
				activationIsDone.await();
				return root;
			}
		}).on(zoneXmlReader).loadRootZone("fooZone");
		rootZoneService.setFileName("fooZone");

		rootZoneService.reload(SpacedVector3.ZERO);
		verifyNever().on(listener).onReload(any(Zone.class));

		activationIsDone.countDown();
		listenerCalled.await();

		verifyOnce().on(listener).onReload(root);
	}
}
