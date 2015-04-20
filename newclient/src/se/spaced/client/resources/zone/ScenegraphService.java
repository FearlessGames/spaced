package se.spaced.client.resources.zone;

import com.ardor3d.scenegraph.Node;
import se.ardortech.math.SpacedVector3;
import se.spaced.client.model.Prop;
import se.spaced.client.physics.PhysicsObject;
import se.spaced.shared.resources.zone.Zone;

import java.util.Collection;

public interface ScenegraphService {
	void update(SpacedVector3 position, double viewDistance);

	void addProp(Prop prop, Zone node);

	void removeProps(Collection<Prop> props);

	void addCollisionObjects(Collection<PhysicsObject<?>> collisionObjects);

	void addNotVisibleProp(Prop prop);

	void setLoadListener(LoadListener loadListener);

	boolean waitingForPhysics();

	void waitFrames(int frames, Runnable callback);

	void detachNode(Node node);

	void attachNode(Node node);
}
