package se.spaced.client.net.messagelisteners;

import se.spaced.client.model.ClientEntity;
import se.spaced.client.model.ClientSpell;

public interface EntityEntitySpellJob {
	void run(ClientEntity first, ClientEntity second, ClientSpell spell);
}