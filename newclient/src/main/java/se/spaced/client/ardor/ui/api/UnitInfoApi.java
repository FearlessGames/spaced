package se.spaced.client.ardor.ui.api;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.ardortech.math.SpacedVector3;
import se.krka.kahlua.integration.annotations.LuaMethod;
import se.krka.kahlua.integration.expose.ReturnValues;
import se.spaced.client.ardor.ui.exposer.EntityStatsExposer;
import se.spaced.client.model.ClientAuraService;
import se.spaced.client.model.ClientEntity;
import se.spaced.client.model.RelationResolver;
import se.spaced.client.model.UnitIdResolver;
import se.spaced.client.model.UserCharacter;
import se.spaced.messages.protocol.ClientAuraInstance;
import se.spaced.shared.model.stats.EntityStats;

import java.util.Set;

@Singleton
public class UnitInfoApi {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private final UnitIdResolver unitIdResolver;
	private final UserCharacter userCharacter;
	private final RelationResolver relationResolver;
	private final ClientAuraService auraService;

	@Inject
	public UnitInfoApi(
			UnitIdResolver unitIdResolver,
			UserCharacter userCharacter,
			RelationResolver relationResolver, ClientAuraService auraService) {
		this.unitIdResolver = unitIdResolver;
		this.userCharacter = userCharacter;
		this.relationResolver = relationResolver;
		this.auraService = auraService;
	}

	@LuaMethod(global = true, name = "UnitIsEnemy")
	public boolean unitIsEnemy(final String unitRefA, final String unitRefB) {
		final ClientEntity entityA = unitIdResolver.resolveEntity(unitRefA);
		final ClientEntity entityB = unitIdResolver.resolveEntity(unitRefB);

		return relationResolver.areEnemies(entityA, entityB);
	}

	@LuaMethod(global = true, name = "GetEntityFromUnitId")
	public ClientEntity getEntityFromUnitId(String unitId) {
		return unitIdResolver.resolveEntity(unitId);
	}

	@LuaMethod(global = true, name = "UnitStats")
	public EntityStatsExposer getUnitStats(ClientEntity entity) {
		EntityStats playerStats = entity.getBaseStats();
		return new EntityStatsExposer(playerStats);
	}

	@LuaMethod(global = true, name = "UnitPortrait")
	public String getUnitPortrait(ClientEntity entity) {
		logger.debug("Retrieving unit portrait {}", entity);

		if (entity != null) {
			return entity.getAppearanceData().getPortraitName();
		} else {
			return null;
		}
	}


	@LuaMethod(global = true, name = "GetUnitPosition")
	public void getUnitPosition(ReturnValues r, ClientEntity entity) {
		if (entity != null) {
			SpacedVector3 position = entity.getPositionalData().getPosition();
			r.push(position.getX());
			r.push(position.getY());
			r.push(position.getZ());
		}
	}


	@LuaMethod(global = true, name = "GetUnitDistance")
	public double getDistanceToUnit(ClientEntity entity) {
		if (entity == null) {
			return -1;
		}
		return SpacedVector3.distance(userCharacter.getPosition(), entity.getPosition());
	}

	@LuaMethod(global = true, name = "IsInRange")
	public boolean isInRange(ClientEntity entity, double range) {
		if (entity == null) {
			return false;
		}
		final double rangeSq = range * range;
		return SpacedVector3.distanceSq(userCharacter.getPosition(), entity.getPosition()) <= rangeSq;
	}

	@LuaMethod(global = true, name = "GetAuras")
	public Set<ClientAuraInstance> getAuras(ClientEntity entity) {
		return auraService.getVisibleAuras(entity);
	}

	@LuaMethod(global = true, name = "GetAllAuras")
	public Set<ClientAuraInstance> getAllAuras(ClientEntity entity) {
		if (userCharacter.isUserControlledEntity(entity)) {
			return auraService.getAuras(entity);
		}
		return getAuras(entity);
	}
}
