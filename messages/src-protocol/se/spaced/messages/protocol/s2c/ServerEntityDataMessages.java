package se.spaced.messages.protocol.s2c;

import se.smrt.core.SmrtProtocol;
import se.spaced.messages.protocol.Entity;
import se.spaced.messages.protocol.ItemTemplate;
import se.spaced.shared.model.PositionalData;
import se.spaced.shared.model.items.ContainerType;
import se.spaced.shared.model.stats.EntityStats;
import se.spaced.shared.network.protocol.codec.datatype.EntityData;

import java.util.Map;

@SmrtProtocol
public interface ServerEntityDataMessages {
	void updateStats(Entity entity, EntityStats stats);

	void entityAppeared(Entity entity, EntityData entityData, Map<ContainerType, ? extends ItemTemplate> items);

	void entityDisappeared(Entity entity);

	void doRespawn(PositionalData positionalData, EntityStats stats);

	void entityDespawned(Entity entity);

	void entityChangedTarget(Entity entity, Entity target);

	void entityClearedTarget(Entity entity);

	void unknownEntityName(String name);

	void changedTarget(Entity newTarget);

	void clearedTarget();
}
