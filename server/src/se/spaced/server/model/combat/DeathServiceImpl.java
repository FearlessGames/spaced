package se.spaced.server.model.combat;

import se.spaced.messages.protocol.s2c.S2CProtocol;
import se.spaced.server.model.PersistedPositionalData;
import se.spaced.server.model.ServerEntity;
import se.spaced.server.model.aura.AuraService;
import se.spaced.server.model.aura.ServerAura;
import se.spaced.server.model.aura.ServerAuraInstance;
import se.spaced.server.net.broadcast.SmrtBroadcaster;
import se.spaced.server.persistence.dao.impl.hibernate.GraveyardTemplate;
import se.spaced.server.persistence.util.transactions.AutoTransaction;
import se.spaced.server.services.GraveyardService;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class DeathServiceImpl implements DeathService {
	private final GraveyardService graveyardService;
	private final SmrtBroadcaster<S2CProtocol> broadcaster;
	private final AuraService auraService;
	private final OutOfCombatRegenService outOfCombatRegenService;

	@Inject
	public DeathServiceImpl(
			SmrtBroadcaster<S2CProtocol> broadcaster,
			AuraService auraService, GraveyardService graveyardService, OutOfCombatRegenService outOfCombatRegenService) {
		this.broadcaster = broadcaster;
		this.auraService = auraService;
		this.graveyardService = graveyardService;
		this.outOfCombatRegenService = outOfCombatRegenService;
	}

	@Override
	@AutoTransaction
	public void respawn(ServerEntity entity) {
		GraveyardTemplate closestGraveyard = graveyardService.getClosestGraveyard(entity.getPosition());
		entity.setPositionalData(new PersistedPositionalData(closestGraveyard.getSpawnPoint().getPosition(), closestGraveyard.getSpawnPoint().getRotation()));
		broadcaster.create().to(entity).send().entity().doRespawn(closestGraveyard.getSpawnPoint(), entity.getBaseStats());
		outOfCombatRegenService.enableOutOfCombatRegen(entity);
	}

	@Override
	public void kill(ServerEntity entity) {
		entity.kill();
		outOfCombatRegenService.disableOutOfCombatRegen(entity);
		for (ServerAuraInstance aura : auraService.getAllAuras(entity)) {
			ServerAura template = aura.getTemplate();
			if (template.isRemoveOnDeath()) {
				auraService.removeAllInstances(entity, template);
			}
		}
	}
}
