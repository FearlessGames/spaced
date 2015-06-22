package se.spaced.server.model.player;

import se.ardortech.math.SpacedRotation;
import se.ardortech.math.SpacedVector3;
import se.fearlessgames.common.util.TimeProvider;
import se.fearlessgames.common.util.uuid.UUID;
import se.fearlessgames.common.util.uuid.UUIDFactory;
import se.spaced.server.model.PersistedAppearanceData;
import se.spaced.server.model.PersistedCreatureType;
import se.spaced.server.model.PersistedFaction;
import se.spaced.server.model.PersistedPositionalData;
import se.spaced.server.model.Player;
import se.spaced.server.model.PlayerType;
import se.spaced.shared.model.Gender;
import se.spaced.shared.model.stats.EntityStats;
import se.spaced.shared.model.stats.StatData;

import java.util.Random;

public class PlayerMockFactory {
	private final UUIDFactory UUID_FACTORY;

	private final PersistedCreatureType creatureType;
	private final PersistedPositionalData startingPosition;
	private final TimeProvider timeProvider;
	private final PersistedFaction faction;
	private final PersistedAppearanceData appearanceData;
	private final GenderGenerator genderGenerator;

	private PlayerMockFactory(Builder builder) {
		creatureType = builder.creatureType;
		startingPosition = builder.startingPosition;
		timeProvider = builder.timeProvider;
		faction = builder.faction;
		appearanceData = builder.appearanceData;
		genderGenerator = builder.genderGenerator;
		UUID_FACTORY = builder.uuidFactory;
	}


	public Player createPlayer(String name) {
		Player player = new Player(UUID_FACTORY.combUUID(), name, genderGenerator.getGender(), creatureType,
				new PersistedPositionalData(startingPosition.getPosition(), startingPosition.getRotation()),
				appearanceData, new EntityStats(timeProvider, new StatData(12, 10, 0, 1.0, EntityStats.IN_COMBAT_COOLRATE, 0.0)), faction, PlayerType.REGULAR);
		return player;
	}

	public static class Builder implements se.spaced.shared.util.Builder<PlayerMockFactory> {
		private PersistedPositionalData startingPosition = new PersistedPositionalData(SpacedVector3.ZERO,
				SpacedRotation.IDENTITY);
		private final TimeProvider timeProvider;
		private PersistedCreatureType creatureType;
		private PersistedFaction faction;
		private PersistedAppearanceData appearanceData = new PersistedAppearanceData("modelName", "portraitName");
		private GenderGenerator genderGenerator = GenderGenerator.RANDOM;
		private final UUIDFactory uuidFactory;

		public Builder(TimeProvider timeProvider, UUIDFactory uuidFactory) {
			this.uuidFactory = uuidFactory;
			this.timeProvider = timeProvider;

			creatureType = new PersistedCreatureType(uuidFactory.randomUUID(), "Humanoid");
			faction = new PersistedFaction(uuidFactory.combUUID(), "Faction");
		}

		public Builder creatureType(PersistedCreatureType creatureType) {
			this.creatureType = creatureType;
			return this;
		}

		public Builder startingPosition(SpacedVector3 startingPosition) {
			this.startingPosition = new PersistedPositionalData(startingPosition, SpacedRotation.IDENTITY);
			return this;
		}

		public Builder faction(PersistedFaction faction) {
			this.faction = faction;
			return this;
		}

		public Builder appearanceData(PersistedAppearanceData appearanceData) {
			this.appearanceData = appearanceData;
			return this;
		}

		public Builder genderGenerator(GenderGenerator genderGenerator) {
			this.genderGenerator = genderGenerator;
			return this;
		}

		@Override
		public PlayerMockFactory build() {
			return new PlayerMockFactory(this);
		}
	}

	public enum GenderGenerator {
		MALE_ONLY {
			@Override
			Gender getGender() {
				return Gender.MALE;
			}
		},
		FEMALE_ONLY {
			@Override
			Gender getGender() {
				return Gender.FEMALE;
			}
		},
		RANDOM {
			private final Random random = new Random(1337);

			@Override
			Gender getGender() {
				return random.nextBoolean() ? Gender.MALE : Gender.FEMALE;
			}
		};

		abstract Gender getGender();
	}

	public static UUIDFactory NULL_UUID_FACTORY = new UUIDFactory() {
		@Override
		public UUID randomUUID() {
			return null;
		}
		@Override
		public UUID combUUID() {
			return null;
		}
	};
}
