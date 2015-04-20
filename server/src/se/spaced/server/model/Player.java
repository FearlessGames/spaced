package se.spaced.server.model;

import se.fearlessgames.common.util.uuid.UUID;
import se.fearlessgames.common.util.uuid.UUIDFactoryImpl;
import se.spaced.server.model.spawn.PlayerTemplate;
import se.spaced.shared.model.EntityInteractionCapability;
import se.spaced.shared.model.Gender;
import se.spaced.shared.model.stats.EntityStats;

import javax.persistence.Entity;
import java.util.EnumSet;


@Entity
public class Player extends ServerEntity {

	private PlayerType type;

	public Player(
			UUID uuid,
			String name,
			Gender gender,
			PersistedCreatureType creatureType,
			PersistedPositionalData positionalData,
			PersistedAppearanceData appearanceData,
			EntityStats stats,
			PersistedFaction faction, PlayerType type) {
		super(uuid,
				name,
				appearanceData,
				positionalData,
				creatureType,
				stats,
				faction,
				new PlayerTemplate(UUIDFactoryImpl.INSTANCE.combUUID(), name));
		this.type = type;
		setGender(gender);
	}


	protected Player() {
	}

	public boolean isGm() {
		return type == PlayerType.GM;
	}

	@Override
	protected EnumSet<EntityInteractionCapability> getEntityInteractionCapabilities() {
		return EnumSet.of(EntityInteractionCapability.ATTACK, EntityInteractionCapability.TRADE, EntityInteractionCapability.WHISPER);
	}
}
