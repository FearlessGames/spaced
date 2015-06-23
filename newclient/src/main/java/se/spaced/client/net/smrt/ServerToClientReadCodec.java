package se.spaced.client.net.smrt;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import se.fearlessgames.common.util.TimeProvider;
import se.fearlessgames.common.util.uuid.UUID;
import se.spaced.client.model.ClientEntityProxy;
import se.spaced.client.model.ClientSpellProxy;
import se.spaced.client.model.InventoryProvider;
import se.spaced.client.model.item.ClientInventory;
import se.spaced.client.model.item.ClientItem;
import se.spaced.client.model.item.ClientItemProxy;
import se.spaced.client.model.item.ItemTemplateProxy;
import se.spaced.client.model.spelleffects.ClientSpellEffect;
import se.spaced.client.net.SpellEffectReader;
import se.spaced.messages.protocol.AuraInstance;
import se.spaced.messages.protocol.AuraTemplate;
import se.spaced.messages.protocol.ClientAuraInstance;
import se.spaced.messages.protocol.ClientAuraTemplate;
import se.spaced.messages.protocol.Cooldown;
import se.spaced.messages.protocol.CooldownData;
import se.spaced.messages.protocol.CooldownProxy;
import se.spaced.messages.protocol.Entity;
import se.spaced.messages.protocol.InventoryData;
import se.spaced.messages.protocol.ItemTemplate;
import se.spaced.messages.protocol.ItemTemplateData;
import se.spaced.messages.protocol.Salts;
import se.spaced.messages.protocol.SpacedInventory;
import se.spaced.messages.protocol.SpacedItem;
import se.spaced.messages.protocol.Spell;
import se.spaced.messages.protocol.s2c.remote.S2CAbstractRequiredReadCodecImpl;
import se.spaced.messages.protocol.s2c.remote.S2CRequiredReadCodec;
import se.spaced.shared.model.AnimationState;
import se.spaced.shared.model.AppearanceData;
import se.spaced.shared.model.MagicSchool;
import se.spaced.shared.model.Money;
import se.spaced.shared.model.PositionalData;
import se.spaced.shared.model.aura.ModStat;
import se.spaced.shared.model.items.ContainerType;
import se.spaced.shared.model.items.EquipFailure;
import se.spaced.shared.model.items.ItemType;
import se.spaced.shared.model.items.UnequipFailure;
import se.spaced.shared.model.player.PlayerCreationFailure;
import se.spaced.shared.model.stats.EntityStats;
import se.spaced.shared.model.stats.Operator;
import se.spaced.shared.model.stats.StatType;
import se.spaced.shared.network.protocol.codec.SharedCodec;
import se.spaced.shared.network.protocol.codec.datatype.EntityData;
import se.spaced.shared.network.protocol.codec.datatype.SpellData;
import se.spaced.shared.playback.RecordingPoint;
import se.spaced.shared.util.math.LinearTimeValue;
import se.spaced.shared.util.math.interval.IntervalInt;
import se.spaced.shared.world.TimeSystemInfo;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;


@Singleton
public class ServerToClientReadCodec extends S2CAbstractRequiredReadCodecImpl implements S2CRequiredReadCodec {
	private final SharedCodec codec;
	private final InventoryProvider inventoryProvider;
	private final TimeProvider timeProvider;
	private final SpellEffectReader spellEffectReader;

	@Inject
	public ServerToClientReadCodec(
			SharedCodec codec,
			InventoryProvider inventoryProvider, TimeProvider timeProvider) {
		this.codec = codec;
		this.inventoryProvider = inventoryProvider;
		this.timeProvider = timeProvider;
		spellEffectReader = new SpellEffectReader();
	}

	@Override
	public AuraTemplate readAuraTemplate(InputStream input) throws IOException {
		UUID uuid = codec.readUUID(this, input);
		String name = readStringAsUTF8(input);
		long duration = readLong(input);
		String iconPath = readStringAsUTF8(input);
		boolean visible = readBoolean(input);
		int size = readByte(input);
		SortedSet<ModStat> stats = Sets.newTreeSet();

		for (int i = 0; i < size; i++) {
			int statOrdinal = readShort(input);
			StatType statType = StatType.values()[statOrdinal];
			int operatorOrdinal = readShort(input);
			Operator oper = Operator.values()[operatorOrdinal];
			double value = readDouble(input);
			stats.add(new ModStat(value, statType, oper));
		}
		boolean isKey = readBoolean(input);
		return new ClientAuraTemplate(uuid, name, duration, iconPath, visible, stats, isKey);
	}

