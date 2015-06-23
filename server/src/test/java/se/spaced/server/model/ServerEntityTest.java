package se.spaced.server.model;

import org.junit.Before;
import org.junit.Test;
import se.fearless.common.time.MockTimeProvider;
import se.fearless.common.time.TimeProvider;
import se.fearless.common.uuid.UUID;
import se.fearless.common.uuid.UUIDFactory;
import se.fearless.common.uuid.UUIDFactoryImpl;
import se.spaced.server.model.movement.TransportationMode;
import se.spaced.server.model.spawn.MobTemplate;
import se.spaced.shared.model.aura.ModStat;
import se.spaced.shared.model.stats.Operator;
import se.spaced.shared.model.stats.StatType;
import se.spaced.shared.util.random.RandomProvider;
import se.spaced.shared.util.random.RealRandomProvider;

import static org.junit.Assert.*;

public class ServerEntityTest {
	private final UUIDFactory uuidFactory = UUIDFactoryImpl.INSTANCE;

	private static final double EPSILON = 0.0000001;
	private RandomProvider randomProvider = new RealRandomProvider();
	private MockTimeProvider timeProvider;

	@Before
	public void setUp() throws Exception {
		timeProvider = new MockTimeProvider();
	}

	@Test
	public void deathChangesState() {
		TimeProvider timeProvider = this.timeProvider;
		MobTemplate template = new MobTemplate.Builder(uuidFactory.randomUUID(), "Foo").build();
		ServerEntity entity = template.createMob(timeProvider, UUID.ZERO, randomProvider);

		assertTrue(entity.isAlive());
		entity.kill();
		assertFalse(entity.isAlive());
	}

	@Test
	public void reviveChangesState() {
		MobTemplate template = new MobTemplate.Builder(uuidFactory.randomUUID(), "Foo").build();
		ServerEntity entity = template.createMob(timeProvider, UUID.ZERO, randomProvider);

		entity.kill();
		entity.revive(10);

		assertEquals(10, entity.getBaseStats().getCurrentHealth().getValue(), EPSILON);
		assertTrue(entity.isAlive());
	}

	@Test
	public void walkSpeed() throws Exception {
		MobTemplate template = new MobTemplate.Builder(uuidFactory.randomUUID(), "Foo").walkSpeed(3.5).build();
		Mob entity = template.createMob(timeProvider, UUID.ZERO, randomProvider);
		double walkSpeed = entity.getWalkSpeed();
		assertEquals(3.5, walkSpeed, EPSILON);
	}

	@Test
	public void runSpeed() throws Exception {
		MobTemplate template = new MobTemplate.Builder(uuidFactory.randomUUID(), "Foo").runSpeed(8.0).build();
		Mob entity = template.createMob(timeProvider, UUID.ZERO, randomProvider);
		double runSpeed = entity.getRunSpeed();
		assertEquals(8.0, runSpeed, EPSILON);
	}


	@Test
	public void buffRunSpeed() throws Exception {
		MobTemplate template = new MobTemplate.Builder(uuidFactory.randomUUID(), "Foo").runSpeed(8.0).build();
		Mob entity = template.createMob(timeProvider, UUID.ZERO, randomProvider);
		entity.getBaseStats().getAuraStatByType(StatType.SPEED).addModStat(new ModStat(.1, StatType.SPEED, Operator.ADD));
		double runSpeed = entity.getRunSpeed();
		assertEquals(8.8, runSpeed, EPSILON);
	}


	@Test
	public void debuffWalkSpeed() throws Exception {
		MobTemplate template = new MobTemplate.Builder(uuidFactory.randomUUID(), "Foo").walkSpeed(4.0).build();
		Mob entity = template.createMob(timeProvider, UUID.ZERO, randomProvider);
		entity.getBaseStats().getAuraStatByType(StatType.SPEED).addModStat(new ModStat(-.5, StatType.SPEED, Operator.ADD));
		double walkSpeed = entity.getWalkSpeed();
		assertEquals(2.0, walkSpeed, EPSILON);
	}

	@Test
	public void slowTransportation() throws Exception {
		MobTemplate template = new MobTemplate.Builder(uuidFactory.randomUUID(), "Foo").walkSpeed(4.0).runSpeed(3.5).build();
		Mob mob = template.createMob(timeProvider, UUID.ZERO, randomProvider);
		assertEquals(TransportationMode.RUN, mob.getSlowTransportationMode());

		MobTemplate template2 = new MobTemplate.Builder(uuidFactory.randomUUID(), "Foo").walkSpeed(4.0).runSpeed(5.5).build();
		Mob mob2 = template2.createMob(timeProvider, UUID.ZERO, randomProvider);
		assertEquals(TransportationMode.WALK, mob2.getSlowTransportationMode());
	}
}
