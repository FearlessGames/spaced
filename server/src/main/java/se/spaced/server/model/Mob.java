package se.spaced.server.model;

import se.fearless.common.uuid.UUID;
import se.krka.kahlua.integration.annotations.LuaMethod;
import se.spaced.server.loot.Loot;
import se.spaced.server.loot.LootGenerator;
import se.spaced.server.model.aggro.AggroManager;
import se.spaced.server.model.aggro.SimpleAggroManager;
import se.spaced.server.model.items.EquippedItems;
import se.spaced.server.model.movement.TransportationMode;
import se.spaced.server.model.spawn.EntityTemplate;
import se.spaced.server.model.spawn.MobTemplate;
import se.spaced.server.model.spell.SpellBook;
import se.spaced.shared.model.EntityInteractionCapability;
import se.spaced.shared.model.stats.EntityStats;

import java.util.Collection;
import java.util.EnumSet;
import java.util.Random;

public class Mob extends ServerEntity {
	private final AggroManager aggro = new SimpleAggroManager(1.1, new Random());

	private final MobTemplate template;
	private final LootGenerator lootGenerator;
	private final EquippedItems equippedItems;

	public Mob(
			UUID uuid, MobTemplate template, String name, PersistedPositionalData positionalData,
			PersistedAppearanceData appearanceData,
			EntityStats stats,
			PersistedFaction faction,
			LootGenerator lootGenerator,
			EquippedItems equippedItems
	) {
		super(uuid, name, appearanceData, positionalData, template.getCreatureType(), stats, faction, template);
		this.template = template;
		this.lootGenerator = lootGenerator;
		this.equippedItems = equippedItems;
	}


	private final transient SpellBook spellBook = new SpellBook();

	private final transient EnumSet<EntityInteractionCapability> entityInteractionCapabilities = EnumSet.noneOf(EntityInteractionCapability.class);

	@Override
	public EntityTemplate getTemplate() {
		return template;
	}

	public void setEntityInteractionCapabilities(EnumSet<EntityInteractionCapability> capabilities) {
		entityInteractionCapabilities.clear();
		entityInteractionCapabilities.addAll(capabilities);
	}

	public Collection<Loot> generateLoot() {
		return lootGenerator.generateLoot();
	}

	public void addAggro(ServerEntity serverEntity, int damage) {
		aggro.addHate(serverEntity, damage);
	}

	public void removeAggro(ServerEntity serverEntity) {
		aggro.clearHate(serverEntity);
	}

	public ServerEntity getCurrentAggroTarget() {
		return aggro.getMostHated();
	}

	public boolean isAggroWith(ServerEntity enemy) {
		return aggro.isAggroWith(enemy);
	}

	public boolean hasAggroTarget() {
		return getCurrentAggroTarget() != null;
	}

	@LuaMethod(name = "GetAggroManager")
	public AggroManager getAggroManager() {
		return aggro;
	}

	@LuaMethod(name = "GetSpellBook")
	public SpellBook getSpellBook() {
		return spellBook;
	}

	@Override
	public void kill() {
		super.kill();
		aggro.clearAll();
	}

	public double getWalkSpeed() {
		return getSpeed(TransportationMode.WALK);
	}

	public double getRunSpeed() {
		return getSpeed(TransportationMode.RUN);
	}

	public double getSpeed(TransportationMode mode) {
		return entityStats.getSpeedModifier().getValue() * template.getSpeed(mode);
	}

	public TransportationMode getSlowTransportationMode() {
		return template.getSlowTransportationMode();
	}

	public EquippedItems getEquippedItems() {
		return equippedItems;
	}

	@Override
	protected EnumSet<EntityInteractionCapability> getEntityInteractionCapabilities() {
		return entityInteractionCapabilities;
	}
}
