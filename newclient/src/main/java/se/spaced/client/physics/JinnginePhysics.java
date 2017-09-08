package se.spaced.client.physics;

import com.ardor3d.image.Texture;
import com.ardor3d.image.TextureStoreFormat;
import com.ardor3d.math.Quaternion;
import com.ardor3d.math.Vector3;
import com.ardor3d.math.type.ReadOnlyQuaternion;
import com.ardor3d.math.type.ReadOnlyVector3;
import com.ardor3d.renderer.state.TextureState;
import com.ardor3d.scenegraph.Mesh;
import com.ardor3d.scenegraph.Node;
import com.ardor3d.scenegraph.Spatial;
import com.ardor3d.scenegraph.shape.Box;
import com.ardor3d.scenegraph.shape.Sphere;
import com.ardor3d.util.TextureManager;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.ardortech.math.SpacedVector3;
import se.spaced.client.model.Prop;

import javax.vecmath.Quat4f;
import javax.vecmath.Tuple3f;
import javax.vecmath.Tuple4f;
import javax.vecmath.Vector3f;
import java.util.List;

@Singleton
public class JinnginePhysics implements PhysicsWorld<Object> {

	private final Logger log = LoggerFactory.getLogger(getClass());

	private static final SpacedVector3 WORLD_UP = SpacedVector3.PLUS_J;

	// the maximum size of the collision world. Make sure objects stay
	// within these boundaries
	// Don't make the world AABB size too large, it will harm simulation
	// quality and performance
	Vector3f worldAabbMin = new Vector3f(-10000, -10000, -10000);
	Vector3f worldAabbMax = new Vector3f(10000, 10000, 10000);

	int maxProxies = 1024 * 8;

	// temp
	float manShapeHalfHeight = 0.6f;
	// CapsuleShape manShape = new CapsuleShape(1,2);

	// Normal stuff
	SpacedVector3 groundNormal = SpacedVector3.PLUS_I;
	SpacedVector3 heightFieldP0;
	SpacedVector3 heightFieldP1;
	SpacedVector3 heightFieldP2;

	@Override
	public void setManBodyVelocity(SpacedVector3 desiredVelocity) {
		throw new UnsupportedOperationException();
	}


	enum GroundHit {
		HEIGHTFIELD,
		PHYSICMESH,
		NONE
	}

	GroundHit hasHit = GroundHit.NONE;


	private boolean debugPhysicsShapes = false;
	private final Node entityNode;
	private final Spatial playerDebugNode = new Sphere("playerPhysicsDebug", 16, 16, 1);
	private final Spatial normalDebugNode = new Box("normal", new Vector3(-0.1, -0.1, 0), new Vector3(0.1, 0.1, 3));

	@Inject
	public JinnginePhysics(@Named("entityNode") Node entityNode) {
		this.entityNode = entityNode;

		createMan();
		setupPlayerPhysicsDebug();
	}

	private void setupPlayerPhysicsDebug() {
		final TextureState ts = new TextureState();
		ts.setTexture(TextureManager.load("/textures/nature/tiles/watertile.png",
				Texture.MinificationFilter.Trilinear, TextureStoreFormat.GuessNoCompressedFormat, false));
		playerDebugNode.setRenderState(ts);
		if (debugPhysicsShapes) {
			entityNode.attachChild(playerDebugNode);
		}
	}

	private void updatePhyicsDebug() {
		playerDebugNode.setTranslation(getManPosition());
		playerDebugNode.setRotation(getManRotation());
	}

	@Override
	public double getHeight(SpacedVector3 pos) {
		return Double.MIN_VALUE;
	}

	public static Vector3f fromArdorV(ReadOnlyVector3 v) {
		return new Vector3f(v.getXf(), v.getYf(), v.getZf());
	}

	public static SpacedVector3 toApacheCommonsVector(Tuple3f v) {
		return new SpacedVector3(v.x, v.y, v.z);
	}

	public static Vector3f fromApacheV(SpacedVector3 vector3D) {
		return new Vector3f((float) vector3D.getX(), (float) vector3D.getY(), (float) vector3D.getZ());
	}

	public static Vector3 toArdorV(Tuple3f v) {
		return new Vector3(v.x, v.y, v.z);
	}

	public static Quat4f fromArdorQ(ReadOnlyQuaternion q) {
		return new Quat4f(q.getXf(), q.getYf(), q.getZf(), q.getWf());
	}

	public static Quaternion toArdorQ(Tuple4f q) {
		return new Quaternion(q.x, q.y, q.z, q.w);
	}

	private void createMan() {
	}

	@Override
	public void stepMan(double dt) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void stepManStill() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void teleportMan(SpacedVector3 p) {
		throw new UnsupportedOperationException();
	}

	@Override
	public SpacedVector3 getManPosition() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Quaternion getManRotation() {
		throw new UnsupportedOperationException();
	}

	@Override
	public double getManHalfHeight() {
		return manShapeHalfHeight;
	}


	@Override
	public SpacedVector3 getGroundNormal() {
		return groundNormal;
	}

	protected void updateGroundContact() {
		groundNormal = SpacedVector3.PLUS_J;
		hasHit = GroundHit.NONE;

		// First test vs landscape
		SpacedVector3 p = getManPosition();
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean getGroundHit() {
		return hasHit != GroundHit.NONE;
	}

	@Override
	public boolean getGroundContact() {
		return getGroundHit() && SpacedVector3.dotProduct(groundNormal, WORLD_UP) > 0.707f;
	}

	private void debugNormal(boolean hit, SpacedVector3 hitPositionWorld, SpacedVector3 hitNormalWorld) {
		if (debugPhysicsShapes) {
			if (hit) {
				Quaternion q = new Quaternion();
				q.lookAt(hitNormalWorld, Vector3.UNIT_X);
				normalDebugNode.setTranslation(hitPositionWorld);
				normalDebugNode.setRotation(q);
				entityNode.attachChild(normalDebugNode);
			} else {
				normalDebugNode.removeFromParent();
			}
		}
	}

	@Override
	public void addCollisionObject(PhysicsObject<Object> object, short group, short mask) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void removeCollisionObject(PhysicsObject<Object> object) {
		throw new UnsupportedOperationException();
	}

	@Override
	public double castRay(ReadOnlyVector3 from, ReadOnlyVector3 to, short filterMask) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void buildPhysicsOn(Prop prop) {
		List<Mesh> physicsMeshes = prop.getXmoEntity().getPhysicsMeshes();
		for (Mesh physicsMesh : physicsMeshes) {
			Spatial top = getTop(physicsMesh);
			top.updateWorldTransform(true);

		}
	}

	private Spatial getTop(Spatial top) {
		while (top.getParent() != null) {
			top = top.getParent();
		}
		return top;
	}


}
