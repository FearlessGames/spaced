package se.spaced.server.model.combat;

import org.junit.Before;
import org.junit.Test;
import se.fearless.common.time.MockTimeProvider;
import se.fearless.common.uuid.UUIDMockFactory;
import se.spaced.server.model.Player;
import se.spaced.server.model.aura.AuraService;
import se.spaced.server.model.aura.ServerAura;
import se.spaced.server.model.player.PlayerMockFactory;
import se.spaced.server.model.spell.ServerSpell;
import se.spaced.shared.util.random.RealRandomProvider;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static se.mockachino.Mockachino.mock;
import static se.mockachino.Mockachino.when;

public class SimpleCombatMechanicsTest {

	private CombatMechanics mechanics;
	private PlayerMockFactory playerMockFactory;
	private Player attacker;
	private Player target;
	private RealRandomProvider randomProvider;
	private MockTimeProvider timeProvider;
	private ServerSpell spell;
	private CurrentActionService currentActionService;
	private AuraService auraService;


	@Before
	public void setUp() throws Exception {
		randomProvider = new RealRandomProvider();
		auraService = mock(AuraService.class);
		mechanics = new SimpleCombatMechanics(randomProvider, auraService);
		timeProvider = new MockTimeProvider();
		playerMockFactory = new PlayerMockFactory.Builder(timeProvider, new UUIDMockFactory()).build();
		attacker = playerMockFactory.createPlayer("alice");
		target = playerMockFactory.createPlayer("alice");

		spell = new ServerSpell.Builder("TestSpell").requiresHostileTarget(false).ranges(0, Integer.MAX_VALUE).build();
		currentActionService = mock(CurrentActionService.class);
	}

	@Test
	public void testIsAllowedToAttackWhenTargetIsDead() throws Exception {
		target.kill();
		assertFalse(mechanics.isAllowedToAttack(attacker, target));
	}

	@Test
	public void testIsAllowedToStartCast() throws Exception {
		target.kill();
		assertFalse(mechanics.isAllowedToStartCast(attacker, target,
				spell, currentActionService, timeProvider.now()));
	}

	@Test
	public void testIsAllowedToCompleteCast() throws Exception {
		target.kill();
		assertFalse(mechanics.isAllowedToCompleteCast(attacker,
				target,
				spell,
				mock(CurrentActionService.class)));
	}

	@Test
	public void isAllowedToStartRequiredAuraMissing() throws Exception {
		ServerAura aura = mock(ServerAura.class);
		ServerSpell testSpell = new ServerSpell.Builder("TestSpell").requiresHostileTarget(false).ranges(0, Integer.MAX_VALUE).requiredAuras(aura).build();
		assertFalse(mechanics.isAllowedToStartCast(attacker, target, testSpell, currentActionService, timeProvider.now()));
	}

	@Test
	public void isAllowedToStartRequiredAuraExists() throws Exception {
		ServerAura aura = mock(ServerAura.class);
		ServerSpell testSpell = new ServerSpell.Builder("TestSpell").requiresHostileTarget(false).ranges(0, Integer.MAX_VALUE).requiredAuras(aura).build();
		when(auraService.hasAura(attacker, aura)).thenReturn(true);
		assertTrue(mechanics.isAllowedToStartCast(attacker, target, testSpell, currentActionService, timeProvider.now()));
	}

	@Test
	public void isAllowedToCompleteRequiredAuraMissing() throws Exception {
		ServerAura aura = mock(ServerAura.class);
		ServerSpell testSpell = new ServerSpell.Builder("TestSpell").requiresHostileTarget(false).ranges(0, Integer.MAX_VALUE).requiredAuras(aura).build();
		assertFalse(mechanics.isAllowedToCompleteCast(attacker, target, testSpell, currentActionService));
	}

	@Test
	public void isAllowedToCompleteRequiredAuraExists() throws Exception {
		ServerAura aura = mock(ServerAura.class);
		ServerSpell testSpell = new ServerSpell.Builder("TestSpell").requiresHostileTarget(false).ranges(0, Integer.MAX_VALUE).requiredAuras(aura).build();
		when(auraService.hasAura(attacker, aura)).thenReturn(true);
		assertTrue(mechanics.isAllowedToCompleteCast(attacker, target, testSpell, currentActionService));
	}

}
