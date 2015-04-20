package se.spaced.server.model.relations;

import se.spaced.server.model.ServerEntity;

public interface RelationsService {
	boolean protects(ServerEntity entity, ServerEntity target);

	boolean hates(ServerEntity entity, ServerEntity target);
}
