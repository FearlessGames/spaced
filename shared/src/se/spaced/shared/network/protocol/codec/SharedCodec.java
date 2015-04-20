package se.spaced.shared.network.protocol.codec;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;
import se.ardortech.math.SpacedRotation;
import se.ardortech.math.SpacedVector3;
import se.fearlessgames.common.util.TimeProvider;
import se.fearlessgames.common.util.uuid.UUID;
import se.smrt.core.remote.DefaultReadCodec;
import se.smrt.core.remote.DefaultWriteCodec;
import se.spaced.messages.protocol.AuraTemplate;
import se.spaced.messages.protocol.Cooldown;
import se.spaced.shared.model.AnimationState;
import se.spaced.shared.model.AppearanceData;
import se.spaced.shared.model.CreatureType;
import se.spaced.shared.model.EntityInteractionCapability;
import se.spaced.shared.model.EntityState;
import se.spaced.shared.model.Faction;
import se.spaced.shared.model.MagicSchool;
import se.spaced.shared.model.PositionalData;
import se.spaced.shared.model.aura.ModStat;
import se.spaced.shared.model.items.ContainerType;
import se.spaced.shared.model.stats.EntityStats;
import se.spaced.shared.network.protocol.codec.datatype.EntityData;
import se.spaced.shared.network.protocol.codec.datatype.SpellData;
import se.spaced.shared.playback.RecordingPoint;
import se.spaced.shared.util.math.LinearTimeValue;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Set;

public class SharedCodec {
	private final TimeProvider timeProvider;
	private final EnumSetCodec enumSetCodec = new EnumSetCodec();

	@Inject
	public SharedCodec(TimeProvider timeProvider) {
		this.timeProvider = timeProvider;
	}

	public SpacedRotation readRotation(DefaultReadCodec defaultCodec, InputStream input) throws IOException {
		return new SpacedRotation(defaultCodec.readDouble(input),
				defaultCodec.readDouble(input),
				defaultCodec.readDouble(input),
				defaultCodec.readDouble(input));
	}

	public void writeRotation(
			DefaultWriteCodec defaultCodec,
			OutputStream output,
			SpacedRotation rotation) throws IOException {
		defaultCodec.writeDouble(output, rotation.getX());
		defaultCodec.writeDouble(output, rotation.getY());
		defaultCodec.writeDouble(output, rotation.getZ());
		defaultCodec.writeDouble(output, rotation.getW());
	}

	public SpacedVector3 readVector3(DefaultReadCodec defaultCodec, InputStream input) throws IOException {
		return new SpacedVector3(defaultCodec.readDouble(input),
				defaultCodec.readDouble(input),
				defaultCodec.readDouble(input));
	}

	public void writeVector3(
			DefaultWriteCodec defaultCodec,
			OutputStream output,
			SpacedVector3 vector3) throws IOException {
		defaultCodec.writeDouble(output, vector3.getX());
		defaultCodec.writeDouble(output, vector3.getY());
		defaultCodec.writeDouble(output, vector3.getZ());
	}


	public PositionalData readPositionalData(DefaultReadCodec defaultCodec, InputStream input) throws IOException {
		return new PositionalData(readVector3(defaultCodec, input), readRotation(defaultCodec, input));
	}

	public void writePositionalData(
			DefaultWriteCodec defaultCodec,
			OutputStream output,
			PositionalData positionalData) throws IOException {
		writeVector3(defaultCodec, output, positionalData.getPosition());
		writeRotation(defaultCodec, output, positionalData.getRotation());
	}

	public UUID readUUID(DefaultReadCodec defaultCodec, InputStream input) throws IOException {
		long lsb = defaultCodec.readLongNormal(input);
		long msb = defaultCodec.readLongNormal(input);
		return new UUID(msb, lsb);
	}

	public void writeUUID(DefaultWriteCodec defaultCodec, OutputStream output, UUID uuid) throws IOException {
		defaultCodec.writeLongNormal(output, uuid.getLeastSignificantBits());
		defaultCodec.writeLongNormal(output, uuid.getMostSignificantBits());
	}

	public AppearanceData readAppearanceData(DefaultReadCodec defaultCodec, InputStream input) throws IOException {
		return new AppearanceData(defaultCodec.readStringAsUTF8(input),
				defaultCodec.readStringAsUTF8(input),
				readVector3(defaultCodec, input));
	}

	public void writeAppearanceData(
			DefaultWriteCodec defaultCodec,
			OutputStream output,
			AppearanceData appearanceData) throws IOException {
		defaultCodec.writeStringAsUTF8(output, appearanceData.getModelName());
		defaultCodec.writeStringAsUTF8(output, appearanceData.getPortraitName());
		writeVector3(defaultCodec, output, appearanceData.getScale());
	}

