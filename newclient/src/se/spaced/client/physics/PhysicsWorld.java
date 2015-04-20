package se.spaced.client.physics;

import com.ardor3d.math.Quaternion;
import com.ardor3d.math.type.ReadOnlyVector3;
import se.ardortech.math.SpacedVector3;
import se.spaced.client.model.Prop;

public interface PhysicsWorld<T> {
	double getHeight(SpacedVector3 pos);

	void stepMan(double dt);

	void stepManStill();

	void teleportMan(SpacedVector3 p);

	SpacedVector3 getManPosition();

	Quaternion getManRotation();

	double getManHalfHeight();

	SpacedVector3 getGroundNormal();

	boolean getGroundHit();

	boolean getGroundContact();

	void addCollisionObject(PhysicsObject<T> object, short group, short mask);

	void removeCollisionObject(PhysicsObject<T> object);

	double castRay(ReadOnlyVector3 from, ReadOnlyVector3 to, short filterMask);

	void setManBodyVelocity(SpacedVector3 desiredVelocity);

	void buildPhysicsOn(Prop prop);

}
