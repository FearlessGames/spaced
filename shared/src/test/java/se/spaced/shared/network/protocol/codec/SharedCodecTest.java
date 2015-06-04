package se.spaced.shared.network.protocol.codec;

import org.junit.Before;
import org.junit.Test;
import se.ardortech.math.SpacedRotation;
import se.ardortech.math.SpacedVector3;
import se.fearlessgames.common.util.MockTimeProvider;
import se.fearless.common.uuid.UUID;
import se.fearlessgames.common.util.uuid.UUIDFactory;
import se.fearlessgames.common.util.uuid.UUIDFactoryImpl;
import se.smrt.core.remote.DefaultCodecImpl;
import se.spaced.shared.model.AnimationState;
import se.spaced.shared.model.AppearanceData;
import se.spaced.shared.model.CreatureType;
import se.spaced.shared.model.EntityState;
import se.spaced.shared.model.Faction;
import se.spaced.shared.model.PositionalData;
import se.spaced.shared.model.aura.ModStat;
import se.spaced.shared.model.stats.EntityStats;
import se.spaced.shared.model.stats.Operator;
import se.spaced.shared.model.stats.StatData;
import se.spaced.shared.model.stats.StatType;
import se.spaced.shared.network.protocol.codec.datatype.EntityData;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.security.SecureRandom;

import static org.junit.Assert.assertEquals;

public class SharedCodecTest {

	private final MockTimeProvider timeProvider = new MockTimeProvider();
	private final UUIDFactory uuidFactory = new UUIDFactoryImpl(timeProvider, new SecureRandom());


	private SharedCodec sharedCodec;
	private DefaultCodecImpl defaultCodec;
	private PipedInputStream inputStream;
	private PipedOutputStream outputStream;
	private static final double EPSILON = 1e-4;

	@Before
	public void setup() throws IOException {
		sharedCodec = new SharedCodec(timeProvider);
		defaultCodec = new DefaultCodecImpl();
		inputStream = new PipedInputStream();
		outputStream = new PipedOutputStream(inputStream);
	}

	@Test
	public void testVector3() throws IOException {
		SpacedVector3 pos = new SpacedVector3(13, -10, 27.5);
		sharedCodec.writeVector3(defaultCodec, outputStream, pos);
		SpacedVector3 vector3 = sharedCodec.readVector3(defaultCodec, inputStream);
		assertEquals(pos, vector3);
	}

	@Test
	public void testAppearanceData() throws IOException {
		AppearanceData data = new AppearanceData("someModelName", "somePortraitName.foo", new SpacedVector3(1, 1, 1));
		sharedCodec.writeAppearanceData(defaultCodec, outputStream, data);
		AppearanceData appearanceData = sharedCodec.readAppearanceData(defaultCodec, inputStream);
		assertEquals(data, appearanceData);
	}

	@Test
	public void testPositionalData() throws IOException {
		SpacedVector3 pos = new SpacedVector3(420.32, 0, -273.513);
		SpacedRotation rot = new SpacedRotation(13, 14, 15, 12);
		PositionalData data = new PositionalData(pos, rot);
		sharedCodec.writePositionalData(defaultCodec, outputStream, data);
		PositionalData positionalData = sharedCodec.readPositionalData(defaultCodec, inputStream);
		assertEquals(data, positionalData);
	}


	@Test
	public void testEntityData() throws IOException {
		SpacedVector3 pos = new SpacedVector3(420.32, 0, -273.513);
		SpacedRotation rot = new SpacedRotation(13, 14, 15, 12);
		AppearanceData appearanceData = new AppearanceData("someModelName",
				"somePortraitName.foo",
				new SpacedVector3(1, 2, 3));
		UUID uuid = uuidFactory.randomUUID();
		String name = "MyReallyCoolUs3rNaem";
		CreatureType creatureType = new CreatureType(uuidFactory.randomUUID(), "Blargh");
		EntityStats stats = new EntityStats(timeProvider, new StatData(20, 10, 0.3, 0.8, EntityStats.IN_COMBAT_COOLRATE, 0.0));
		stats.getCurrentHealth().decreaseValue(10);
		stats.getHeat().generate(30);
		stats.getShieldStrength().decreaseValue(2);
		stats.getShieldStrength().decreaseValue(2);
		stats.getBaseShieldEfficiency().increaseValue(0.1);
		PositionalData positionalData = new PositionalData(pos, rot);
		Faction faction = new Faction(uuid, "pigs");

		EntityData data = new EntityData(uuid, name, positionalData, appearanceData, creatureType, stats, faction,
				AnimationState.IDLE, EntityState.ALIVE);
		sharedCodec.writeEntityData(defaultCodec, outputStream, data);
		EntityData entityData = sharedCodec.readEntityData(defaultCodec, inputStream);
		assertEquals(data, entityData);

		assertEquals(appearanceData, entityData.getAppearanceData());
		assertEquals(uuid, entityData.getId());
		assertEquals(name, entityData.getName());
		assertEquals(creatureType, entityData.getCreatureType());
		assertEquals(appearanceData, entityData.getAppearanceData());
		assertEquals(positionalData, entityData.getPositionalData());
		EntityStats readStats = entityData.getStats();
		assertEquals(stats, readStats);
		assertEquals(faction, entityData.getFaction());
	}