	public CreatureType readCreatureType(DefaultReadCodec defaultCodec, InputStream input) throws IOException {
		return new CreatureType(readUUID(defaultCodec, input), defaultCodec.readStringAsUTF8(input));
	}

	public void writeCreatureType(
			DefaultWriteCodec defaultCodec,
			OutputStream output,
			CreatureType creatureType) throws IOException {
		writeUUID(defaultCodec, output, creatureType.getPk());
		defaultCodec.writeStringAsUTF8(output, creatureType.getName());
	}

	public Faction readFaction(DefaultReadCodec defaultCodec, InputStream input) throws IOException {
		return new Faction(readUUID(defaultCodec, input), defaultCodec.readStringAsUTF8(input));
	}

	public void writeFaction(DefaultWriteCodec defaultCodec, OutputStream output, Faction faction) throws IOException {
		writeUUID(defaultCodec, output, faction.getPk());
		defaultCodec.writeStringAsUTF8(output, faction.getName());
	}

	public EntityStats readEntityStats(DefaultReadCodec defaultCodec, InputStream input) throws IOException {
		EntityStats stats = new EntityStats(timeProvider);
		stats.getStamina().changeValue(defaultCodec.readInt(input));
		stats.getCurrentHealth().changeValue(defaultCodec.readInt(input));
		stats.getBaseHealthRegenRate().changeValue(defaultCodec.readDouble(input));
		boolean oocEnabled = defaultCodec.readBoolean(input);
		stats.getOutOfCombatHealthRegen().setEnabled(oocEnabled);

		stats.getMaxHeat().changeValue(defaultCodec.readInt(input));
		stats.getHeat().setValue(defaultCodec.readInt(input));
		stats.getCoolRate().changeValue(defaultCodec.readDouble(input));

		stats.getShieldCharge().changeValue(defaultCodec.readInt(input));
		stats.getShieldEfficiency().changeValue(defaultCodec.readDouble(input));
		stats.getBaseShieldRecovery().changeValue(defaultCodec.readDouble(input));
		boolean shieldOocEnabled = defaultCodec.readBoolean(input);
		stats.getOutOfCombatShieldRecovery().setEnabled(shieldOocEnabled);

		stats.getShieldStrength().changeValue(defaultCodec.readInt(input));

		stats.getSpeedModifier().changeValue(defaultCodec.readDouble(input));

		stats.getAttackRating().changeValue(defaultCodec.readDouble(input));
		return stats;
	}

	public void writeEntityStats(
			DefaultWriteCodec defaultCodec,
			OutputStream output,
			EntityStats stats) throws IOException {
		defaultCodec.writeInt(output, (int) stats.getStamina().getValue());
		defaultCodec.writeInt(output, (int) stats.getCurrentHealth().getValue());
		defaultCodec.writeDouble(output, stats.getBaseHealthRegenRate().getValue());
		defaultCodec.writeBoolean(output, stats.getOutOfCombatHealthRegen().isEnabled());

		defaultCodec.writeInt(output, (int) stats.getMaxHeat().getValue());
		defaultCodec.writeInt(output, (int) stats.getHeat().getValue());
		defaultCodec.writeDouble(output, stats.getCoolRate().getValue());

		defaultCodec.writeInt(output, (int) stats.getShieldCharge().getValue());
		defaultCodec.writeDouble(output, stats.getShieldEfficiency().getValue());
		defaultCodec.writeDouble(output, stats.getBaseShieldRecovery().getValue());
		defaultCodec.writeBoolean(output, stats.getOutOfCombatShieldRecovery().isEnabled());
		defaultCodec.writeInt(output, (int) stats.getShieldStrength().getValue());

		defaultCodec.writeDouble(output, stats.getSpeedModifier().getValue());

		defaultCodec.writeDouble(output, stats.getAttackRating().getValue());
	}

	public EntityData readEntityData(DefaultReadCodec codec, InputStream input) throws IOException {
		UUID uuid = readUUID(codec, input);
		String name = codec.readString(input);
		CreatureType creatureType = readCreatureType(codec, input);
		AppearanceData appearance = readAppearanceData(codec, input);
		PositionalData positionalData = readPositionalData(codec, input);
		EntityStats entityStats = readEntityStats(codec, input);
		Faction faction = readFaction(codec, input);
		AnimationState animationState = readAnimationState(codec, input);
		EntityState entityState = readEntityState(codec, input);
		UUID targetUUID = readUUID(codec, input);
		EnumSet<EntityInteractionCapability> entityInteractionCapabilities = enumSetCodec.readEnumSet(codec,
				input,
				EntityInteractionCapability.class);

		return new EntityData(uuid, name, positionalData, appearance, creatureType, entityStats, faction,
				animationState, entityState, targetUUID, entityInteractionCapabilities);
	}

