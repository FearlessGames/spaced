package se.spaced.server.model.cooldown;

import org.junit.Test;
import se.spaced.server.ScenarioTestBase;
import se.spaced.server.model.Mob;
import se.spaced.server.model.action.SpellListener;
import se.spaced.server.model.spawn.MobTemplate;
import se.spaced.server.model.spell.ServerSpell;
import se.spaced.server.model.spell.effect.Effect;
import se.spaced.shared.model.MagicSchool;
import se.spaced.shared.util.math.interval.IntervalInt;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static se.mockachino.Mockachino.*;

public class CooldownTest extends ScenarioTestBase {


	@Test
	public void testSimpleCooldown() {
		CooldownTemplate template = new CooldownTemplate(1.5, "cooldown");
		final ServerCooldown cooldown = template.createCooldown(timeProvider.now());
		assertTrue(cooldown.isReady(timeProvider.now()));
		cooldown.consume(timeProvider.now());
		assertFalse(cooldown.isReady(timeProvider.now()));
	}

	@Test
	public void testSimpleCooldownScenario() {
		CooldownTemplate cooldownTemplate = new CooldownTemplate(1.5, "cooldown");
		cooldownTemplate.setPk(uuidFactory.randomUUID());
		final ServerSpell spell = ServerSpell.createDirectEffectSpell(
				uuidFactory.randomUUID(),
				"",
				100,
				MagicSchool.ELECTRICITY,
				mock(Effect.class),
				false,
				false,
				new IntervalInt(0, 0),
				cooldownTemplate);

		MobTemplate template = new MobTemplate.Builder(uuidFactory.randomUUID(), "Foo").build();

		Mob caster = template.createMob(timeProvider, uuidFactory.randomUUID(), randomProvider);
		Mob target = template.createMob(timeProvider, uuidFactory.randomUUID(), randomProvider);
		visibilityService.entityAdded(caster);
		visibilityService.entityAdded(target);

		final ServerCooldown cooldown = caster.getCooldown(spell, timeProvider.now());

		assertTrue(cooldown.isReady(timeProvider.now()));
		assertNull(currentActionService.getCurrentAction(caster));

		spellCombatService.startSpellCast(caster, target,
				spell, timeProvider.now(), mock(SpellListener.class));

		assertFalse(cooldown.isReady(timeProvider.now()));
		assertNotNull(currentActionService.getCurrentAction(caster));

		tick(1499);

		assertFalse(cooldown.isReady(timeProvider.now()));
		assertNull(currentActionService.getCurrentAction(caster));

		spellCombatService.startSpellCast(caster, target,
				spell, timeProvider.now(), mock(SpellListener.class));

		assertNull(currentActionService.getCurrentAction(caster));

		tick(1);

		assertTrue(cooldown.isReady(timeProvider.now()));
		assertNull(currentActionService.getCurrentAction(caster));

		spellCombatService.startSpellCast(caster, target,
				spell, timeProvider.now(), mock(SpellListener.class));

		assertFalse(cooldown.isReady(timeProvider.now()));
		assertNotNull(currentActionService.getCurrentAction(caster));

	}
}
