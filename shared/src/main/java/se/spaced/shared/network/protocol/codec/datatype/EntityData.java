package se.spaced.shared.network.protocol.codec.datatype;

import se.fearless.common.uuid.UUID;
import se.spaced.shared.model.AnimationState;
import se.spaced.shared.model.AppearanceData;
import se.spaced.shared.model.CreatureType;
import se.spaced.shared.model.EntityInteractionCapability;
import se.spaced.shared.model.EntityState;
import se.spaced.shared.model.Faction;
import se.spaced.shared.model.PositionalData;
import se.spaced.shared.model.stats.EntityStats;
import se.spaced.shared.playback.MovementPoint;

import java.util.EnumSet;

/**
 *
 */
public class EntityData {
	private final UUID id;
	private final PositionalData positionalData;
	private final AppearanceData appearanceData;
	private final CreatureType creatureType;
	private final String name;
	private final EntityStats stats;
	private final Faction faction;
	private final AnimationState currentAnimationState;
	private final EntityState entityState;
	private final UUID target;
	private final EnumSet<EntityInteractionCapability> entityInteractionCapabilities;

	public EntityData(
			UUID id,
			String name,
			PositionalData positionalData,
			AppearanceData appearanceData,
			CreatureType creatureType,
			EntityStats stats,
			Faction faction,
			AnimationState currentAnimationState,
			EntityState entityState,
			UUID target,
			EnumSet<EntityInteractionCapability> entityInteractionCapabilities) {
		this.id = id;
		this.name = name;
		this.positionalData = positionalData;
		this.appearanceData = appearanceData;
		this.creatureType = creatureType;
		this.stats = stats;
		this.faction = faction;
		this.currentAnimationState = currentAnimationState;
		this.entityState = entityState;
		this.target = target;
		this.entityInteractionCapabilities = entityInteractionCapabilities;
	}

	public EntityData(
			UUID id,
			String name,
			PositionalData positionalData,
			AppearanceData appearanceData,
			CreatureType creatureType,
			EntityStats stats,
			Faction faction, AnimationState currentAnimationState, EntityState entityState) {
		this(id,
				name,
				positionalData,
				appearanceData,
				creatureType,
				stats,
				faction,
				currentAnimationState,
				entityState,
				UUID.ZERO,
				EnumSet.noneOf(EntityInteractionCapability.class));
	}

	public UUID getId() {
		return id;
	}

	public PositionalData getPositionalData() {
		return positionalData;
	}

	public AppearanceData getAppearanceData() {
		return appearanceData;
	}

	public CreatureType getCreatureType() {
		return creatureType;
	}

	public String getName() {
		return name;
	}

	public EntityStats getStats() {
		return stats;
	}

	public Faction getFaction() {
		return faction;
	}

	public UUID getTarget() {
		return target;
	}

	public EnumSet<EntityInteractionCapability> getEntityInteractionCapabilities() {
		return entityInteractionCapabilities;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!getClass().isInstance(obj)) {
			return false;
		}
		EntityData other = (EntityData) obj;
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		return true;
	}

	public AnimationState getCurrentAnimationState() {
		return currentAnimationState;
	}

	@Override
	public String toString() {
		return "EntityData{" +
				"id=" + id +
				", positionalData=" + positionalData +
				", appearanceData=" + appearanceData +
				", creatureType=" + creatureType +
				", name='" + name + '\'' +
				", stats=" + stats +
				", entityState=" + entityState +
				'}';
	}

	public EntityState getEntityState() {
		return entityState;
	}

	public MovementPoint<AnimationState> getMovementPoint(long now) {
		PositionalData positionaldata = getPositionalData();
		return new MovementPoint<AnimationState>(now,
				getCurrentAnimationState(),
				positionaldata.getPosition(),
				positionaldata.getRotation());
	}
}
