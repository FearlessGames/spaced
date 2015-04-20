package se.spaced.server.model.aura;

import com.google.common.collect.ImmutableSet;
import se.spaced.server.model.ServerEntity;
import se.spaced.server.model.entity.EntityServiceListener;

public interface AuraService extends EntityServiceListener {
	boolean hasAura(ServerEntity entity, ServerAura aura);

	void apply(ServerEntity performer, ServerEntity target, ServerAura aura, long now);

	void removeInstance(ServerEntity target, ServerAura aura);

	void removeAllInstances(ServerEntity target, ServerAura aura);

	ImmutableSet<ServerAuraInstance> getAllAuras(ServerEntity entity);
}
