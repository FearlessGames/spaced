package se.spaced.server.stats;

import org.junit.Before;
import org.junit.Test;
import se.fearless.common.time.MockTimeProvider;
import se.fearless.common.uuid.UUIDMockFactory;
import se.spaced.messages.protocol.s2c.S2CProtocol;
import se.spaced.server.model.Player;
import se.spaced.server.model.player.PlayerMockFactory;
import se.spaced.server.model.spell.ServerSpell;
import se.spaced.server.model.spell.effect.DamageSchoolEffect;
import se.spaced.server.net.broadcast.SmrtBroadcaster;
import se.spaced.shared.model.MagicSchool;
import se.spaced.shared.statistics.EventLogger;
import se.spaced.shared.util.math.interval.IntervalInt;

import static se.mockachino.Mockachino.mock;
import static se.mockachino.Mockachino.verifyOnce;

public class SpellStatisticsCollectorTest {
	SpellStatisticsCollector spellStatisticsCollector;
	SmrtBroadcaster<S2CProtocol> broadcaster;
	private EventLogger eventLogger;
	private PlayerMockFactory playerMockFactory;
	private UUIDMockFactory uuidFactory;

	@Before
	public void setUp() throws Exception {
		broadcaster = mock(SmrtBroadcaster.class);
		eventLogger = mock(EventLogger.class);
		spellStatisticsCollector = new SpellStatisticsCollector(broadcaster, eventLogger);
		uuidFactory = new UUIDMockFactory();
		playerMockFactory = new PlayerMockFactory.Builder(new MockTimeProvider(), uuidFactory).build();
	}

	@Test
	public void testEntityStartedSpellCastAndCompletedIt() throws Exception {
		String playerName = "hiflyer";
		String targetName = "krka";
		String spellName = "Electric Shock";
		Player performer = playerMockFactory.createPlayer(playerName);
		Player target = playerMockFactory.createPlayer(targetName);
		DamageSchoolEffect damageSchoolEffect = mock(DamageSchoolEffect.class);
		ServerSpell spell = ServerSpell.createDirectEffectSpell(uuidFactory.combUUID(), spellName, 300, MagicSchool.ELECTRICITY, damageSchoolEffect, true, true, new IntervalInt(3, 5));

		spellStatisticsCollector.entityStartedSpellCast(performer, target, spell);
		spellStatisticsCollector.entityCompletedSpellCast(performer, target, spell);

		verifyOnce().on(eventLogger).log("SPELLCAST_COMPLETED", playerName, performer.getPk().toString(), performer.getTemplate().getPk().toString(),
				targetName, target.getPk().toString(), target.getTemplate().getPk().toString(), spellName, spell.getPk().toString());
	}


}