	public Set<? extends AuraTemplate> readAuras(InputStream input) throws IOException {
		int size = readIntVlq(input);
		Set<AuraTemplate> auras = Sets.newHashSetWithExpectedSize(size);
		for (int i = 0; i < size; i++) {
			AuraTemplate aura = readAuraTemplate(input);
			auras.add(aura);
		}
		return auras;
	}


	@Override
	public AuraInstance readAuraInstance(InputStream input) throws IOException {
		AuraTemplate auraTemplate = readAuraTemplate(input);
		long timeLeft = readLong(input);
		return new ClientAuraInstance(auraTemplate, timeLeft, timeProvider);

	}

	@Override
	public Entity readEntity(InputStream input) throws IOException {
		UUID uuid = codec.readUUID(this, input);
		return new ClientEntityProxy(uuid);
	}

	@Override
	public SpacedInventory readSpacedInventory(InputStream input) throws IOException {
		UUID pk = readUUID(input);
		// TODO: add pk as a parameter for when you can have multiple inventories
		return inventoryProvider.getPlayerInventory();
	}

	@Override
	public PlayerCreationFailure readPlayerCreationFailure(InputStream input) throws IOException {
		byte ordinal = readByte(input);
		return PlayerCreationFailure.values()[ordinal];
	}

	@Override
	public InventoryData readInventoryData(InputStream input) throws IOException {
		final UUID uuid = codec.readUUID(this, input);
		int nrOfStacks = readShort(input);
		final Multimap<Integer, ClientItem> items = HashMultimap.create();
		for (int i = 0; i < nrOfStacks; i++) {

			Integer position = readIntVlq(input);
			Integer stackSize = readIntVlq(input);

			for (int j = 0; j < stackSize; j++) {
				UUID pk = codec.readUUID(this, input);
				ItemTemplateData itemTemplate = readItemTemplateData(input);
				items.put(position, new ClientItem(pk, itemTemplate));
			}
		}
		return new ClientInventory(uuid, items);
	}


	@Override
	public ItemTemplate readItemTemplate(InputStream input) throws IOException {
		UUID uuid = codec.readUUID(this, input);
		return new ItemTemplateProxy(uuid);
	}


	@Override
	public SpacedItem readSpacedItem(InputStream input) throws IOException {
		UUID uuid = codec.readUUID(this, input);
		UUID templatePk = codec.readUUID(this, input);
		ItemTemplateProxy itemTemplate = new ItemTemplateProxy(templatePk);
		return new ClientItemProxy(uuid, itemTemplate);
	}

	public ItemType readItemType(InputStream input) throws IOException {
		int ordinal = readUnsignedByte(input);
		return ItemType.values()[ordinal];
	}

	@Override
	public Spell readSpell(InputStream input) throws IOException {
		UUID uuid = codec.readUUID(this, input);
		return new ClientSpellProxy(uuid);
	}


	private Collection<? extends Cooldown> readCooldowns(InputStream input) throws IOException {
		final List<Cooldown> cooldowns = new ArrayList<Cooldown>();
		final int n = readInt(input);
		for (int i = 0; i < n; i++) {
			cooldowns.add(readCooldown(input));
		}
		return cooldowns;
	}

	@Override
	public Cooldown readCooldown(InputStream input) throws IOException {
		final UUID uuid = codec.readUUID(this, input);
		return new CooldownProxy(uuid);
	}

	@Override
	public CooldownData readCooldownData(InputStream input) throws IOException {
		UUID uuid = codec.readUUID(this, input);
		LinearTimeValue linearTimeValue = codec.readLinearTimeValue(this, input);
		return new CooldownData(uuid, linearTimeValue);
	}

