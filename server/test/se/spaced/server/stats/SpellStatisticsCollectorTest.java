package se.spaced.server.stats;

import org.junit.Before;
import org.junit.Test;
import se.spaced.messages.protocol.s2c.S2CProtocol;
import se.spaced.server.model.Player;
import se.spaced.server.model.spell.ServerSpell;
import se.spaced.server.net.broadcast.SmrtBroadcaster;
import se.spaced.server.persistence.dao.interfaces.SpellActionEntryDao;

import static se.mockachino.Mockachino.*;
import static se.mockachino.matchers.Matchers.*;

public class SpellStatisticsCollectorTest {
	SpellStatisticsCollector spellStatisticsCollector;
	SmrtBroadcaster<S2CProtocol> broadcaster;
	private SpellActionEntryDao spellActionEntryDao;

	@Before
	public void setUp() throws Exception {
		broadcaster = mock(SmrtBroadcaster.class);
		spellActionEntryDao = mock(SpellActionEntryDao.class);
		spellStatisticsCollector = new SpellStatisticsCollector(broadcaster, spellActionEntryDao);
	}

	@Test
	public void testEntityStartedSpellCastAndCompletedIt() throws Exception {
		Player performer = mock(Player.class);
		Player target = mock(Player.class);
		ServerSpell spell = mock(ServerSpell.class);

		spellStatisticsCollector.entityStartedSpellCast(performer, target, spell);
		spellStatisticsCollector.entityCompletedSpellCast(performer, target, spell);

		verifyOnce().on(spellActionEntryDao).persist(any(SpellActionEntry.class));
	}


}
