package se.spaced.client.view;

import se.spaced.client.model.ClientEntity;
import se.spaced.client.model.ClientSpell;

public interface AbilityView {
	void startAbilityCharge(ClientEntity source, ClientEntity target, ClientSpell spell);

	void stopAbilityCharge(ClientEntity source, ClientSpell spell);

	void startEffectApplied(ClientEntity target, ClientEntity to, String effectResource);

	void startAbilityProjectile(int projectileId, ClientEntity performer, ClientEntity target, String effectResource, double speed);
}
