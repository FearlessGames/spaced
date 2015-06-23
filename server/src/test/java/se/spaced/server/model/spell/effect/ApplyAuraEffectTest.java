package se.spaced.server.model.spell.effect;

import org.junit.Before;
import org.junit.Test;
import se.fearless.common.mock.MockUtil;
import se.fearless.common.uuid.UUID;
import se.spaced.messages.protocol.AuraInstance;
import se.spaced.messages.protocol.s2c.S2CProtocol;
import se.spaced.server.ScenarioTestBase;
import se.spaced.server.model.ServerEntity;
import se.spaced.server.model.aura.ModStatAura;
import se.spaced.server.model.aura.ServerAura;
import se.spaced.server.model.spawn.MobTemplate;
import se.spaced.shared.model.MagicSchool;
import se.spaced.shared.model.aura.ModStat;
import se.spaced.shared.model.stats.EntityStats;
import se.spaced.shared.model.stats.Operator;
import se.spaced.shared.model.stats.StatType;

import static org.junit.Assert.*;
import static se.mockachino.Mockachino.getData;
import static se.mockachino.Mockachino.verifyOnce;
import static se.mockachino.matchers.Matchers.any;

public class ApplyAuraEffectTest extends ScenarioTestBase {
	private static final double EPSILON = 1e-10;
	private static final double BUFF_AMOUNT = 10;
	private ModStat modStat;
	private static final UUID AURA_PK = new UUID(644, 677);
	private static final long DURATION = 3000L;
	private ServerAura serverAura;
	private ApplyAuraEffect applyAuraEffect;
	private ServerEntity target;
	private ServerEntity performer;
	private S2CProtocol receiver;
	private S2CProtocol targetReceiver;

	@Before
	public void setup() {
		modStat = new ModStat(BUFF_AMOUNT, StatType.STAMINA, Operator.ADD);
		serverAura = new ModStatAura("Some AuraTemplate", "", DURATION, false, 0, true, modStat);
		serverAura.setPk(AURA_PK);
		applyAuraEffect = new ApplyAuraEffect(smrtBroadcaster,
				MagicSchool.FROST, serverAura,
				auraService);

		MobTemplate template = new MobTemplate.Builder(uuidFactory.randomUUID(), "Mr Mob").build();
		performer = template.createMob(timeProvider, new UUID(523626, 12214), randomProvider);
		receiver = MockUtil.deepMock(S2CProtocol.class);
		targetReceiver = MockUtil.deepMock(S2CProtocol.class);

		entityService.addEntity(performer, receiver);

		target = template.createMob(timeProvider, new UUID(123L, 456L), randomProvider);
		entityService.addEntity(target, targetReceiver);
	}

	@Test
	public void applyAura() {
		double staminaBefore = target.getBaseStats().getStamina().getValue();
		applyAuraEffect.apply(timeProvider.now(), performer, target, "Some spell");

		double staminaAfter = target.getBaseStats().getStamina().getValue();

		assertEquals(BUFF_AMOUNT, staminaAfter - staminaBefore, EPSILON);

		verifyOnce().on(receiver.combat()).gainedAura(target, any(AuraInstance.class));
		verifyOnce().on(targetReceiver.combat()).gainedAura(target, any(AuraInstance.class));
		verifyOnce().on(targetReceiver.entity()).updateStats(target, any(EntityStats.class));
		verifyOnce().on(receiver.entity()).updateStats(target, any(EntityStats.class));
	}

	@Test
	public void removeAura() {
		double staminaBefore = target.getBaseStats().getStamina().getValue();
		applyAuraEffect.apply(timeProvider.now(), performer, target, "Some spell");

		getData(targetReceiver.entity()).resetCalls();
		getData(receiver.entity()).resetCalls();
		assertTrue(staminaBefore < target.getBaseStats().getStamina().getValue());
		tick(5000);
		assertFalse(staminaBefore < target.getBaseStats().getStamina().getValue());

		verifyOnce().on(targetReceiver.combat()).lostAura(target, any(AuraInstance.class));
		verifyOnce().on(receiver.combat()).lostAura(target, any(AuraInstance.class));
		verifyOnce().on(targetReceiver.entity()).updateStats(target, any(EntityStats.class));
		verifyOnce().on(receiver.entity()).updateStats(target, any(EntityStats.class));

	}

}