	@Test
	public void entityStats() throws Exception {
		EntityStats stats = new EntityStats(timeProvider, new StatData(20, 10, 0.3, 0.9, 0.5, 0.0));
		stats.getCurrentHealth().decreaseValue(10);
		stats.getHeat().generate(30);
		stats.getShieldStrength().decreaseValue(2);
		stats.getShieldStrength().decreaseValue(2);
		stats.getShieldRecoveryRate().changeValue(0.4);
		stats.getBaseCoolRate().changeValue(0.6);
		stats.getBaseShieldEfficiency().decreaseValue(0.1);
		stats.getAuraStatByType(StatType.SPEED).addModStat(new ModStat(0.0, StatType.SPEED, Operator.POST_MULTIPLY));
		stats.getOutOfCombatShieldRecovery().disable();

		sharedCodec.writeEntityStats(defaultCodec, outputStream, stats);
		EntityStats readStats = sharedCodec.readEntityStats(defaultCodec, inputStream);

		assertEquals(0.0, readStats.getSpeedModifier().getValue(), EPSILON);
		assertEquals(0.4, readStats.getShieldRecoveryRate().getValue(), EPSILON);

		assertEquals(stats, readStats);
	}

	@Test
	public void updateEntityStatsAfterCodec() throws Exception {
		EntityStats stats = new EntityStats(timeProvider, new StatData(20, 10, 0.3, 0.9, 0.5, 0.0));
		stats.getShieldStrength().changeValue(8);
		stats.getShieldRecoveryRate().changeValue(1);
		sharedCodec.writeEntityStats(defaultCodec, outputStream, stats);
		EntityStats readStats = sharedCodec.readEntityStats(defaultCodec, inputStream);
		timeProvider.advanceTime(10000);
		assertEquals(readStats.getMaxShieldStrength().getValue(), readStats.getShieldStrength().getValue(), EPSILON);
	}

	@Test
	public void healthRegen() throws Exception {
		EntityStats stats = new EntityStats(timeProvider, new StatData(20, 10, 0.3, 0.9, 0.5, 0.0));
		stats.getOutOfCombatHealthRegen().disable();

		sharedCodec.writeEntityStats(defaultCodec, outputStream, stats);
		EntityStats readStats = sharedCodec.readEntityStats(defaultCodec, inputStream);

		assertEquals(stats.getHealthRegenRate().getValue(), readStats.getHealthRegenRate().getValue(), EPSILON);
	}

	@Test
	public void shieldRegenWithBuffs() throws Exception {
		EntityStats stats = new EntityStats(timeProvider, new StatData(20, 10, 0.3, 0.9, 0.5, 0.0));
		stats.getOutOfCombatShieldRecovery().disable();
		stats.getShieldRecoveryRate().changeValue(1);
		stats.getBaseShieldRecovery().addModStat(new ModStat(2, StatType.SHIELD_RECOVERY, Operator.ADD));

		assertEquals(3, stats.getShieldRecoveryRate().getValue(), EPSILON);

		sharedCodec.writeEntityStats(defaultCodec, outputStream, stats);
		EntityStats readStats = sharedCodec.readEntityStats(defaultCodec, inputStream);

		assertEquals(3, readStats.getShieldRecoveryRate().getValue(), EPSILON);
	}


	@Test
	public void attackRating() throws Exception {
		int attackRatingAdded = 10;
		EntityStats stats = new EntityStats(timeProvider, new StatData(20, 10, 0.3, 0.9, 0.5, 0.0));

		stats.getAttackRating().addModStat(new ModStat(attackRatingAdded, StatType.ATTACK_RATING, Operator.ADD));

		sharedCodec.writeEntityStats(defaultCodec, outputStream, stats);
		EntityStats readStats = sharedCodec.readEntityStats(defaultCodec, inputStream);

		assertEquals(attackRatingAdded, readStats.getAttackRating().getValue(), EPSILON);
		double percentIncrease = attackRatingAdded / EntityStats.ATTACK_RATING_PER_ATTACK_PERCENT_MULTIPLIER;
		assertEquals(1 + (percentIncrease/100), readStats.getAttackModifier().getValue(), EPSILON);

		assertEquals(stats, readStats);
	}

}
