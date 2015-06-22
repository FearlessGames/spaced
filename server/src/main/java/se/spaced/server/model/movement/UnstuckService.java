package se.spaced.server.model.movement;

import se.spaced.server.model.ServerEntity;
import se.spaced.server.persistence.util.transactions.AutoTransaction;

public interface UnstuckService {
	@AutoTransaction
	void unstuck(ServerEntity entity);
}