	@Override
	public SpellData readSpellData(InputStream input) throws IOException {
		UUID id = codec.readUUID(this, input);
		String name = readStringAsAscii(input);
		int castTime = readInt(input);
		MagicSchool school = readMagicSchool(input);
		boolean requiresHostile = readBoolean(input);
		boolean cancelOnMove = readBoolean(input);
		int start = readInt(input);
		int end = readInt(input);
		IntervalInt ranges = new IntervalInt(start, end);
		String effectResource = readStringAsAscii(input);
		int heat = readInt(input);

		final Collection<? extends Cooldown> cooldowns = readCooldowns(input);
		final Set<? extends AuraTemplate> auras = readAuras(input);
		List<ClientSpellEffect> clientSpellEffects = spellEffectReader.readSpellEffectList(this, input);
		return new SpellData(id, name, castTime, school, requiresHostile, ranges, effectResource, heat,
				cooldowns, cancelOnMove, auras, clientSpellEffects);
	}

	@Override
	public RecordingPoint<AnimationState> readRecordingPointOfAnimationState(InputStream input) throws IOException {
		return codec.readRecordingPoint(this, input);
	}

	@Override
	public TimeSystemInfo readTimeSystemInfo(InputStream input) throws IOException {
		int hoursPerDay = readByte(input);
		int minutesPerHour = readByte(input);
		int secondsPerMinute = readByte(input);
		double speedFactor = readDouble(input);
		return new TimeSystemInfo(hoursPerDay, minutesPerHour, secondsPerMinute, speedFactor);
	}

	@Override
	public AnimationState readAnimationState(InputStream input) throws IOException {
		return codec.readAnimationState(this, input);
	}

	@Override
	public MagicSchool readMagicSchool(InputStream input) throws IOException {
		return codec.readMagicSchool(this, input);
	}

	@Override
	public PositionalData readPositionalData(InputStream input) throws IOException {
		return codec.readPositionalData(this, input);
	}

	@Override
	public ContainerType readContainerType(InputStream input) throws IOException {
		return codec.readContainerType(this, input);
	}

	@Override
	public EquipFailure readEquipFailure(InputStream input) throws IOException {
		byte ordinal = readByte(input);
		return EquipFailure.values()[ordinal];
	}

	@Override
	public UnequipFailure readUnequipFailure(InputStream input) throws IOException {
		byte ordinal = readByte(input);
		return UnequipFailure.values()[ordinal];
	}

	@Override
	public EntityStats readEntityStats(InputStream input) throws IOException {
		return codec.readEntityStats(this, input);
	}

	@Override
	public EntityData readEntityData(InputStream input) throws IOException {
		return codec.readEntityData(this, input);
	}

	@Override
	public ItemTemplateData readItemTemplateData(InputStream input) throws IOException {
		UUID pk = codec.readUUID(this, input);
		String name = readString(input);
		AppearanceData appearanceData = codec.readAppearanceData(this, input);
		Set<ItemType> itemTypes = readSetOfItemType(input);
		Set<AuraTemplate> auras = readSetOfAuraTemplate(input);
		String currency = readString(input);
		long amount = readLong(input);
		boolean hasOnClick = readBoolean(input);
		Spell onClickSpell = null;
		if (hasOnClick) {
			onClickSpell = readSpell(input);
		}
		return new ItemTemplateData(pk, name, appearanceData, itemTypes, auras, new Money(amount, currency), onClickSpell);
	}

	@Override
	public Salts readSalts(InputStream input) throws IOException {
		return new Salts(readString(input), readString(input));
	}

	@Override
	public UUID readUUID(InputStream input) throws IOException {
		return codec.readUUID(this, input);
	}

	public Set<ItemType> readSetOfItemType(InputStream input) throws IOException {
		Set<ItemType> list = new HashSet<ItemType>();
		int size = readInt(input);
		for (int i = 0; i < size; i++) {
			list.add(readItemType(input));
		}
		return list;
	}

	public Set<AuraTemplate> readSetOfAuraTemplate(InputStream input) throws IOException {
		Set<AuraTemplate> list = new HashSet<AuraTemplate>();
		int size = readInt(input);
		for (int i = 0; i < size; i++) {
			list.add(readAuraTemplate(input));
		}
		return list;
	}


}
