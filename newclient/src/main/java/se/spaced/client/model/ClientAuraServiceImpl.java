package se.spaced.client.model;

import com.google.common.base.Predicate;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;
import se.spaced.client.model.player.PlayerEntityProvider;
import se.spaced.messages.protocol.ClientAuraInstance;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class ClientAuraServiceImpl implements ClientAuraService {
	private final Multimap<ClientEntity, ClientAuraInstance> auras = HashMultimap.create();
	private final PlayerEntityProvider playerEntityProvider;

	@Inject
	public ClientAuraServiceImpl(PlayerEntityProvider playerEntityProvider) {
		this.playerEntityProvider = playerEntityProvider;
	}

	@Override
	public boolean selfHasAura(ClientAuraInstance aura) {
		return auras.get(playerEntityProvider.get()).contains(aura);
	}

	@Override
	public void applyAura(ClientEntity entity, ClientAuraInstance aura) {
		auras.put(entity, aura);
	}

	@Override
	public void removeAura(ClientEntity entity, ClientAuraInstance aura) {
		auras.remove(entity, aura);
	}

	@Override
	public ImmutableSet<ClientAuraInstance> getAuras(ClientEntity entity) {
		return ImmutableSet.copyOf(auras.get(entity));
	}

	@Override
	public ImmutableSet<ClientAuraInstance> getVisibleAuras(ClientEntity entity) {
		return ImmutableSet.copyOf(Iterables.filter(getAuras(entity), new VisibleAuraPredicate()));
	}

	private static class VisibleAuraPredicate implements Predicate<ClientAuraInstance> {
		@Override
		public boolean apply(ClientAuraInstance clientAuraTemplate) {
			return clientAuraTemplate.isVisible();
		}
	}
}
