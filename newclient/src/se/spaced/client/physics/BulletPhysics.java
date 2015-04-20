package se.spaced.client.physics;

import com.ardor3d.image.Texture;
import com.ardor3d.image.TextureStoreFormat;
import com.ardor3d.math.Matrix4;
import com.ardor3d.math.Quaternion;
import com.ardor3d.math.Vector3;
import com.ardor3d.math.type.ReadOnlyQuaternion;
import com.ardor3d.math.type.ReadOnlyTransform;
import com.ardor3d.math.type.ReadOnlyVector3;
import com.ardor3d.renderer.state.TextureState;
import com.ardor3d.scenegraph.Mesh;
import com.ardor3d.scenegraph.Node;
import com.ardor3d.scenegraph.Spatial;
import com.ardor3d.scenegraph.shape.Box;
import com.ardor3d.scenegraph.shape.Sphere;
import com.ardor3d.util.TextureManager;
import com.bulletphysics.collision.broadphase.AxisSweep3;
import com.bulletphysics.collision.dispatch.CollisionConfiguration;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.collision.dispatch.CollisionWorld;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.collision.shapes.BvhTriangleMeshShape;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.SphereShape;
import com.bulletphysics.collision.shapes.TriangleIndexVertexArray;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.Transform;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.ardortech.math.SpacedVector3;
import se.spaced.client.model.Prop;
import se.spaced.shared.model.xmo.XmoEntity;

import javax.vecmath.Quat4f;
import javax.vecmath.Tuple3f;
import javax.vecmath.Tuple4f;
import javax.vecmath.Vector3f;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

@Singleton
public class BulletPhysics implements PhysicsWorld<CollisionObject> {

	private final Logger log = LoggerFactory.getLogger(getClass());

	private static final SpacedVector3 WORLD_UP = SpacedVector3.PLUS_J;

	// collision configuration contains default setup for memory, collision
	// setup. Advanced users can create their own configuration.
	CollisionConfiguration collisionConfiguration = new DefaultCollisionConfiguration();

	// use the default collision dispatcher. For parallel processing you
	// can use a diffent dispatcher (see Extras/BulletMultiThreaded)
	CollisionDispatcher dispatcher = new CollisionDispatcher(collisionConfiguration);

	// the maximum size of the collision world. Make sure objects stay
	// within these boundaries
	// Don't make the world AABB size too large, it will harm simulation
	// quality and performance
	Vector3f worldAabbMin = new Vector3f(-10000, -10000, -10000);
	Vector3f worldAabbMax = new Vector3f(10000, 10000, 10000);

	int maxProxies = 1024 * 8;
	AxisSweep3 overlappingPairCache = new AxisSweep3(worldAabbMin, worldAabbMax, maxProxies);
	//BroadphaseInterface overlappingPairCache = new SimpleBroadphase(maxProxies);

	// the default constraint solver. For parallel processing you can use a
	// different solver (see Extras/BulletMultiThreaded)
	SequentialImpulseConstraintSolver solver = new SequentialImpulseConstraintSolver();

	final DiscreteDynamicsWorld dynamicsWorld = new DiscreteDynamicsWorld(
			dispatcher, overlappingPairCache, solver,
			collisionConfiguration);

	// temp
	float manShapeHalfHeight = 0.6f;
	// CapsuleShape manShape = new CapsuleShape(1,2);
	SphereShape manShape = new SphereShape(manShapeHalfHeight);
	RigidBody manBody;
	Transform fromWorld = new Transform();
	Transform toWorld = new Transform();

	// Normal stuff
	SpacedVector3 groundNormal = SpacedVector3.PLUS_J;

