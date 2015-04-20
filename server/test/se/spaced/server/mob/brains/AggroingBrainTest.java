package se.spaced.server.mob.brains;

import org.junit.Before;
import org.junit.Test;
import se.ardortech.math.SpacedRotation;
import se.ardortech.math.SpacedVector3;
import se.spaced.messages.protocol.ItemTemplate;
import se.spaced.messages.protocol.s2c.S2CProtocol;
import se.spaced.server.ScenarioTestBase;
import se.spaced.server.mob.MobOrderExecutor;
import se.spaced.server.model.Mob;
import se.spaced.server.model.PersistedPositionalData;
import se.spaced.server.model.relations.RelationsService;
import se.spaced.server.model.spawn.MobTemplate;
import se.spaced.server.model.spawn.ProximityAggroParameters;
import se.spaced.shared.model.MagicSchool;
import se.spaced.shared.model.items.ContainerType;
import se.spaced.shared.network.protocol.codec.datatype.EntityData;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static se.fearlessgames.common.mock.MockUtil.deepMock;
import static se.mockachino.Mockachino.*;

public class AggroingBrainTest extends ScenarioTestBase {
	private AggroingBrain aggroingBrain;
	private Mob mob;
	private MobOrderExecutor executor;
	private RelationsService relationsService;
	private Mob target;
	private MobTemplate mobTemplate2;

	@Before
	public void setup() {
		executor = mock(MobOrderExecutor.class);
		relationsService = mock(RelationsService.class);
		MobTemplate mobTemplate = new MobTemplate.Builder(uuidFactory.combUUID(), "Foo").build();
		mob = mobTemplate.createMob(timeProvider, uuidFactory.combUUID(), randomProvider);
		mobTemplate2 = new MobTemplate.Builder(uuidFactory.combUUID(), "Bar").build();
		target = mobTemplate2.createMob(timeProvider, uuidFactory.combUUID(), randomProvider);
		mob.setPositionalData(new PersistedPositionalData(new SpacedVector3(100, 200, 300), SpacedRotation.IDENTITY));
		aggroingBrain = new AggroingBrain(mob,
				executor,
				timeProvider,
				relationsService,
				new ProximityAggroParameters(100, 50),
				entityCombatService);
		entityService.addEntity(target, deepMock(S2CProtocol.class));
		entityService.addEntity(mob, deepMock(S2CProtocol.class));
	}

	@Test
	public void testProximityAggroOnAppear() {
		when(relationsService.hates(mob, target)).thenReturn(true);
		target.getPositionalData().setPosition(new SpacedVector3(100, 190, 290));
		EntityData entityData = target.createEntityData();
		aggroingBrain.getSmrtReceiver().entity().entityAppeared(target,
				entityData,
				new HashMap<ContainerType, ItemTemplate>());

		assertEquals(target, mob.getCurrentAggroTarget());
	}

	@Test
	public void testSocialAggro() {
		Mob attacker = mobTemplate2.createMob(timeProvider, uuidFactory.combUUID(), randomProvider);
		entityService.addEntity(attacker, deepMock(S2CProtocol.class));
		when(relationsService.protects(this.mob, target)).thenReturn(true);


		attacker.getPositionalData().setPosition(new SpacedVector3(100, 110, 290));
		target.getPositionalData().setPosition(new SpacedVector3(100, 190, 290));

		aggroingBrain.getSmrtReceiver().combat().entityDamaged(attacker, target, 3, 100, "Foo", MagicSchool.LIGHT);
		assertEquals(attacker, this.mob.getCurrentAggroTarget());
	}

	@Test
	public void testSocialAggroTooFarAway() {
		Mob attacker = mobTemplate2.createMob(timeProvider, uuidFactory.combUUID(), randomProvider);
		when(relationsService.protects(this.mob, target)).thenReturn(true);


		attacker.getPositionalData().setPosition(new SpacedVector3(100, 180, 290));
		target.getPositionalData().setPosition(new SpacedVector3(100, 110, 290));

		aggroingBrain.entityDamaged(attacker, target, 3, 100, "Foo", MagicSchool.LIGHT);
		assertEquals(null, this.mob.getCurrentAggroTarget());
	}

	@Test
	public void dontAggroWhenDead() {
		when(relationsService.hates(mob, target)).thenReturn(true);
		mob.kill();
		target.getPositionalData().setPosition(new SpacedVector3(100, 190, 290));
		EntityData entityData = target.createEntityData();
		aggroingBrain.getSmrtReceiver().entity().entityAppeared(target,
				entityData,
				new HashMap<ContainerType, ItemTemplate>());

		assertEquals(null, mob.getCurrentAggroTarget());
	}
}
