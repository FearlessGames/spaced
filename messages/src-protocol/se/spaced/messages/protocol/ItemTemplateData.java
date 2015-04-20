package se.spaced.messages.protocol;

import com.google.common.collect.Sets;
import se.fearlessgames.common.util.uuid.UUID;
import se.spaced.shared.model.AppearanceData;
import se.spaced.shared.model.Money;
import se.spaced.shared.model.items.ItemType;

import java.util.Set;

public class ItemTemplateData implements ItemTemplate {
	private final UUID pk;
	private final String name;
	private final AppearanceData appearanceData;
	private final Set<ItemType> itemTypes;
	private final Set<? extends AuraTemplate> auras;
	private final Money sellsFor;
	private Spell onClickSpell;

	public ItemTemplateData(
			UUID pk,
			String name,
			AppearanceData appearanceData,
			Iterable<ItemType> itemTypes,
			Set<? extends AuraTemplate> auras,
			Money sellsFor, Spell onClickSpell) {
		this.pk = pk;
		this.name = name;
		this.appearanceData = appearanceData;
		this.auras = auras;
		this.sellsFor = sellsFor;
		this.onClickSpell = onClickSpell;
		this.itemTypes = Sets.immutableEnumSet(itemTypes);
	}

	@Override
	public UUID getPk() {
		return pk;
	}

	public String getName() {
		return name;
	}

	public Set<? extends AuraTemplate> getAuras() {
		return auras;
	}

	public Money getSellsFor() {
		return sellsFor;
	}

	public AppearanceData getAppearanceData() {
		return appearanceData;
	}

	public Set<ItemType> getItemTypes() {
		return itemTypes;
	}

	public Spell getOnClickSpell() {
		return onClickSpell;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || !(o instanceof ItemTemplate)) {
			return false;
		}

		ItemTemplate that = (ItemTemplate) o;

		if (!pk.equals(that.getPk())) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		return pk.hashCode();
	}

	public void setOnClickSpell(Spell clickOnSpell) {
		this.onClickSpell = clickOnSpell;
	}
}
