package se.spaced.server.model.combat;

import se.spaced.server.model.ServerEntity;

public interface DeathService {
	void respawn(ServerEntity entity);

	void kill(ServerEntity entity);
}
