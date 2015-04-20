package se.spaced.server.net.listeners.auth;

import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.spaced.messages.protocol.Spell;
import se.spaced.messages.protocol.c2s.ClientSpellMessages;
import se.spaced.server.model.Player;
import se.spaced.server.model.spell.ServerSpell;
import se.spaced.server.model.spell.SpellDataFactory;
import se.spaced.server.net.ClientConnection;
import se.spaced.server.spell.SpellService;
import se.spaced.shared.network.protocol.codec.datatype.SpellData;

import java.util.Collection;
import java.util.List;

public class ClientSpellMessagesAuth implements ClientSpellMessages {
	private final Logger log = LoggerFactory.getLogger(getClass());
	private final ClientConnection clientConnection;
	private final SpellService spellService;

	public ClientSpellMessagesAuth(ClientConnection clientConnection, SpellService spellService) {
		this.clientConnection = clientConnection;
		this.spellService = spellService;
	}

	@Override
	public void requestSpellBook() {
		Player player = clientConnection.getPlayer();

		Collection<ServerSpell> spellCollection = spellService.getSpellsForEntity(player);
		clientConnection.getReceiver().spell().spellBookInfo(spellCollection);
	}

	@Override
	public void requestSpellInfo(List<? extends Spell> dummy) {
		Collection<SpellData> answer = Lists.newArrayList();
		for (Spell spell : dummy) {
			ServerSpell storedSpell = spellService.getSpellById(spell.getPk());
			if (storedSpell != null) {
				answer.add(SpellDataFactory.createSpellData(storedSpell));
			}
		}
		clientConnection.getReceiver().spell().spellData(answer);
	}
}
