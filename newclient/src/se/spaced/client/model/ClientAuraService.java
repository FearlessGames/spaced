package se.spaced.client.model;

import com.google.common.collect.ImmutableSet;
import se.spaced.messages.protocol.ClientAuraInstance;

public interface ClientAuraService {
	boolean selfHasAura(ClientAuraInstance aura);

	void applyAura(ClientEntity entity, ClientAuraInstance aura);

	void removeAura(ClientEntity entity, ClientAuraInstance aura);

	ImmutableSet<ClientAuraInstance> getAuras(ClientEntity entity);

	ImmutableSet<ClientAuraInstance> getVisibleAuras(ClientEntity entity);
}
