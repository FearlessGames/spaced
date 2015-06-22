package se.spaced.server.model.spell;

import org.junit.Before;
import org.junit.Test;
import se.fearlessgames.common.util.MockTimeProvider;
import se.fearlessgames.common.util.uuid.UUIDFactory;
import se.fearlessgames.common.util.uuid.UUIDFactoryImpl;
import se.spaced.messages.protocol.s2c.S2CProtocol;
import se.spaced.server.model.ServerEntity;
import se.spaced.server.model.combat.CombatRepository;
import se.spaced.server.model.combat.SpellCombatService;
import se.spaced.server.model.entity.AppearanceService;
import se.spaced.server.model.entity.EntityService;
import se.spaced.server.model.entity.VisibilityService;
import se.spaced.server.model.entity.VisibilityServiceImpl;
import se.spaced.server.model.spell.effect.DamageSchoolEffect;
import se.spaced.server.net.broadcast.SmrtBroadcaster;
import se.spaced.server.net.broadcast.SmrtBroadcasterImpl;
import se.spaced.shared.model.MagicSchool;
import se.spaced.shared.util.ListenerDispatcher;
import se.spaced.shared.util.math.interval.IntervalInt;

import java.security.SecureRandom;

import static se.mockachino.Mockachino.*;
import static se.mockachino.matchers.Matchers.*;


public class SpellTest {
	private final MockTimeProvider timeProvider = new MockTimeProvider();
	private final UUIDFactory uuidFactory = new UUIDFactoryImpl(timeProvider, new SecureRandom());

	private static final int PROJECTILE_ID = 42;

	private VisibilityService visibilityService;

	@Before
	public void setup() {
		setupMocks(this);
	}

	@Test
	public void perform() {
		SpellCombatService spellCombatService = mock(SpellCombatService.class);
		EntityService entityService = mock(EntityService.class);
		CombatRepository combatRepo = mock(CombatRepository.class);
		visibilityService = new VisibilityServiceImpl(entityService, ListenerDispatcher.create(AppearanceService.class));
		SmrtBroadcaster<S2CProtocol> broadCaster = new SmrtBroadcasterImpl(entityService, combatRepo, visibilityService);
		ServerSpell spell = ServerSpell.createDirectEffectSpell(uuidFactory.randomUUID(), "Nuke", 1000, MagicSchool.FIRE, new DamageSchoolEffect(spellCombatService, broadCaster, new IntervalInt(10, 12), MagicSchool.FIRE),
				true, true, new IntervalInt(0, 100));
		ServerEntity attacker = mock(ServerEntity.class);
		ServerEntity target = mock(ServerEntity.class);

		MockTimeProvider time = new MockTimeProvider();

		spell.perform(timeProvider.now(), attacker, target);

		verifyOnce().on(spellCombatService).doDamage(eq(attacker), eq(target), eq(timeProvider.now()), any(DamageSchoolEffect.class), eq(spell.getName()));
	}
}
