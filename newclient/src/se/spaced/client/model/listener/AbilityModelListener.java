package se.spaced.client.model.listener;

import se.spaced.client.model.ClientEntity;
import se.spaced.client.model.ClientSpell;
import se.spaced.messages.protocol.AuraTemplate;

public interface AbilityModelListener {
	void abilityStarted(ClientEntity source, ClientEntity target, ClientSpell spell);

	void abilityCompleted(ClientEntity source, ClientEntity target, ClientSpell spell);

	void abilityStopped(ClientEntity source, ClientSpell spell);

	void homingProjectileCreated(int projectileId, ClientEntity performer, ClientEntity target, String effectResource, double speed);

	void effectApplied(ClientEntity entity, ClientEntity clientEntity, String resource);

	void auraGained(ClientEntity entity, AuraTemplate aura);

	void auraLost(ClientEntity entity, AuraTemplate aura);
}
