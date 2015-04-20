package se.spaced.server.model.aura;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import se.spaced.server.model.ServerEntity;
import se.spaced.server.model.action.ActionScheduler;
import se.spaced.shared.util.ListenerDispatcher;

import java.util.Map;
import java.util.SortedSet;

@Singleton
public class AuraServiceImpl implements AuraService, AuraInstanceRemover {
	private final ActionScheduler actionScheduler;
	private final Map<ServerEntity, ServerEntityAuras> activeAuras = Maps.newConcurrentMap();
	private final ListenerDispatcher<AuraUpdateListener> auraUpdateListener;

	@Inject
	public AuraServiceImpl(
			ActionScheduler actionScheduler,
			ListenerDispatcher<AuraUpdateListener> auraUpdateListener) {
		this.actionScheduler = actionScheduler;
		this.auraUpdateListener = auraUpdateListener;
	}

	@Override
	public boolean hasAura(ServerEntity entity, ServerAura aura) {
		ServerEntityAuras entityAuras = activeAuras.get(entity);
		if (entityAuras == null) {
			return false;
		}
		return entityAuras.hasAura(aura);
	}

	private boolean hasAura(ServerEntity entity, ServerAuraInstance instance) {
		ServerEntityAuras entityAuras = activeAuras.get(entity);
		if (entityAuras == null) {
			return false;
		}
		return entityAuras.hasAura(instance);
	}

	@Override
	public void apply(
			ServerEntity performer,
			final ServerEntity target,
			final ServerAura serverAura,
			final long now) {
		ServerEntityAuras auras = activeAuras.get(target);

		final ServerAuraInstance newAura = new ServerAuraInstance(serverAura, now);
		ServerAuraInstance removed = auras.add(newAura);
		if (removed != null) {
			performRemove(target, removed);
		}

		serverAura.apply(performer, target, actionScheduler, now, newAura, this);
		auraUpdateListener.trigger().gainedAura(target, newAura, performer);
	}

	@Override
	public void removeInstance(ServerEntity target, ServerAura aura) {
		ServerEntityAuras entityAuras = activeAuras.get(target);
		SortedSet<ServerAuraInstance> auras = entityAuras.getInstances(aura);
		if (!auras.isEmpty()) {
			remove(target, auras.first());
		}
	}

	@Override
	public void removeAllInstances(ServerEntity target, ServerAura aura) {
		ServerEntityAuras entityAuras = activeAuras.get(target);
		SortedSet<ServerAuraInstance> auras = entityAuras.getInstances(aura);
		for (ServerAuraInstance serverAuraInstance : auras) {
			remove(target, serverAuraInstance);
		}
	}

	@Override
	public ImmutableSet<ServerAuraInstance> getAllAuras(ServerEntity entity) {
		ServerEntityAuras serverEntityAuras = activeAuras.get(entity);
		if (serverEntityAuras == null) {
			return ImmutableSet.of();
		}
		return ImmutableSet.copyOf(serverEntityAuras.getActiveAuras());
	}

	@Override
	public void remove(ServerEntity target, ServerAuraInstance instance) {
		if (!hasAura(target, instance)) {
			return;
		}
		performRemove(target, instance);
	}

	private void performRemove(ServerEntity target, ServerAuraInstance instance) {
		instance.remove(target);
		ServerEntityAuras entityAuras = activeAuras.get(target);
		entityAuras.remove(instance);
		if (!hasAura(target, instance.getAura())) {
			auraUpdateListener.trigger().lostAura(target, instance);
		}
	}

	@Override
	public void entityAdded(ServerEntity entity) {
		activeAuras.put(entity, new ServerEntityAuras());
	}

	@Override
	public void entityRemoved(ServerEntity entity) {
		activeAuras.remove(entity);
	}
}
