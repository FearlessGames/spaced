package se.spaced.server.model.action;

import com.google.common.collect.Sets;
import org.junit.Before;
import org.junit.Test;
import se.fearless.common.mock.MockUtil;
import se.spaced.messages.protocol.s2c.S2CProtocol;
import se.spaced.server.ScenarioTestBase;
import se.spaced.server.model.Mob;
import se.spaced.server.model.Player;
import se.spaced.server.model.player.PlayerMockFactory;
import se.spaced.server.model.spawn.MobTemplate;
import se.spaced.server.model.spell.ServerSpell;
import se.spaced.server.model.spell.effect.DamageSchoolEffect;
import se.spaced.server.model.spell.effect.Effect;
import se.spaced.server.model.spell.effect.ProjectileEffect;
import se.spaced.shared.model.MagicSchool;
import se.spaced.shared.util.math.interval.IntervalInt;

import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import static se.mockachino.Mockachino.*;
import static se.mockachino.matchers.Matchers.type;


public class SpellActionTest extends ScenarioTestBase {
	private final AtomicInteger projectileIdCounter = new AtomicInteger();

	private Player attacker;
	private Mob target;

	private SpellAction spellAction;
	private static final long EXECUTION_TIME = 3000L;
	private ServerSpell spell;
	private S2CProtocol combatSpy;

	@Before
	public void setup() {
		PlayerMockFactory factory = new PlayerMockFactory.Builder(timeProvider, uuidFactory).build();
		attacker = factory.createPlayer("Nisse");

		MobTemplate mobTemplate = new MobTemplate.Builder(uuidFactory.randomUUID(), "Kalle").stamina(100).build();
		target = mobTemplate.createMob(timeProvider, uuidFactory.randomUUID(), randomProvider);
		visibilityService.entityAdded(attacker);
		visibilityService.entityAdded(target);

		stubReturn(target).on(entityTargetService).getCurrentTarget(attacker);
		combatSpy = MockUtil.deepMock(S2CProtocol.class);
		smrtBroadcaster.addSpy(combatSpy);

		Set<Effect> effects = Sets.newHashSet();
		ProjectileEffect projectileEffect = new ProjectileEffect(actionScheduler,
				smrtBroadcaster,
				projectileIdCounter
		);
		projectileEffect.addImpactEffect(new DamageSchoolEffect(spellCombatService,
				smrtBroadcaster,
				new IntervalInt(10, 12),
				MagicSchool.FIRE
		));
		effects.add(projectileEffect);
		spell = ServerSpell.createProjectileSpell(uuidFactory.randomUUID(),
				"Nuke",
				3000,
				MagicSchool.FIRE,
				effects,
				true,
				true,
				new IntervalInt(0, 100),
				1,
				actionScheduler,
				smrtBroadcaster,
				projectileIdCounter
		);
		spellAction = new SpellAction(combatMechanics,
				EXECUTION_TIME,
				attacker,
				target,
				spell,
				currentActionService,
				smrtBroadcaster,
				mock(SpellListener.class)
		);
	}

	@Test
	public void performSpawnsProjectile() {
		spellAction.perform();
		verifyOnce().on(actionScheduler).add(type(ProjectileAction.class));
	}


	@Test
	public void movementAffectsSpell() {
		spellAction.performerMoved();
		verifyOnce().on(currentActionService).cancelCurrentAction(attacker);
	}

	@Test
	public void spellCompletedDoesNotTriggerSpellStopped() {
		spellAction.perform();
		verifyOnce().on(currentActionService).clearCurrentAction(attacker);
		verifyNever().on(combatSpy.combat()).entityStoppedSpellCast(attacker, spell);
	}
}