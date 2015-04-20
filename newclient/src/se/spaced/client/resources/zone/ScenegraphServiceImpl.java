package se.spaced.client.resources.zone;

import com.ardor3d.bounding.BoundingVolume;
import com.ardor3d.scenegraph.Node;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.ardortech.math.SpacedVector3;
import se.fearlessgames.common.lifetime.LifetimeManager;
import se.spaced.client.model.Prop;
import se.spaced.client.physics.CollisionFilter;
import se.spaced.client.physics.PhysicsObject;
import se.spaced.client.physics.PhysicsWorld;
import se.spaced.shared.model.xmo.XmoEntity;
import se.spaced.shared.resources.zone.Zone;
import se.spaced.shared.util.QueueRunner;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

@Singleton
public class ScenegraphServiceImpl implements ScenegraphService, QueueRunner.Callback<Prop, Void> {
	private final Logger log = LoggerFactory.getLogger(getClass());

	private final Node propsRoot;
	private final PhysicsWorld physicsWorld;
	private final Set<Prop> visibleProps = Sets.newHashSet();
	final Set<Prop> notVisibleProps = Collections.synchronizedSet(new HashSet<Prop>());
	final Queue<PhysicsObject> collisionObjectAddQueue = new ConcurrentLinkedQueue<PhysicsObject>();
	private final QueueRunner<Prop, Void> propXmoEntityCreator;
	private int numberOfJobsRemaining;
	private LoadListener loadListener;
	private final AtomicInteger waitCountdown = new AtomicInteger();
	private Runnable waitCallback;

	@Inject
	public ScenegraphServiceImpl(
			@Named("propsNode") Node propsRoot, PhysicsWorld physicsWorld,
			LifetimeManager lifetimeManager,
			QueueRunner<Prop, Void> propXmoEntityCreator) {
		this.propsRoot = propsRoot;
		this.physicsWorld = physicsWorld;

		this.propXmoEntityCreator = propXmoEntityCreator;

		lifetimeManager.addListener(this.propXmoEntityCreator);

	}

	@Override
	public void setLoadListener(LoadListener loadListener) {
		this.loadListener = loadListener;
	}

	@Override
	public boolean waitingForPhysics() {
		return !this.collisionObjectAddQueue.isEmpty();
	}

	@Override
	public void waitFrames(int frames, Runnable callback) {
		this.waitCountdown.set(frames);
		this.waitCallback = callback;
	}

	@Override
	public void detachNode(Node node) {
		propsRoot.detachChild(node);
	}

	@Override
	public void attachNode(Node node) {
		propsRoot.attachChild(node);
	}

	@Override
	public void update(SpacedVector3 position, double viewDistance) {
		// TODO: optimize this!
		double propIn = getSetting("pIn", 16000);
		double propOut = propIn + getSetting("pOut", 4);
		double exp = getSetting("pExp", 2);
		double minRange = getSetting("minRange", 30);
		List<Prop> propsToAddToVisibleSet = Lists.newArrayList();
		synchronized (notVisibleProps) {
			for (Prop prop : notVisibleProps) {
				if (shouldBeVisible(position, propIn, prop, exp, minRange)) {
					showProp(prop);
					propsToAddToVisibleSet.add(prop);
				}
			}
		}
		notVisibleProps.removeAll(propsToAddToVisibleSet);
		List<Prop> propsToRemoveFromVisibleSet = Lists.newArrayList();
		for (Prop prop : visibleProps) {
			if (!shouldBeVisible(position, propOut, prop, exp, minRange)) {
				hideProp(prop);
				notVisibleProps.add(prop);
				propsToRemoveFromVisibleSet.add(prop);
			}
		}
		visibleProps.addAll(propsToAddToVisibleSet);
		visibleProps.removeAll(propsToRemoveFromVisibleSet);

		addPropsPhysicsFromQueue();
		if (waitCountdown.decrementAndGet() == 0) {
			waitCallback.run();
		}
	}

	private void notifyProgress(LoadListener loadListener) {
		if (loadListener != null) {
			int remaining = numberOfJobsRemaining;
			log.debug("{} remaining", remaining);
			loadListener.loadUpdate(remaining);
			if (remaining == 0) {
				loadListener.loadCompleted();
			}
		}
	}

	private double getSetting(String name, double defaultValue) {
		return defaultValue;
	}

	private boolean shouldBeVisible(SpacedVector3 position, double scaleFactor, Prop prop, double exp, double minRange) {
		Node mesh = prop.getXmoEntity().getModel();
		double distanceToProp = mesh.getTranslation().distance(position);
		if (distanceToProp < minRange) {
			return true;
		}
		BoundingVolume volume = mesh.getWorldBound();
		double boundingSize = Math.abs(volume.distanceToEdge(volume.getCenter()));
		double weightedDistanceToProp = Math.pow(distanceToProp, exp);
		return weightedDistanceToProp < scaleFactor * boundingSize;
	}

	private void addPropsPhysicsFromQueue() {
		while (!collisionObjectAddQueue.isEmpty()) {
			PhysicsObject collisionObject = collisionObjectAddQueue.poll();
			if (collisionObject == null) {
				return;
			}
			physicsWorld.addCollisionObject(collisionObject, CollisionFilter.STATIC_GROUP, CollisionFilter.STATIC_MASK);
		}
	}

	private void removePropsPhysics(Prop prop) {
		if (prop.getXmoEntity() != null) {
			for (PhysicsObject<?> collisionObject : prop.getXmoEntity().getCollisionObjects()) {
				physicsWorld.removeCollisionObject(collisionObject);
			}
		}
	}

	private void showProp(Prop prop) {
		prop.getZone().getNode().attachChild(prop.getXmoEntity().getModel());
	}

	private void hideProp(Prop prop) {
		Zone zone = prop.getZone();
		Node node = zone.getNode();
		XmoEntity xmoEntity = prop.getXmoEntity();
		Node model = xmoEntity.getModel();
		node.detachChild(model);
	}


	@Override
	public void addProp(Prop prop, Zone zone) {
		prop.setZone(zone);
		numberOfJobsRemaining++;
		notifyProgress(loadListener);
		propXmoEntityCreator.runWith(prop, this);
	}


	private void removeProp(Prop prop) {
		hideProp(prop);
		visibleProps.remove(prop);
		notVisibleProps.remove(prop);
		removePropsPhysics(prop);
	}

	@Override
	public void removeProps(Collection<Prop> props) {
		for (Prop prop : props) {
			removeProp(prop);
		}
	}

	@Override
	public void addCollisionObjects(Collection<PhysicsObject<?>> collisionObjects) {
		collisionObjectAddQueue.addAll(collisionObjects);
	}

	@Override
	public void addNotVisibleProp(Prop prop) {
		notVisibleProps.add(prop);
	}

	@Override
	public void afterRunWith(Prop prop, Void aVoid, int numberOfJobsRemaining) {
		this.numberOfJobsRemaining = numberOfJobsRemaining;
		notifyProgress(loadListener);
	}
}
