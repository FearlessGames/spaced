package se.spaced.server.model.aura;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.Collection;
import java.util.SortedSet;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListSet;

public class ServerEntityAuras {
	private final ConcurrentMap<ServerAura, SortedSet<ServerAuraInstance>> auras = Maps.newConcurrentMap();

	public ServerEntityAuras() {
	}

	public boolean hasAura(ServerAura aura) {
		Collection<ServerAuraInstance> instances = auras.get(aura);
		if (instances == null) {
			return false;
		}
		return !instances.isEmpty();
	}

	public boolean hasAura(ServerAuraInstance instance) {
		SortedSet<ServerAuraInstance> instances = auras.get(instance.getAura());
		if (instances == null) {
			return false;
		}
		return instances.contains(instance);
	}

	public ServerAuraInstance add(ServerAuraInstance instance) {
		ServerAuraInstance removed = null;
		ServerAura aura = instance.getAura();
		SortedSet<ServerAuraInstance> instances = auras.get(aura);
		if (instances == null) {
			instances = new ConcurrentSkipListSet<ServerAuraInstance>();
			SortedSet<ServerAuraInstance> oldValue = auras.putIfAbsent(aura,
					instances);
			if (oldValue != null) {
				instances = oldValue;
			}
		}
		int maxStack = aura.getMaxStacks();
		if (instances.size() >= maxStack && maxStack > 0) {
			removed = instances.first();
			instances.remove(removed);
		}
		instances.add(instance);
		return removed;
	}

	public void remove(ServerAuraInstance aura) {
		SortedSet<ServerAuraInstance> instances = auras.get(aura.getAura());
		if (instances != null) {
			instances.remove(aura);
		}
	}

	public SortedSet<ServerAuraInstance> getInstances(ServerAura aura) {
		SortedSet<ServerAuraInstance> instances = auras.get(aura);
		if (instances == null) {
			return new ConcurrentSkipListSet<ServerAuraInstance>();
		}
		return instances;
	}

	public Collection<ServerAuraInstance> getActiveAuras() {
		return Lists.newArrayList(Iterables.concat(auras.values()));
	}
}
