package se.spaced.client.net.messagelisteners;

import se.spaced.client.model.ClientSpell;
import se.spaced.client.net.smrt.ServerConnection;
import se.spaced.messages.protocol.Spell;
import se.spaced.shared.activecache.ActiveCacheImpl;
import se.spaced.shared.activecache.KeyRequestHandler;

import java.util.Arrays;

public class SpellCacheImpl extends ActiveCacheImpl<Spell, ClientSpell> {
	public SpellCacheImpl(final ServerConnection serverConnection) {
		super(new KeyRequestHandler<Spell>() {
			@Override
			public void requestKey(Spell key) {
				serverConnection.getReceiver().spell().requestSpellInfo(Arrays.asList(key));
			}
		});
	}
}