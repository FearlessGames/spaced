package se.spaced.server.net.mina;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableMultimap;
import com.google.inject.Inject;
import se.fearlessgames.common.util.TimeProvider;
import se.fearlessgames.common.util.uuid.UUID;
import se.spaced.messages.protocol.AuraInstance;
import se.spaced.messages.protocol.AuraTemplate;
import se.spaced.messages.protocol.CooldownData;
import se.spaced.messages.protocol.Entity;
import se.spaced.messages.protocol.InventoryData;
import se.spaced.messages.protocol.ItemTemplate;
import se.spaced.messages.protocol.ItemTemplateData;
import se.spaced.messages.protocol.Salts;
import se.spaced.messages.protocol.SpacedInventory;
import se.spaced.messages.protocol.SpacedItem;
import se.spaced.messages.protocol.Spell;
import se.spaced.messages.protocol.s2c.remote.S2CAbstractRequiredWriteCodecImpl;
import se.spaced.server.model.items.ItemTemplateDataFactory;
import se.spaced.server.model.items.ServerItemTemplate;
import se.spaced.server.spell.SpellEffectWriter;
import se.spaced.shared.model.AnimationState;
import se.spaced.shared.model.MagicSchool;
import se.spaced.shared.model.PositionalData;
import se.spaced.shared.model.items.ContainerType;
import se.spaced.shared.model.items.EquipFailure;
import se.spaced.shared.model.items.ItemType;
import se.spaced.shared.model.items.UnequipFailure;
import se.spaced.shared.model.player.PlayerCreationFailure;
import se.spaced.shared.model.stats.EntityStats;
import se.spaced.shared.network.protocol.codec.SharedCodec;
import se.spaced.shared.network.protocol.codec.datatype.EntityData;
import se.spaced.shared.network.protocol.codec.datatype.SpellData;
import se.spaced.shared.network.protocol.codec.datatype.SpellEffect;
import se.spaced.shared.playback.RecordingPoint;
import se.spaced.shared.world.TimeSystemInfo;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Set;

public class ServerToClientRequiredWriteCodec extends S2CAbstractRequiredWriteCodecImpl {
	private final SharedCodec sharedCodec;
	private final TimeProvider timeProvider;
	private final SpellEffectWriter spellEffectWriter;

	@Inject
	public ServerToClientRequiredWriteCodec(SharedCodec sharedCodec, TimeProvider timeProvider) {
		this.sharedCodec = sharedCodec;
		this.timeProvider = timeProvider;
		spellEffectWriter = new SpellEffectWriter();
	}

	@Override
	public void writeAuraTemplate(OutputStream output, AuraTemplate aura) throws IOException {
		sharedCodec.writeAuraTemplate(this, output, aura);
	}

	@Override
	public void writeAuraInstance(OutputStream output, AuraInstance auraInstance) throws IOException {
		writeAuraTemplate(output, auraInstance.getTemplate());
		writeLong(output, auraInstance.getTimeLeft(timeProvider.now()));
	}

	@Override
	public void writeCooldown(OutputStream output, se.spaced.messages.protocol.Cooldown cooldown) throws IOException {
		sharedCodec.writeUUID(this, output, cooldown.getPk());
	}

	@Override
	public void writeCooldownData(OutputStream output, CooldownData cooldownData) throws IOException {
		sharedCodec.writeUUID(this, output, cooldownData.getPk());
		sharedCodec.writeLinearTimeValue(this, output, cooldownData.getLinearTimeValue());
	}

	@Override
	public void writeEntity(OutputStream output, Entity entity) throws IOException {
		sharedCodec.writeUUID(this, output, entity.getPk());
	}

	@Override
	public void writeSpacedInventory(OutputStream output, SpacedInventory inventory) throws IOException {
		writeUUID(output, inventory.getPk());
	}

	@Override
	public void writePlayerCreationFailure(
			OutputStream output, PlayerCreationFailure playerCreationFailure) throws IOException {
		writeByte(output, (byte) playerCreationFailure.ordinal());
	}

	@Override
	public void writeInventoryData(OutputStream output, InventoryData inventory) throws IOException {
		sharedCodec.writeUUID(this, output, inventory.getPk());
		ImmutableMultimap<Integer, ? extends SpacedItem> items = inventory.getItemMap();
		writeIntVlq(output, items.keySet().size());
		for (Integer position : items.keySet()) {
			ImmutableCollection<? extends SpacedItem> itemStack = items.get(position);
			writeIntVlq(output, position.shortValue());
			writeIntVlq(output, itemStack.size());
			for (SpacedItem item : itemStack) {
				sharedCodec.writeUUID(this, output, item.getPk());
				writeItemTemplateData(output, ItemTemplateDataFactory.create((ServerItemTemplate) item.getItemTemplate()));
			}
		}
	}


	@Override
	public void writeItemTemplate(OutputStream output, ItemTemplate itemTemplate) throws IOException {
		sharedCodec.writeUUID(this, output, itemTemplate.getPk());
	}

	@Override
	public void writeSpacedItem(OutputStream output, SpacedItem spacedItem) throws IOException {
		sharedCodec.writeUUID(this, output, spacedItem.getPk());
		sharedCodec.writeUUID(this, output, spacedItem.getItemTemplate().getPk());
	}


