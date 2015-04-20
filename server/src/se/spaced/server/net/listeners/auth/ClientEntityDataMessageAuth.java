package se.spaced.server.net.listeners.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.spaced.messages.protocol.Entity;
import se.spaced.messages.protocol.c2s.ClientEntityDataMessages;
import se.spaced.server.model.Player;
import se.spaced.server.model.ServerEntity;
import se.spaced.server.model.combat.EntityCombatService;
import se.spaced.server.model.combat.EntityTargetService;
import se.spaced.server.model.entity.EntityService;
import se.spaced.server.model.entity.VisibilityService;
import se.spaced.server.model.movement.UnstuckService;
import se.spaced.server.net.ClientConnection;

import java.util.concurrent.atomic.AtomicInteger;

public class ClientEntityDataMessageAuth implements ClientEntityDataMessages {
	private final Logger log = LoggerFactory.getLogger(getClass());
	private static final AtomicInteger NUMBER_OF_WHO_REQUESTS = new AtomicInteger(0);
	
	private final ClientConnection clientConnection;
	private final EntityTargetService entityTargetService;
	private final EntityCombatService entityCombatService;
	private final EntityService entityService;
	private final UnstuckService unstuckService;
	private final VisibilityService visibilityService;

	public ClientEntityDataMessageAuth(
			ClientConnection clientConnection,
			EntityTargetService entityTargetService,
			EntityCombatService entityCombatService,
			EntityService entityService,
			UnstuckService unstuckService,
			VisibilityService visibilityService) {
		this.clientConnection = clientConnection;
		this.entityTargetService = entityTargetService;
		this.entityCombatService = entityCombatService;
		this.entityService = entityService;
		this.unstuckService = unstuckService;
		this.visibilityService = visibilityService;
	}

	@Override
	public void whoRequest(Entity entity) {
		int requestsSoFar = NUMBER_OF_WHO_REQUESTS.incrementAndGet();
		log.warn("{} whoreQuests so far", requestsSoFar);
		if (entity != null) {
			ServerEntity serverEntity = entityService.getEntity(entity.getPk());
			if (serverEntity != null) {
				visibilityService.forceConnect(clientConnection.getPlayer(), serverEntity);
			} else {
				//clientConnection.getReceiver().entity().unknownEntityName();
			}
		} else {
			log.error("Tried to lookup an unknown entity");
		}
	}

	@Override
	public void requestResurrection() {
		ServerEntity entity = clientConnection.getPlayer();
		log.debug("Got a res request from {}", clientConnection.getPlayer());

		if (entity != null) {
			entityCombatService.respawnWithHealth(entity, (int) entity.getBaseStats().getMaxHealth().getValue());
		} else {
			log.error("could not find entity when resurrecting ");
		}
	}

	@Override
	public void setTarget(Entity entity) {
		if (entity != null) {
			Player player = clientConnection.getPlayer();
			entityTargetService.setTarget(player, (ServerEntity) entity);
		} else {
			log.info("Tried to target an entity that was no longer logged in");
		}
	}

	@Override
	public void clearTarget() {
		Player player = clientConnection.getPlayer();
		entityTargetService.clearTarget(player);
	}

	@Override
	public void unstuck() {
		ServerEntity entity = clientConnection.getPlayer();
		log.info("Got a unstuck request from {}", clientConnection.getPlayer());

		if (entity != null) {
			unstuckService.unstuck(entity);
		} else {
			log.error("could not find entity when resurrecting ");
}
	}
}
