package se.spaced.server.model.combat;

import se.spaced.server.model.ServerEntity;
import se.spaced.server.model.entity.EntityServiceListener;

import java.util.Set;

public interface EntityTargetService extends EntityServiceListener {

	void setTarget(ServerEntity targetingEntity, ServerEntity newTarget);

	ServerEntity getCurrentTarget(ServerEntity performer);

	void clearTarget(ServerEntity targetingEntity);

	Set<ServerEntity> getEntitiesTargeting(ServerEntity target);
}
