package se.spaced.server.model.spell.effect;

import org.junit.Before;
import org.junit.Test;
import se.fearless.common.time.MockTimeProvider;
import se.fearless.common.time.TimeProvider;
import se.fearless.common.uuid.UUIDMockFactory;
import se.mockachino.Mockachino;
import se.mockachino.Settings;
import se.spaced.messages.protocol.s2c.S2CProtocol;
import se.spaced.server.model.ServerEntity;
import se.spaced.server.model.player.PlayerMockFactory;
import se.spaced.server.model.spell.ServerSpell;
import se.spaced.server.net.broadcast.SmrtBroadcaster;
import se.spaced.server.net.broadcast.SmrtBroadcasterImpl;
import se.spaced.server.spell.SpellService;

import static se.mockachino.Mockachino.*;

public class GrantSpellEffectTest {
	private SmrtBroadcaster<S2CProtocol> broadcaster;
	private SpellService spellService;
	private TimeProvider timeProvider;

	@Before
	@SuppressWarnings("unchecked")
	public void setup() {
		timeProvider = new MockTimeProvider();

		//entityService = mock(EntityService.class);
		broadcaster = mock(SmrtBroadcasterImpl.class, Settings.fallback(Mockachino.DEEP_MOCK));
		spellService = mock(SpellService.class);
	}

	@Test
	public void testGrant() {
		ServerSpell spell = mock(ServerSpell.class);
		GrantSpellEffect grant = new GrantSpellEffect(broadcaster, spellService, spell);



		PlayerMockFactory playerMockFactory = new PlayerMockFactory.Builder(timeProvider, new UUIDMockFactory()).build();
		ServerEntity performer = playerMockFactory.createPlayer("Performer");
		ServerEntity target = playerMockFactory.createPlayer("Target");

		grant.apply(0, performer, target, "Just because!");

		verifyOnce().on(spellService).addSpellForEntity(target, spell);
		verifyNever().on(spellService).addSpellForEntity(performer, spell);

		verifyOnce().on(broadcaster.create().to(target).send().spell()).spellAdded(spell);
	}
}
