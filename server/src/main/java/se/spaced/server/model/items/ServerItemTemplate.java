package se.spaced.server.model.items;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import se.fearless.common.uuid.UUID;
import se.spaced.messages.protocol.ItemTemplate;
import se.spaced.server.loot.EmptyLootTemplate;
import se.spaced.server.loot.LootTemplate;
import se.spaced.server.loot.PersistableLootTemplate;
import se.spaced.server.model.PersistedAppearanceData;
import se.spaced.server.model.aura.ServerAura;
import se.spaced.server.model.currency.PersistedMoney;
import se.spaced.server.model.spell.ServerSpell;
import se.spaced.server.persistence.dao.impl.ExternalPersistableBase;
import se.spaced.server.persistence.dao.interfaces.NamedPersistable;
import se.spaced.shared.model.items.ContainerType;
import se.spaced.shared.model.items.ItemType;

import javax.persistence.*;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Entity
public class ServerItemTemplate extends ExternalPersistableBase implements ItemTemplate, NamedPersistable {

	@Embedded
	protected PersistedAppearanceData appearanceData;

	@Column(unique = true, nullable = false)
	protected String name;

	@Embedded
	private PersistedMoney sellsFor = PersistedMoney.ZERO; //Defaults to not for sale

	@ElementCollection(fetch = FetchType.EAGER)
	@JoinTable(name = "ItemTemplateItemTypes")
	@Cascade(value = CascadeType.ALL)
	@Column(name = "equipmentSlot", length = 30)
	@Enumerated(EnumType.STRING)
	protected final Set<ItemType> itemTypes = Sets.newHashSet();

	// Spells on usage
	@ManyToOne(cascade = javax.persistence.CascadeType.ALL)
	private ServerSpell spell;

	// Auras that are applied when item is equipped
	@ManyToMany(cascade = javax.persistence.CascadeType.ALL, fetch = FetchType.EAGER)
	private Set<ServerAura> equipAuras = new HashSet<ServerAura>();

	@ManyToOne(cascade = javax.persistence.CascadeType.ALL)
	private PersistableLootTemplate salvageLootTemplate;

	private Integer maxStackSize;

	protected ServerItemTemplate() {
	}

	private ServerItemTemplate(Builder builder) {
		super(builder.pk);
		this.name = builder.name;
		this.appearanceData = builder.appearanceData;
		this.spell = builder.spell;
		this.itemTypes.addAll(builder.itemTypes);
		this.equipAuras.addAll(builder.auras);
		this.sellsFor = builder.sellsFor;
		this.salvageLootTemplate = builder.salvageLootTemplate;
		this.maxStackSize = builder.stackSize;
	}

	public Set<ItemType> getItemTypes() {
		return itemTypes;
	}

	@Override
	public String getName() {
		return name;
	}

	public PersistedAppearanceData getAppearanceData() {
		return appearanceData;
	}

	public ServerItem create() {
		return new ServerItem(this);
	}

	public ServerSpell getSpell() {
		return spell;
	}

	public Set<ServerAura> getEquipAuras() {
		return equipAuras;
	}

	public PersistedMoney getSellsFor() {
		return sellsFor;
	}

	public int getMaxStackSize() {
		return maxStackSize;
	}

	public LootTemplate getSalvageLootTemplate() {
		return salvageLootTemplate;
	}

	public boolean goesInSlot(ContainerType slot) {
		for (ItemType itemType : getItemTypes()) {
			if (itemType.getOccupiedSlots().contains(slot)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String toString() {
		return name + " (" + getPk() + ")";
	}

	public static class Builder implements se.spaced.shared.util.Builder<ServerItemTemplate> {
		private final UUID pk;
		private final String name;
		private PersistedAppearanceData appearanceData = new PersistedAppearanceData("notInitialized", "notInitialized");
		private ServerSpell spell;
		private final Collection<ItemType> itemTypes = Lists.newArrayList();
		private final Set<ServerAura> auras = Sets.newHashSet();
		private PersistedMoney sellsFor = PersistedMoney.ZERO;
		private PersistableLootTemplate salvageLootTemplate = EmptyLootTemplate.INSTANCE;
		private int stackSize = 1;

		public Builder(UUID pk, String name, ItemType... types) {
			this.pk = pk;
			this.name = name;
			itemTypes.addAll(Arrays.asList(types));
		}

		public Builder appearance(PersistedAppearanceData appearanceData) {
			this.appearanceData = appearanceData;
			return this;
		}

		public Builder spell(ServerSpell spell) {
			this.spell = spell;
			return this;
		}

		public Builder sellsFor(PersistedMoney sellsFor) {
			this.sellsFor = sellsFor;
			return this;
		}

		@Override
		public ServerItemTemplate build() {
			return new ServerItemTemplate(this);
		}

		public Builder addAura(ServerAura aura) {
			this.auras.add(aura);
			return this;
		}

		public Builder salvageLootTemplate(final PersistableLootTemplate salvageLootTemplate) {
			this.salvageLootTemplate = salvageLootTemplate;
			return this;
		}

		public Builder stackable(int stackSize) {
			this.stackSize = stackSize;
			return this;
		}
	}

}
