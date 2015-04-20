package se.spaced.server.net.listeners.auth;

import com.google.common.collect.Lists;
import org.junit.Test;
import se.fearlessgames.common.mock.MockUtil;
import se.fearlessgames.common.util.uuid.UUID;
import se.spaced.messages.protocol.Spell;
import se.spaced.messages.protocol.s2c.S2CProtocol;
import se.spaced.server.model.spell.ServerSpell;
import se.spaced.server.net.ClientConnection;
import se.spaced.server.spell.SpellService;
import se.spaced.shared.network.protocol.codec.datatype.SpellData;

import java.util.List;

import static se.mockachino.Mockachino.*;

public class ClientSpellMessagesAuthTest {
	@Test
	public void testRequestSpellInfoWithUnknownSpellId() throws Exception {
		ClientConnection clientConnection = mock(ClientConnection.class);
		SpellService spellService = mock(SpellService.class);
		S2CProtocol receiver = MockUtil.deepMock(S2CProtocol.class);
		stubReturn(receiver).on(clientConnection).getReceiver();
		ClientSpellMessagesAuth spellMessagesAuth = new ClientSpellMessagesAuth(clientConnection, spellService);

		Spell serverSpell = new ServerSpell.Builder("Foo").uuid(new UUID(123456, 98765)).build();
		List<? extends Spell> spellList = Lists.newArrayList(serverSpell);
		spellMessagesAuth.requestSpellInfo(spellList);
		verifyOnce().on(receiver.spell()).spellData(Lists.<SpellData>newArrayList());
	}
}