	public void writeItemType(OutputStream output, ItemType itemType) throws IOException {
		writeByte(output, (byte) itemType.ordinal());
	}

	@Override
	public void writeSpell(OutputStream output, Spell spell) throws IOException {
		sharedCodec.writeUUID(this, output, spell.getPk());
	}

	@Override
	public void writeAnimationState(OutputStream output, AnimationState animationState) throws IOException {
		sharedCodec.writeAnimationState(this, output, animationState);
	}

	@Override
	public void writeMagicSchool(OutputStream output, MagicSchool magicSchool) throws IOException {
		sharedCodec.writeMagicSchool(this, output, magicSchool);
	}

	@Override
	public void writePositionalData(OutputStream output, PositionalData positionalData) throws IOException {
		sharedCodec.writePositionalData(this, output, positionalData);
	}

	@Override
	public void writeContainerType(OutputStream output, ContainerType containerType) throws IOException {
		sharedCodec.writeContainerType(this, output, containerType);
	}

	@Override
	public void writeEquipFailure(OutputStream output, EquipFailure equipFailure) throws IOException {
		writeByte(output, (byte) equipFailure.ordinal());
	}

	@Override
	public void writeUnequipFailure(OutputStream output, UnequipFailure unequipFailure) throws IOException {
		writeByte(output, (byte) unequipFailure.ordinal());
	}

	@Override
	public void writeEntityStats(OutputStream output, EntityStats entityStats) throws IOException {
		sharedCodec.writeEntityStats(this, output, entityStats);
	}

	@Override
	public void writeEntityData(OutputStream output, EntityData entityData) throws IOException {
		sharedCodec.writeEntityData(this, output, entityData);
	}

	@Override
	public void writeItemTemplateData(OutputStream output, ItemTemplateData itemTemplateData) throws IOException {
		sharedCodec.writeUUID(this, output, itemTemplateData.getPk());
		writeString(output, itemTemplateData.getName());
		sharedCodec.writeAppearanceData(this, output, itemTemplateData.getAppearanceData());
		writeSetOfItemType(output, itemTemplateData.getItemTypes());
		writeSetOfAuraTemplate(output, itemTemplateData.getAuras());
		writeString(output, itemTemplateData.getSellsFor().getCurrency());
		writeLong(output, itemTemplateData.getSellsFor().getAmount());
		Spell clickSpell = itemTemplateData.getOnClickSpell();
		if (clickSpell != null) {
			writeBoolean(output, true);
			writeSpell(output, clickSpell);
		} else {
			writeBoolean(output, false);
		}
	}

	@Override
	public void writeSalts(OutputStream output, Salts salts) throws IOException {
		writeString(output, salts.getUserSalt());
		writeString(output, salts.getOneTimeSalt());
	}

	@Override
	public void writeSpellData(OutputStream output, SpellData spellData) throws IOException {
		writeUUID(output, spellData.getId());
		writeStringAsAscii(output, spellData.getName());
		writeInt(output, spellData.getCastTime());
		writeMagicSchool(output, spellData.getSchool());
		writeBoolean(output, spellData.isRequiresHostileTarget());
		writeBoolean(output, spellData.isCancelOnMove());
		writeInt(output, spellData.getRanges().getStart());
		writeInt(output, spellData.getRanges().getEnd());
		writeStringAsAscii(output, spellData.getEffectResource());
		writeInt(output, spellData.getHeat());

		sharedCodec.writeCooldowns(this, output, spellData.getCooldowns());
		sharedCodec.writeAuras(this, output, spellData.getRequiredAuras());
		Collection<? extends SpellEffect> spellEffects = spellData.getSpellEffects();
		spellEffectWriter.writeSpellEffectList(this, output, spellEffects);
	}

	@Override
	public void writeRecordingPointOfAnimationState(
			OutputStream output, RecordingPoint<AnimationState> recordingPointOfAnimationState) throws IOException {
		sharedCodec.writeRecordingPoint(this, output, recordingPointOfAnimationState);
	}

	@Override
	public void writeTimeSystemInfo(OutputStream output, TimeSystemInfo timeSystemInfo) throws IOException {
		writeByte(output, (byte) timeSystemInfo.getHoursPerDay());
		writeByte(output, (byte) timeSystemInfo.getMinutesPerHour());
		writeByte(output, (byte) timeSystemInfo.getSecondsPerMinute());
		writeDouble(output, timeSystemInfo.getSpeedFactor());
	}

	@Override
	public void writeUUID(OutputStream output, UUID uuid) throws IOException {
		sharedCodec.writeUUID(this, output, uuid);
	}

	public void writeSetOfItemType(OutputStream output, Set<? extends ItemType> listOfItemType) throws IOException {
		int size = listOfItemType.size();
		writeInt(output, size);
		for (ItemType itemType : listOfItemType) {
			writeItemType(output, itemType);
		}
	}

	private void writeSetOfAuraTemplate(OutputStream output, Set<? extends AuraTemplate> auras) throws IOException {
		int size = auras.size();
		writeInt(output, size);
		for (AuraTemplate auraTemplate : auras) {
			writeAuraTemplate(output, auraTemplate);
		}
	}


}