	private EntityState readEntityState(DefaultReadCodec codec, InputStream input) throws IOException {
		return EntityState.values()[codec.readInt(input)];
	}

	public void writeEntityData(DefaultWriteCodec codec, OutputStream output, EntityData entityData) throws IOException {
		writeUUID(codec, output, entityData.getId());
		codec.writeString(output, entityData.getName());
		writeCreatureType(codec, output, entityData.getCreatureType());
		writeAppearanceData(codec, output, entityData.getAppearanceData());
		writePositionalData(codec, output, entityData.getPositionalData());
		writeEntityStats(codec, output, entityData.getStats());
		writeFaction(codec, output, entityData.getFaction());
		writeAnimationState(codec, output, entityData.getCurrentAnimationState());
		writeEntityState(codec, output, entityData.getEntityState());
		writeUUID(codec, output, entityData.getTarget());
		enumSetCodec.writeEnumSet(codec, output, entityData.getEntityInteractionCapabilities());

	}

	private void writeEntityState(
			DefaultWriteCodec codec,
			OutputStream output,
			EntityState entityState) throws IOException {
		codec.writeInt(output, entityState.ordinal());
	}

	public void writeSpellData(DefaultWriteCodec codec, OutputStream output, SpellData spellData) throws IOException {

	}

	public void writeAuras(DefaultWriteCodec codec, OutputStream output, Set<? extends AuraTemplate> auras) throws IOException {
		codec.writeIntVlq(output, auras.size());
		for (AuraTemplate aura : auras) {
			writeAuraTemplate(codec, output, aura);
		}
	}

	public void writeAuraTemplate(DefaultWriteCodec codec, OutputStream output, AuraTemplate aura) throws IOException {
		writeUUID(codec, output, aura.getPk());
		String name = aura.getName();
		name = name == null ? "" : name;
		codec.writeStringAsUTF8(output, name);
		codec.writeLong(output, aura.getDuration());
		String iconPath = aura.getIconPath();
		iconPath = iconPath == null ? "" : iconPath;
		codec.writeStringAsUTF8(output, iconPath);
		codec.writeBoolean(output, aura.isVisible());

		ImmutableSet<ModStat> mods = aura.getMods();
		if (mods.size() > Byte.MAX_VALUE) {
			throw new RuntimeException("Too many mods for the current codec to work " + mods.size());
		}
		codec.writeByte(output, (byte) mods.size());
		for (ModStat modStat : mods) {
			codec.writeShort(output, (short) modStat.getStatType().ordinal());
			codec.writeShort(output, (short) modStat.getOperator().ordinal());
			codec.writeDouble(output, modStat.getValue());
		}
		codec.writeBoolean(output, aura.isKey());
	}


	public void writeCooldowns(
			DefaultWriteCodec codec,
			OutputStream output,
			Collection<? extends Cooldown> cooldowns) throws IOException {
		codec.writeInt(output, cooldowns.size());
		for (Cooldown cooldown : cooldowns) {
			writeCooldown(codec, output, cooldown);
		}
	}

	private void writeCooldown(DefaultWriteCodec codec, OutputStream output, Cooldown cooldown) throws IOException {
		writeUUID(codec, output, cooldown.getPk());
	}

	public LinearTimeValue readLinearTimeValue(DefaultReadCodec codec, InputStream input) throws IOException {
		final long now = timeProvider.now();

		final double maxValue = codec.readDouble(input);
		final double rate = codec.readDouble(input);
		final double current = codec.readDouble(input);

		final LinearTimeValue value = new LinearTimeValue(maxValue);
		value.setValue(now, current);
		value.setCurrentRate(now, rate);

		return value;
	}

	public void writeLinearTimeValue(
			DefaultWriteCodec codec,
			OutputStream output,
			LinearTimeValue value) throws IOException {
		final long now = timeProvider.now();

		codec.writeDouble(output, value.getMaxValue());
		codec.writeDouble(output, value.getCurrentRate());
		codec.writeDouble(output, value.getValue(now));
	}

	public MagicSchool readMagicSchool(DefaultReadCodec codec, InputStream input) throws IOException {
		int ordinal = codec.readUnsignedByte(input);
		return MagicSchool.values()[ordinal];
	}

	public void writeMagicSchool(
			DefaultWriteCodec codec,
			OutputStream output,
			MagicSchool magicSchool) throws IOException {
		codec.writeByte(output, (byte) magicSchool.ordinal());
	}

