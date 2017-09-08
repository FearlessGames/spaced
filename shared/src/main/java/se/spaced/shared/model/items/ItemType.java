package se.spaced.shared.model.items;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import se.krka.kahlua.integration.annotations.LuaMethod;

import java.util.Set;

//TODO Clean this up, split into groups (ARMOR, CONSUMABLE, WEAPON, ..) and subgroups (CHEST, LEGS, ONE HAND, ..)
public enum ItemType {
	HELMET(Usage.EQUIP, ContainerType.HEAD),
	SHIRT(Usage.EQUIP, ContainerType.CHEST),
	TROUSERS(Usage.EQUIP, ContainerType.LEGS),
	JUMPSUIT(Usage.EQUIP, ContainerType.CHEST, ContainerType.LEGS),
	SHOES(Usage.EQUIP, ContainerType.FEET),
	GLOVES(Usage.EQUIP, ContainerType.HAND),

	JETPACK(Usage.EQUIP, ContainerType.BACK),
	VEHICLE(Usage.EQUIP, ContainerType.UTILITY),
	SHIELD_GENERATOR(Usage.EQUIP, ContainerType.UTILITY),
	MAIN_HAND_ITEM(Usage.EQUIP, ContainerType.MAIN_HAND),
	OFF_HAND_ITEM(Usage.EQUIP, ContainerType.OFF_HAND),
	TWO_HAND_ITEM(Usage.EQUIP, ContainerType.MAIN_HAND, ContainerType.OFF_HAND),

	LEFT_WRIST(Usage.EQUIP, ContainerType.LEFT_WRIST),
	RIGHT_WRIST(Usage.EQUIP, ContainerType.RIGHT_WRIST),

	BAG(Usage.NONE, ContainerType.BAG_SLOT),
	TRASH(Usage.NONE, ContainerType.BAG_SLOT),
	RESOURCE(Usage.NONE, ContainerType.BAG_SLOT),

	CONSUMABLE(Usage.CONSUME, ContainerType.BAG_SLOT);

	private final ContainerType mainSlot;
	private final ImmutableSet<ContainerType> occupiedSlots;
	private final Usage usage;

	ItemType(Usage usage, ContainerType mainSlot, ContainerType... occupiedSlots) {
		this.usage = usage;
		this.mainSlot = mainSlot;
		this.occupiedSlots = Sets.immutableEnumSet(mainSlot, occupiedSlots);
	}

	public Set<ContainerType> getOccupiedSlots() {
		return occupiedSlots;
	}

	public Usage getUsage() {
		return usage;
	}

	public ContainerType getMainSlot() {
		return mainSlot;
	}

	@LuaMethod(name = "GetName")
	public String getName() {
		return super.toString();
	}
}
