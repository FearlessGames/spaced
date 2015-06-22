package se.spaced.server.model.spawn;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import se.fearlessgames.common.util.TimeProvider;
import se.fearlessgames.common.util.uuid.UUID;
import se.fearlessgames.common.util.uuid.UUIDFactory;
import se.fearlessgames.common.util.uuid.UUIDFactoryImpl;
import se.spaced.server.loot.EmptyLootTemplate;
import se.spaced.server.loot.Loot;
import se.spaced.server.loot.LootGenerator;
import se.spaced.server.loot.PersistableLootTemplate;
import se.spaced.server.mob.brains.templates.BrainTemplate;
import se.spaced.server.mob.brains.templates.NullBrainTemplate;
import se.spaced.server.model.Mob;
import se.spaced.server.model.PersistedAppearanceData;
import se.spaced.server.model.PersistedCreatureType;
import se.spaced.server.model.PersistedFaction;
import se.spaced.server.model.PersistedPositionalData;
import se.spaced.server.model.items.EquippedItems;
import se.spaced.server.model.items.ServerItemTemplate;
import se.spaced.server.model.movement.TransportationMode;
import se.spaced.server.model.spell.ServerSpell;
import se.spaced.server.persistence.MockOwner;
import se.spaced.shared.model.items.ContainerType;
import se.spaced.shared.model.stats.EntityStats;
import se.spaced.shared.model.stats.StatData;
import se.spaced.shared.util.random.RandomProvider;

import javax.persistence.CascadeType;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.MapKeyClass;
import javax.persistence.MapKeyEnumerated;
import javax.persistence.OneToOne;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Entity
public class MobTemplate extends EntityTemplate {

	private static final UUIDFactory UUID_FACTORY = UUIDFactoryImpl.INSTANCE;
	private int stamina;
	private int maxShield;
	private double coolRate;
	private double shieldRecovery;

	@ElementCollection(fetch = FetchType.EAGER)
	@MapKeyClass(TransportationMode.class)
	Map<TransportationMode, Double> transportationModes = Maps.newEnumMap(TransportationMode.class);

	@ManyToOne(cascade = CascadeType.ALL)
	private BrainTemplate brainTemplate;

	@ManyToOne(cascade = CascadeType.ALL)
	private PersistableLootTemplate lootTemplate;


	@Embedded
	private PersistedAppearanceData appearance;

	@ManyToOne
	private PersistedCreatureType creatureType;

	@ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private Set<ServerSpell> spellBook = Sets.newHashSet();

	@ManyToOne
	private PersistedFaction faction;

	@OneToOne(cascade = CascadeType.ALL)
	private WhisperMessage whisperMessage;

	@ManyToMany
	@Fetch(value = FetchMode.SUBSELECT)
	private List<ServerItemTemplate> itemTypesForSale = Lists.newArrayList();

	private String scriptPath;

	private Boolean moveToTarget;
	private Boolean lookAtTarget;

	private Integer proximityAggroDistance;
	private Integer socialAggroDistance;


	@ManyToMany
	@JoinTable(name = "MobTemplate_equippedItems")
	@MapKeyEnumerated
	@Fetch(value = FetchMode.SUBSELECT)
	private Map<ContainerType, ServerItemTemplate> equippedItems = Maps.newHashMap();

	protected MobTemplate() {
		super(null, null);
	}

	@Override
	public boolean isPersistent() {
		return false;
	}


	private MobTemplate(Builder builder) {
		super(builder.pk, builder.templateName);
		this.creatureType = builder.creatureType;
		this.stamina = builder.stamina;
		this.maxShield = builder.maxShield;
		this.shieldRecovery = builder.shieldRecovery;
		this.appearance = builder.appearanceData;
		this.spellBook = builder.spellBook;
		this.faction = builder.faction;
		this.brainTemplate = builder.brainTemplate;
		this.lootTemplate = builder.lootTemplate;
		this.transportationModes.putAll(builder.transportationModes);
	}

	public Mob createMob(TimeProvider timeProvider, UUID mobId, final RandomProvider randomProvider) {
		Mob mob = new Mob(mobId,
				this,
				name,
				new PersistedPositionalData(),
				appearance,
				new EntityStats(timeProvider, new StatData(stamina, maxShield, shieldRecovery, 1.0, coolRate, 0.0)),
				faction,
				new LootGenerator() {
					@Override
					public Collection<Loot> generateLoot() {
						return lootTemplate.generateLoot(randomProvider);
					}
				},
				createEquipedItems(mobId));

		for (ServerSpell spell : spellBook) {
			mob.getSpellBook().addSpell(spell);
		}
		return mob;
	}

	private EquippedItems createEquipedItems(UUID mobId) {
		EquippedItems equippedItems = new EquippedItems(new MockOwner(mobId));

		if (equippedItems != null) {
			for (Map.Entry<ContainerType, ServerItemTemplate> entry : this.equippedItems.entrySet()) {
				equippedItems.put(entry.getValue().create(), entry.getKey());
			}
		}
		return equippedItems;
	}

