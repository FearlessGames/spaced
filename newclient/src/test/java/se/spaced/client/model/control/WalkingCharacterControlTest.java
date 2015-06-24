package se.spaced.client.model.control;

import com.ardor3d.renderer.Camera;
import com.ardor3d.scenegraph.Node;
import org.junit.Before;
import org.junit.Test;
import se.ardortech.math.SpacedRotation;
import se.ardortech.math.SpacedVector3;
import se.fearless.common.time.TimeProvider;
import se.fearless.common.util.MockTimeProvider;
import se.fearless.common.uuid.UUIDFactory;
import se.fearless.common.uuid.UUIDMockFactory;
import se.mockachino.Invocation;
import se.spaced.client.model.ClientEntity;
import se.spaced.client.model.UserCharacter;
import se.spaced.client.model.control.states.LocalRecorder;
import se.spaced.client.model.listener.ClientEntityListener;
import se.spaced.client.model.listener.UserCharacterListener;
import se.spaced.client.physics.BulletPhysics;
import se.spaced.client.physics.PhysicsWorld;
import se.spaced.shared.model.*;
import se.spaced.shared.model.stats.EntityStats;
import se.spaced.shared.model.stats.StatData;
import se.spaced.shared.network.protocol.codec.datatype.EntityData;
import se.spaced.shared.util.ListenerDispatcher;

import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static se.mockachino.Mockachino.*;

public class WalkingCharacterControlTest {
	private static final UUIDFactory UUID_FACTORY = new UUIDMockFactory();
	private WalkingCharacterControl walkingCharacterControl;
	private ThirdPersonCamera cameraControl;
	private PhysicsWorld physicsWorld;
	private UserCharacter userCharacter;
	private ClientEntity clientEntity;
	private TimeProvider timeProvider;
	private CharacterControlLuaHandler luaHandler;
	private Camera camera;
	private GroundImpactListener groundImpactListener;
	private PositionalData positionalData;
	private SpacedVector3 position;
	private SpacedRotation rotation;

	@Before
	public void setUp() throws Exception {
		timeProvider = new MockTimeProvider();
		cameraControl = mock(ThirdPersonCamera.class);
		physicsWorld = spy(new BulletPhysics(new Node()));
		userCharacter = spy(new UserCharacter(ListenerDispatcher.create(UserCharacterListener.class)));
		position = SpacedVector3.ZERO;
		rotation = SpacedRotation.IDENTITY;
		positionalData = spy(new PositionalData(position, rotation));
		EntityData entityData = new EntityData(UUID_FACTORY.combUUID(), "Name", positionalData,
				new AppearanceData("modelName", "portraitName"),
				CreatureType.NULL_TYPE,
				new EntityStats(timeProvider, new StatData(12, 10, 0, 1.0, EntityStats.IN_COMBAT_COOLRATE, 0.0)),
				Faction.NULL_TYPE, AnimationState.IDLE, EntityState.ALIVE);
		clientEntity = new ClientEntity(entityData, ListenerDispatcher.create(ClientEntityListener.class));
		userCharacter.setControlledEntity(clientEntity);
		getData(userCharacter).deleteLastInvocation();
		luaHandler = new CharacterControlLuaHandler();
		camera = mock(Camera.class);
		groundImpactListener = mock(GroundImpactListener.class);

		walkingCharacterControl = new WalkingCharacterControl(
				cameraControl,
				physicsWorld,
				userCharacter,
				luaHandler,
				camera,
				groundImpactListener, mock(LocalRecorder.class), mock(ClientTeleporter.class));
	}

	@Test
	public void testUpdateCamera() throws Exception {

	}

	@Test
	public void testUpdatePhysics() throws Exception {
	}

	@Test
	public void testSteerForward() throws Exception {
		luaHandler.moveForward();
		walkingCharacterControl.updateSteering(0.05, physicsWorld);
		printAllInteractions("updateSteering");

		assertEquals(new SpacedVector3(0.000000, 0.000000, 3.500000), walkingCharacterControl.inputVector);
		assertEquals(new SpacedRotation(0.000000, 0.000000, 0.000000, 1.000000), walkingCharacterControl.desiredRotation);

		walkingCharacterControl.updateSteering(0.05, physicsWorld);

		assertEquals(new SpacedVector3(0.000000, 0.000000, 7.000000), walkingCharacterControl.inputVector);
		assertEquals(new SpacedRotation(0.000000, 0.000000, 0.000000, 1.000000), walkingCharacterControl.desiredRotation);
	}

