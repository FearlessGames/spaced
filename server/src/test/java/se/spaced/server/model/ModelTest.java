package se.spaced.server.model;

import org.junit.Before;
import org.junit.Test;
import se.ardortech.math.SpacedRotation;
import se.ardortech.math.SpacedVector3;
import se.fearless.common.uuid.UUID;
import se.mockachino.annotations.Mock;
import se.spaced.messages.protocol.s2c.S2CProtocol;
import se.spaced.server.ScenarioTestBase;
import se.spaced.server.model.spawn.MobTemplate;
import se.spaced.shared.model.AnimationState;
import se.spaced.shared.playback.MovementPoint;

import java.util.Collection;

import static org.junit.Assert.*;


public class ModelTest extends ScenarioTestBase {
	private final UUID MOB_ID = uuidFactory.randomUUID();
	private static final String ENTITY_NAME = "trogdor";
	private static final SpacedVector3 NEW_POSITION = new SpacedVector3(30, 40, 50);
	private static final SpacedRotation NEW_ROTATION = new SpacedRotation(20, 30, 40, 10, true);

	private ServerEntity mob;

	@Mock
	private S2CProtocol smrtReceiver;

	@Before
	public void setUp() {
		MobTemplate mobTemplate = new MobTemplate.Builder(uuidFactory.randomUUID(), ENTITY_NAME).stamina(10).build();
		mob = mobTemplate.createMob(timeProvider, MOB_ID, randomProvider);
		entityService.addEntity(mob, smrtReceiver);
	}

	@Test
	public void findEntityById() {
		ServerEntity e = entityService.getEntity(MOB_ID);
		assertSame(mob, e);
	}

	@Test
	public void findEntityByIdFailure() {
		ServerEntity e = entityService.getEntity(uuidFactory.randomUUID());
		assertNull(e);
	}

	@Test
	public void findEntityByName() {
		ServerEntity entity = entityService.findEntityByName(ENTITY_NAME);
		assertEquals(MOB_ID, entity.getPk());
	}

	@Test
	public void findEntityByNameFailure() {
		ServerEntity entity = entityService.findEntityByName(ENTITY_NAME + "qwerty");
		assertNull(entity);
	}

	@Test
	public void addedEntityIsPresentInModel() {
		Collection<ServerEntity> allEntities = entityService.getAllEntities(null);
		assertTrue(allEntities.contains(mob));
	}

	@Test
	public void moveEntityChangesPosition() {
		movementService.moveAndRotateEntity(entityService.getEntity(MOB_ID),
				new MovementPoint<AnimationState>(0, AnimationState.IDLE, NEW_POSITION, NEW_ROTATION));
		assertEquals(NEW_POSITION, mob.getPosition());
	}

	@Test
	public void deadEntityCantChangePosition() {
		mob.kill();
		SpacedVector3 oldPos = mob.getPosition();
		movementService.moveAndRotateEntity(entityService.getEntity(MOB_ID),
				new MovementPoint<AnimationState>(0, AnimationState.IDLE, NEW_POSITION, NEW_ROTATION));
		assertEquals(oldPos, mob.getPosition());
	}

	@Test
	public void rotateEntityChangesRotation() {
		movementService.moveAndRotateEntity(entityService.getEntity(MOB_ID),
				new MovementPoint<AnimationState>(0, AnimationState.IDLE, SpacedVector3.ZERO, NEW_ROTATION));
		assertEquals(NEW_ROTATION, mob.getRotation());
	}
}