	public PersistedCreatureType getCreatureType() {
		return creatureType;
	}

	public BrainTemplate getBrainTemplate() {
		return brainTemplate;
	}

	public PersistableLootTemplate getLootTemplate() {
		return lootTemplate;
	}

	@Override
	public String toString() {
		return "MobTemplate{" +
				"name='" + name + '\'' +
				", stamina=" + stamina +
				", creatureType=" + creatureType +
				'}';
	}

	public WhisperMessage getWhisperMessage() {
		return whisperMessage;
	}

	public List<ServerItemTemplate> getItemTypesForSale() {
		return itemTypesForSale;
	}

	public String getScriptPath() {
		return scriptPath;
	}

	public boolean isMoveToTarget() {
		return moveToTarget;
	}

	public boolean isLookAtTarget() {
		return lookAtTarget;
	}

	public int getProximityAggroDistance() {
		return proximityAggroDistance;
	}

	public int getSocialAggroDistance() {
		return socialAggroDistance;
	}

	public void setScriptPath(String path) {
		this.scriptPath = path;
	}

	public void setProximityAggroParameters(ProximityAggroParameters parameters) {
		proximityAggroDistance = parameters.getProximityAggroDistance();
		socialAggroDistance = parameters.getSocialAggroDistance();
	}

	public void setAttackingParameters(AttackingParameters attackingParameters) {
		moveToTarget = attackingParameters.isMoveToTarget();
		lookAtTarget = attackingParameters.isLookAtTarget();
	}

	public double getSpeed(TransportationMode mode) {
		Double speed = transportationModes.get(mode);
		return speed == null ? 0.0 : speed;
	}

	public TransportationMode getSlowTransportationMode() {
		TransportationMode mode = TransportationMode.WALK;
		double speed = Double.MAX_VALUE;
		for (Map.Entry<TransportationMode, Double> entry : transportationModes.entrySet()) {
			if (entry.getValue() < speed) {
				speed = entry.getValue();
				mode = entry.getKey();
			}
		}
		return mode;
	}

	public static class Builder implements se.spaced.shared.util.Builder<MobTemplate> {
		private final UUID pk;
		private final String templateName;
		private int stamina = 12;
		private int maxShield = 10;
		private double shieldRecovery = 0.2;
		private PersistedAppearanceData appearanceData = new PersistedAppearanceData("unititialized", "uninitialized");
		private PersistedCreatureType creatureType = new PersistedCreatureType(UUID_FACTORY.combUUID(), "humanoid");
		private final Set<ServerSpell> spellBook = new HashSet<ServerSpell>();
		private PersistedFaction faction = new PersistedFaction(UUID_FACTORY.combUUID(), "noFaction");
		private BrainTemplate brainTemplate;
		private PersistableLootTemplate lootTemplate;
		public Map<TransportationMode, Double> transportationModes = Maps.newEnumMap(TransportationMode.class);


		public Builder(UUID pk, String templateName) {
			this.pk = pk;
			this.templateName = templateName;
			this.lootTemplate = EmptyLootTemplate.INSTANCE;
			this.brainTemplate = NullBrainTemplate.INSTANCE;
		}

		@Override
		public MobTemplate build() {
			return new MobTemplate(this);
		}

		public Builder lootTemplate(PersistableLootTemplate lootTemplate) {
			this.lootTemplate = lootTemplate;
			return this;
		}

		public Builder brainTemplate(BrainTemplate brainTemplate) {
			this.brainTemplate = brainTemplate;
			return this;
		}

		public Builder stamina(int stamina) {
			this.stamina = stamina;
			return this;
		}

		public Builder maxShield(int maxShield) {
			this.maxShield = maxShield;
			return this;
		}

		public Builder shieldRecovery(double shieldRecovery) {
			this.shieldRecovery = shieldRecovery;
			return this;
		}

		public Builder appearance(PersistedAppearanceData appearanceData) {
			this.appearanceData = appearanceData;
			return this;
		}

		public Builder creatureType(PersistedCreatureType creatureType) {
			this.creatureType = creatureType;
			return this;
		}

		public Builder spells(Collection<ServerSpell> spells) {
			spellBook.addAll(spells);
			return this;
		}

		public Builder faction(PersistedFaction faction) {
			this.faction = faction;
			return this;
		}

		public Builder walkSpeed(double speed) {
			transportationModes.put(TransportationMode.WALK, speed);
			return this;
		}

		public Builder runSpeed(double speed) {
			transportationModes.put(TransportationMode.RUN, speed);
			return this;
		}
	}

	public int getStamina() {
		return stamina;
	}

	public int getMaxShield() {
		return maxShield;
	}

	public double getCoolRate() {
		return coolRate;
	}

	public double getShieldRecovery() {
		return shieldRecovery;
	}
}
