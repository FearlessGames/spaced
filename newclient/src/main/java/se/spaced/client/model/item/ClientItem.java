package se.spaced.client.model.item;

import com.google.common.base.Objects;
import se.fearlessgames.common.util.uuid.UUID;
import se.krka.kahlua.integration.annotations.LuaMethod;
import se.spaced.client.model.ClientSpell;
import se.spaced.messages.protocol.AuraTemplate;
import se.spaced.messages.protocol.ItemTemplate;
import se.spaced.messages.protocol.ItemTemplateData;
import se.spaced.messages.protocol.SpacedItem;
import se.spaced.shared.model.AppearanceData;
import se.spaced.shared.model.Money;
import se.spaced.shared.model.items.ContainerType;
import se.spaced.shared.model.items.ItemType;
import se.spaced.shared.model.items.Usage;

import java.util.EnumSet;
import java.util.Set;

public class ClientItem implements SpacedItem {
	private final UUID id;
	private final ItemTemplateData template;

	public ClientItem(UUID id, ItemTemplateData template) {
		this.id = id;
		this.template = template;
	}

	@Override
	public UUID getPk() {
		return id;
	}

	@Override
	public ItemTemplate getItemTemplate() {
		return template;
	}

	@LuaMethod(name = "GetUUID")
	public String getUUID() {
		return id.toString();
	}

	@LuaMethod(name = "GetName")
	public String getName() {
		return template.getName();
	}

	@LuaMethod(name = "GetItemTypes")
	public Set<ItemType> getItemTypes() {
		return template.getItemTypes();
	}

	@LuaMethod(name = "IsOfType")
	public boolean isOfType(ItemType type) {
		return getItemTypes().contains(type);
	}

	private AppearanceData getAppearanceData() {
		return template.getAppearanceData();
	}

	@LuaMethod(name = "GetModelPath")
	public String getModelPath() {
		return getAppearanceData().getModelName();
	}

	@LuaMethod(name = "GetIconPath")
	public String getIconPath() {
		return getAppearanceData().getPortraitName();
	}

	@LuaMethod(name = "GetEquipAuras")
	public Set<? extends AuraTemplate> getEquipAuras() {
		return template.getAuras();
	}

	@LuaMethod(name = "GetPrice")
	public Money getSellsFor() {
		return template.getSellsFor();
	}

	@LuaMethod(name = "GetUsages")
	public Set<Usage> getUsages() {
		final EnumSet<Usage> usages = EnumSet.noneOf(Usage.class);
		for (final ItemType itemType : template.getItemTypes()) {
			usages.add(itemType.getUsage());
		}
		return usages;
	}

	@LuaMethod(name = "GetOnClickSpell")
	public ClientSpell getOnClickSpell() {
		return (ClientSpell) template.getOnClickSpell();
	}

	@LuaMethod(name = "GoesInSlot")
	public boolean goesInSlot(ContainerType containerType) {
		Set<ItemType> itemTypes = getItemTypes();
		for (ItemType itemType : itemTypes) {
			if (itemType.getOccupiedSlots().contains(containerType)) {
				// TODO: do we need to look for usage here?
				return true;
			}
		}
		return false;
	}

	public ItemTemplateData getItemTemplateData() {
		return template;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof SpacedItem)) {
			return false;
		}
		SpacedItem spacedItem = (SpacedItem) o;
		UUID pk1 = getPk();
		UUID pk2 = spacedItem.getPk();
		if (pk1 != null && pk2 != null) {
			return pk1.equals(pk2);
		}
		return false;
	}

	@Override
	public int hashCode() {
		UUID pk = getPk();
		if (pk != null) {
			return getPk().hashCode();
		}
		return super.hashCode();
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(getClass()).add("pk", getPk()).add("name", getName()).toString();
	}
}
