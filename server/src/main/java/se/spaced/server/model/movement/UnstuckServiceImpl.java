package se.spaced.server.model.movement;

import com.google.inject.Inject;
import se.fearlessgames.common.util.TimeProvider;
import se.spaced.server.model.ServerEntity;
import se.spaced.server.persistence.dao.impl.hibernate.GraveyardTemplate;
import se.spaced.server.persistence.util.transactions.AutoTransaction;
import se.spaced.server.services.GraveyardService;

public class UnstuckServiceImpl implements UnstuckService {

	private final GraveyardService graveyardService;
	private final MovementService movementService;
	private final TimeProvider timeProvider;

	@Inject
	public UnstuckServiceImpl(
			GraveyardService graveyardService,
			MovementService movementService,
			TimeProvider timeProvider) {
		this.graveyardService = graveyardService;
		this.movementService = movementService;
		this.timeProvider = timeProvider;
	}

	@Override
	@AutoTransaction
	public void unstuck(ServerEntity entity) {
		GraveyardTemplate closestGraveyard = graveyardService.getClosestGraveyard(entity.getPosition());
		movementService.teleportEntity(entity, closestGraveyard.getSpawnPoint().getPosition(), closestGraveyard.getSpawnPoint().getRotation(),
				timeProvider.now());
	}
}