	public void writeAnimationState(
			DefaultWriteCodec codec,
			OutputStream output,
			AnimationState animationState) throws IOException {
		codec.writeByte(output, (byte) animationState.ordinal());
	}

	public AnimationState readAnimationState(DefaultReadCodec codec, InputStream input) throws IOException {
		int ordinal = codec.readUnsignedByte(input);
		return AnimationState.values()[ordinal];
	}

	public void writeContainerType(
			DefaultWriteCodec writeCodec,
			OutputStream output,
			ContainerType type) throws IOException {
		writeCodec.writeByte(output, (byte) type.ordinal());
	}

	public ContainerType readContainerType(DefaultReadCodec codec, InputStream input) throws IOException {
		int ordinal = codec.readUnsignedByte(input);
		return ContainerType.values()[ordinal];
	}

	public void writeRecordingPoint(
			DefaultWriteCodec codec,
			OutputStream output,
			RecordingPoint<AnimationState> recordingPoint) throws IOException {
		int bits = bit(recordingPoint.sameState, 1) | bit(recordingPoint.sameRotation,
				2) | bit(recordingPoint.samePosition, 4) | bit(recordingPoint.exactPos, 8);
		codec.writeByte(output, (byte) bits);
		codec.writeByte(output, (byte) recordingPoint.delta);

		if (!recordingPoint.sameState) {
			writeAnimationState(codec, output, recordingPoint.state);
		}
		if (!recordingPoint.sameRotation) {
			writeCompressedRotation(codec, output, recordingPoint.rotation);
		}
		if (!recordingPoint.samePosition) {
			if (recordingPoint.exactPos) {
				writeCompressedVector(codec, output, recordingPoint.position);
			} else {
				codec.writeByte(output, (byte) recordingPoint.size);
				codec.writeByte(output, (byte) recordingPoint.xi);
				codec.writeByte(output, (byte) recordingPoint.yi);
				codec.writeByte(output, (byte) recordingPoint.zi);
			}
		}
	}

	private int bit(boolean b, int mask) {
		return b ? mask : 0;
	}

	public RecordingPoint<AnimationState> readRecordingPoint(
			DefaultReadCodec codec,
			InputStream input) throws IOException {
		int bits = codec.readUnsignedByte(input);
		boolean sameState = (bits & 1) != 0;
		boolean sameRotation = (bits & 2) != 0;
		boolean samePosition = (bits & 4) != 0;
		boolean exactPos = (bits & 8) != 0;
		int delta = codec.readUnsignedByte(input);

		AnimationState state = null;
		if (!sameState) {
			state = readAnimationState(codec, input);
		}
		SpacedRotation spacedRotation = null;
		if (!sameRotation) {
			spacedRotation = readCompressedRotation(codec, input);
		}
		SpacedVector3 position = null;
		int size = 0;
		int xi = 0;
		int yi = 0;
		int zi = 0;
		if (!samePosition) {
			if (exactPos) {
				position = readCompressedVector3(codec, input);
			} else {
				size = codec.readByte(input);
				xi = codec.readByte(input);
				yi = codec.readByte(input);
				zi = codec.readByte(input);
			}
		}
		return new RecordingPoint<AnimationState>(delta,
				state,
				sameState,
				spacedRotation,
				sameRotation,
				samePosition,
				position,
				size,
				xi,
				yi,
				zi,
				exactPos);
	}

	private SpacedRotation readCompressedRotation(DefaultReadCodec codec, InputStream input) throws IOException {
		double x = codec.readByte(input) / 127.0;
		double y = codec.readByte(input) / 127.0;
		double z = codec.readByte(input) / 127.0;
		double w = codec.readByte(input) / 127.0;
		return new SpacedRotation(x, y, z, w, true);
	}

	private void writeCompressedRotation(
			DefaultWriteCodec codec,
			OutputStream output,
			SpacedRotation rotation) throws IOException {

		codec.writeByte(output, (byte) (rotation.getX() * 127.0));
		codec.writeByte(output, (byte) (rotation.getY() * 127.0));
		codec.writeByte(output, (byte) (rotation.getZ() * 127.0));
		codec.writeByte(output, (byte) (rotation.getW() * 127.0));
	}

	private void writeCompressedVector(
			DefaultWriteCodec codec, OutputStream output, SpacedVector3 v) throws IOException {
		codec.writeFloat(output, v.getXf());
		codec.writeFloat(output, v.getYf());
		codec.writeFloat(output, v.getZf());
	}

	private SpacedVector3 readCompressedVector3(DefaultReadCodec codec, InputStream input) throws IOException {
		float x = codec.readFloat(input);
		float y = codec.readFloat(input);
		float z = codec.readFloat(input);
		return new SpacedVector3(x, y, z);
	}


}
