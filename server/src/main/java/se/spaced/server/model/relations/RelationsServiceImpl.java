package se.spaced.server.model.relations;

import se.spaced.server.model.ServerEntity;

public class RelationsServiceImpl implements RelationsService {

	// TODO: extremely simplified hate management. Small steps, remember? ^^

	@Override
	public boolean protects(ServerEntity entity, ServerEntity target) {
		return entity.getFaction().equals(target.getFaction());
	}

	@Override
	public boolean hates(ServerEntity entity, ServerEntity target) {
		return !entity.getFaction().equals(target.getFaction());
	}
}