	public void setManBodyVelocity(SpacedVector3 desiredVelocity) {
		manBody.setLinearVelocity(fromApacheV(desiredVelocity));
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
	public BulletPhysics(@Named("entityNode") Node entityNode) {
		this.entityNode = entityNode;

		fromWorld.setIdentity();
		toWorld.setIdentity();

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

	public static Transform fromArdorVQ(ReadOnlyVector3 pos, ReadOnlyQuaternion rot) {
		Transform t = new Transform();
		t.origin.set(fromArdorV(pos));
		t.basis.set(fromArdorQ(rot));
		return t;
	}

	public static Transform fromArdorT(ReadOnlyTransform in) {
		float[] glMatrix = new float[16];
		Matrix4 ardorMat4 = in.getHomogeneousMatrix(null);
		for (int i = 0; i < 16; ++i) {
			glMatrix[i] = ardorMat4.getValuef(i % 4, i / 4);
		}

		Transform out = new Transform();
		out.setFromOpenGLMatrix(glMatrix);
		return out;
	}

	private void createMan() {
		Transform startTransform = new Transform();
		startTransform.setIdentity();

		Vector3f localInertia = new Vector3f(0, 0, 0);
		float mass = 80;
		manShape.calculateLocalInertia(mass, localInertia);

		DefaultMotionState myMotionState = new DefaultMotionState(startTransform);
		RigidBodyConstructionInfo rbInfo = new RigidBodyConstructionInfo(mass, myMotionState, manShape, localInertia);
		manBody = new RigidBody(rbInfo);
		manBody.setActivationState(CollisionObject.DISABLE_DEACTIVATION);
		manBody.setDamping(0.01f, 1.0f);


		dynamicsWorld.addRigidBody(manBody, CollisionFilter.PLAYER_GROUP, CollisionFilter.PLAYER_MASK);
	}

	@Override
	public void stepMan(double dt) {

		manBody.setAngularVelocity(new Vector3f(0, 0, 0));
		//by sending in 0 to maxSubsSteps dynamicWorld doenst interpolate but rather use the supplied dt
		dynamicsWorld.stepSimulation((float) dt, 0, 0);

		SpacedVector3 manPosition = getManPosition();

		updateGroundContact();
		updatePhyicsDebug();
	}

	@Override
	public void stepManStill() {
		manBody.setAngularVelocity(new Vector3f(0, 0, 0));
		updateGroundContact();
		updatePhyicsDebug();
	}

	@Override
	public void teleportMan(SpacedVector3 p) {
		setManPositon(p.add(new SpacedVector3(0, manShapeHalfHeight, 0)));
	}

	protected void setManPositon(SpacedVector3 p) {
		Transform t = new Transform();
		t.setIdentity();
		t.origin.set((float) p.getX(), (float) p.getY(), (float) p.getZ());
		manBody.setWorldTransform(t);
	}

	@Override
	public SpacedVector3 getManPosition() {
		Transform t = new Transform();
		manBody.getWorldTransform(t);
		return toApacheCommonsVector(t.origin);
	}

	@Override
	public Quaternion getManRotation() {
		Quat4f out = new Quat4f();
		manBody.getOrientation(out);
		return toArdorQ(out);
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
		float softRadius = 0.1f;
		// Check with a shapecast
		fromWorld.origin.set((float) p.getX(), (float) p.getY() + softRadius, (float) p.getZ());
		toWorld.origin.set((float) p.getX(), (float) p.getY() - softRadius, (float) p.getZ());

		CollisionWorld.ClosestConvexResultCallback result = new CollisionWorld.ClosestConvexResultCallback(fromWorld.origin,
				toWorld.origin);

		dynamicsWorld.removeCollisionObject(manBody);
		dynamicsWorld.convexSweepTest(manShape, fromWorld, toWorld, result);
		dynamicsWorld.addCollisionObject(manBody);

		if (result.hasHit()) {
			hasHit = GroundHit.PHYSICMESH;
		}
		groundNormal = toApacheCommonsVector(result.hitNormalWorld);
		debugNormal(result.hasHit(), toApacheCommonsVector(result.hitPointWorld), groundNormal);
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
	public void addCollisionObject(PhysicsObject<CollisionObject> object, short group, short mask) {
		dynamicsWorld.addCollisionObject(object.getObject(), group, mask);
	}

	@Override
	public void removeCollisionObject(PhysicsObject<CollisionObject> object) {
		dynamicsWorld.removeCollisionObject(object.getObject());
	}

	@Override
	public double castRay(ReadOnlyVector3 from, ReadOnlyVector3 to, short filterMask) {
		// dynamicsWorld.convexSweepTest();
		Vector3f p0 = fromArdorV(from);
		Vector3f p1 = fromArdorV(to);
		CollisionWorld.RayResultCallback resultCallback = new CollisionWorld.ClosestRayResultCallback(p0, p1);
		resultCallback.collisionFilterMask = filterMask;

		dynamicsWorld.rayTest(p0, p1, resultCallback);

		if (resultCallback.hasHit()) {
			return resultCallback.closestHitFraction;
		}
		return Double.MAX_VALUE;
	}

	@Override
	public void buildPhysicsOn(Prop prop) {
		XmoEntity xmoEntity = prop.getXmoEntity();
		for (Mesh physicsMesh : xmoEntity.getPhysicsMeshes()) {
			try {
				// Find the top node so we can update the worldTransform correctly
				Spatial top = getTop(physicsMesh);
				top.updateWorldTransform(true);

				// Convert transforms
				Transform worldTransform = BulletPhysics.fromArdorT(xmoEntity.getModel().getWorldTransform());
				Transform meshTransform = BulletPhysics.fromArdorT(physicsMesh.getWorldTransform());
				worldTransform.mul(meshTransform);

				// Convert mesh
				CollisionObject colObj = convertToCollObject(physicsMesh, worldTransform, true);
				PhysicsObject<CollisionObject> object = new PhysicsObject<CollisionObject>(colObj);
				prop.getXmoEntity().getCollisionObjects().add(object);

			} catch (Exception e) {
				log.error("Failed to build physics: " + prop + " - " + physicsMesh.getName(), e);
			}
		}
	}

	private Spatial getTop(Spatial top) {
		while (top.getParent() != null) {
			top = top.getParent();
		}
		return top;
	}

	private CollisionObject convertToCollObject(Mesh physicsMesh, Transform worldTransform, boolean bakeTransform) {
		IntBuffer indexIntBuffer = physicsMesh.getMeshData().getIndices().asIntBuffer().duplicate();
		FloatBuffer vertexFloatBuffer = physicsMesh.getMeshData().getVertexBuffer().duplicate();

		indexIntBuffer.rewind();
		vertexFloatBuffer.rewind();
		int totalTriangles = indexIntBuffer.remaining() / 3;
		int totalVertices = vertexFloatBuffer.remaining() / 3;

		// Convert buffers
		ByteBuffer indexByteBuffer = ByteBuffer.allocate(indexIntBuffer.remaining() * 4);
		indexByteBuffer.asIntBuffer().put(indexIntBuffer);
		indexByteBuffer.rewind();

		ByteBuffer vertexByteBuffer = ByteBuffer.allocate(vertexFloatBuffer.remaining() * 4);
		if (bakeTransform) {
			Tuple3f v = new Vector3f();
			FloatBuffer bytfbuf = vertexByteBuffer.asFloatBuffer();
			for (int i = 0; i < totalVertices; ++i) {
				v.set(vertexFloatBuffer.get(), vertexFloatBuffer.get(), vertexFloatBuffer.get());
				worldTransform.basis.transform(v);
				bytfbuf.put(v.x);
				bytfbuf.put(v.y);
				bytfbuf.put(v.z);
			}
			worldTransform.basis.setIdentity();
		} else {
			vertexByteBuffer.asFloatBuffer().put(vertexFloatBuffer);
		}
		vertexByteBuffer.rewind();

		// Create the physics version of the mesh
		TriangleIndexVertexArray triangleIndexVertexArray = new TriangleIndexVertexArray(
				totalTriangles, indexByteBuffer, 3 * 4,
				totalVertices, vertexByteBuffer, 3 * 4
		);

		CollisionShape triangleShape = new BvhTriangleMeshShape(triangleIndexVertexArray, true);
		CollisionObject colObj = new CollisionObject();
		colObj.setCollisionShape(triangleShape);

		colObj.setWorldTransform(worldTransform);
		return colObj;
	}

}