	@Test
	public void testTurnRightStandingStill() throws Exception {
		luaHandler.moveRight();
		walkingCharacterControl.updateSteering(0.05, physicsWorld);

		assertEquals(new SpacedVector3(0.0, 0.0, 0.0), walkingCharacterControl.inputVector);
		assertEquals(new SpacedRotation(0.0, -0.09983341664682818, 0.0, 0.9950041652780259), walkingCharacterControl.desiredRotation);

		walkingCharacterControl.updateSteering(0.05, physicsWorld);

		assertEquals(new SpacedVector3(0.0, 0.0, 0.0), walkingCharacterControl.inputVector);
		assertEquals(new SpacedRotation(0.0, -0.1986693307950613, 0.0, 0.980066577841242), walkingCharacterControl.desiredRotation);
		printAllInteractions("testTurnRightStandingStill");
		printImportantMembers();

	}

	@Test
	public void testTurnLeftStandingStillThenStartWalking() throws Exception {
		luaHandler.moveLeft();
		walkingCharacterControl.updateSteering(0.1, physicsWorld);

		assertEquals(new SpacedVector3(0.0, 0.0, 0.0), walkingCharacterControl.inputVector);
		assertEquals(new SpacedRotation(0.0, 0.19866933079506122, 0.0, 0.9800665778412416), walkingCharacterControl.desiredRotation);

		luaHandler.moveLeftStop();
		luaHandler.moveForward();

		// TODO: which way is better? Physics is more real but the direct way isolates the steering part
		//walkingCharacterControl.updatePhysics(1.0, physicsWorld);
		userCharacter.getPositionalData().setRotation(walkingCharacterControl.desiredRotation);

		walkingCharacterControl.updateSteering(0.05, physicsWorld);

		assertEquals(new SpacedRotation(0.000000000000000000000000, 0.198669330795061220000000, 0.000000000000000000000000, 0.980066577841241600000000), walkingCharacterControl.desiredRotation);
		assertEquals(new SpacedVector3(1.362964198080276700000000, 0.000000000000000000000000, 3.223713479010098300000000), walkingCharacterControl.inputVector);
		printAllInteractions("testTurnLeftStandingStillThenStartWalking");
		printImportantMembers();

		// TODO: Verify important interactions if we go for the physics way
	}

	@Test
	public void testOnMouseMove() throws Exception {
	}

	@Test
	public void testOnMouseWheel() throws Exception {
	}

	@Test
	public void testOnSelected() throws Exception {
		SpacedVector3 pos = new SpacedVector3(30, 20, 10);
		userCharacter.getPositionalData().setPosition(pos);
		walkingCharacterControl.onSelected();

		verifyOnce().on(physicsWorld).teleportMan(pos);
	}


	private void printAllInteractions(String name) {
		System.out.println(" ===== Invocations for " + name + " =====");
		printInteractions("cameraControl", cameraControl);
		printInteractions("physicsWorld", physicsWorld);
		printInteractions("userCharacter", userCharacter);
		printInteractions("camera", camera);
		printInteractions("groundImpactListener", groundImpactListener);
		printInteractions("positionalData", positionalData);

		System.out.println(" ===== end of " + name + " =====");
	}

	private void printInteractions(String name, Object o) {
		Iterable<Invocation> invocations = getData(o).getInvocations();
		for (Invocation invocation : invocations) {
			System.out.println(name + "." + invocation.getMethodCall());
		}
	}

	private void printImportantMembers() {
		System.out.printf(new Locale("en"),
				"assertEquals(new SpacedVector3(%.24f, %.24f, %.24f), walkingCharacterControl.inputVector);%n",
				walkingCharacterControl.inputVector.getX(),
				walkingCharacterControl.inputVector.getY(),
				walkingCharacterControl.inputVector.getZ());
		System.out.printf(new Locale("en"),
				"assertEquals(new SpacedRotation(%.24f, %.24f, %.24f, %.24f), walkingCharacterControl.desiredRotation);%n",
				walkingCharacterControl.desiredRotation.getX(),
				walkingCharacterControl.desiredRotation.getY(),
				walkingCharacterControl.desiredRotation.getZ(),
				walkingCharacterControl.desiredRotation.getW());
	}

}
