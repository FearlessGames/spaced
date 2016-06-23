package se.spaced.server.model;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.hibernate.annotations.Columns;
import org.hibernate.annotations.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.ardortech.math.SpacedRotation;
import se.ardortech.math.SpacedVector3;
import se.fearless.common.stats.Stat;
import se.fearless.common.uuid.UUID;
import se.krka.kahlua.integration.annotations.LuaMethod;
import se.spaced.messages.protocol.Cooldown;
import se.spaced.server.model.cooldown.*;
import se.spaced.server.model.spawn.EntityTemplate;
import se.spaced.server.model.spell.ServerSpell;
import se.spaced.server.persistence.dao.impl.PersistableBase;
import se.spaced.server.persistence.dao.interfaces.NamedPersistable;
import se.spaced.shared.model.*;
import se.spaced.shared.model.stats.EntityStats;
import se.spaced.shared.network.protocol.codec.datatype.EntityData;
import se.spaced.shared.playback.MovementPoint;

import javax.persistence.*;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;


/**
 * The base class of every physical entity in the world, NPCs, Players, critters and objects
 */
@Entity
public abstract class ServerEntity extends PersistableBase implements se.spaced.messages.protocol.Entity, Comparable<ServerEntity>, NamedPersistable {
	@Transient
	private final Logger log = LoggerFactory.getLogger(getClass());

	private String name;

	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	private final EntityTemplate template;

	@Embedded
	private PersistedAppearanceData appearanceData;

	@Embedded
	PersistedPositionalData positionalData;

	@ManyToOne(cascade = CascadeType.ALL)
	PersistedCreatureType creatureType;

	@Type(type = "se.spaced.server.persistence.dao.impl.hibernate.types.EntityStatsUserType")
	@Columns(columns = {
			@Column(name = "STAMINA"),
			@Column(name = "CURRENT_HEALTH"),
			@Column(name = "CURRENT_HEAT"),
			@Column(name = "SHIELD_POWER"),
			@Column(name = "COOL_RATE"),
			@Column(name = "RECOVERY_RATE"),
			@Column(name = "ATTACK_RATING")
	}
	)
	protected EntityStats entityStats;


	@Transient
	private final Map<CooldownSetTemplate, ServerCooldown> cooldowns = Maps.newHashMap();

	@Transient
	private final Map<Cooldown, SimpleCooldown> simpleCooldowns = Maps.newHashMap();


	@ManyToOne(cascade = CascadeType.ALL)
	private PersistedFaction faction;

	@Enumerated(EnumType.STRING)
	@Column(length = 5, nullable = false)
	private EntityState state = EntityState.ALIVE;

	@Enumerated(EnumType.STRING)
	@Column(length = 6, nullable = false)
	private Gender gender = Gender.NONE;

	@Transient
	private long lastCombatTimestamp;

	@Transient
	private MovementPoint<AnimationState> point;

	@Override
	public int compareTo(ServerEntity o) {
		if (o == null) {
			return -1;
		}
		UUID otherPk = o.getPk();
		if (otherPk == null) {
			return -1;
		}
		UUID myPk = getPk();
		if (myPk == null) {
			return 1;
		}
		return myPk.compareTo(otherPk);
	}

	protected ServerEntity(
			UUID id,
			String name,
			PersistedAppearanceData appearanceData,
			PersistedPositionalData positionalData,
			PersistedCreatureType creatureType,
			EntityStats entityStats,
			PersistedFaction faction, EntityTemplate template) {
		super(id);
		this.name = name;
		this.appearanceData = appearanceData;
		this.positionalData = positionalData;
		this.creatureType = creatureType;
		this.entityStats = entityStats;
		this.faction = faction;
		this.template = template;
	}

	protected ServerEntity() {
		this(null, null, null, null, null, null, null, null);
	}

	public boolean updateLocation(SpacedVector3 newLoc) {
		SpacedVector3 oldPosition = positionalData.getPosition();
		boolean equals = oldPosition.equals(newLoc);
		positionalData.setPosition(newLoc);
		return !equals;
	}

	public boolean updateRotation(SpacedRotation newRot) {
		SpacedRotation oldRotation = positionalData.getRotation();
		boolean equals = oldRotation.equals(newRot);
		positionalData.setRotation(newRot);
		return !equals;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}

	public Gender getGender() {
		return gender;
	}

	@Override
	@LuaMethod(name = "GetName")
	public String getName() {
		return name;
	}

	public EntityStats getBaseStats() {
		return entityStats;
	}

	public PersistedPositionalData getPositionalData() {
		return positionalData;
	}

	@LuaMethod(name = "GetPosition")
	public SpacedVector3 getPosition() {
		return positionalData.getPosition();
	}

	@LuaMethod(name = "GetRotation")
	public SpacedRotation getRotation() {
		return positionalData.getRotation();
	}

	public PersistedFaction getFaction() {
		return faction;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder("Entity ");
		builder.append(name);
		builder.append(' ');
		builder.append(getPk());
		builder.append(" @ ");
		builder.append(positionalData != null ? positionalData.getPosition() : "NoPosition");
		return builder.toString();
	}

	public void setPositionalData(PersistedPositionalData positionalData) {
		this.positionalData = positionalData;
		point = convert(positionalData);
	}

	public PersistedAppearanceData getAppearanceData() {
		return appearanceData;
	}

	public PersistedCreatureType getCreatureType() {
		return creatureType;
	}

	public boolean isAlive() {
		return state == EntityState.ALIVE;
	}


	public void kill() {
		state = EntityState.DEAD;
		getBaseStats().getCurrentHealth().changeValue(0.0);
	}

	public void revive(double amount) {
		state = EntityState.ALIVE;
		getBaseStats().getCurrentHealth().changeValue(amount);
	}

	public ServerCooldown getCooldown(ServerSpell spell, long now) {
		return getCooldown(spell.getCoolDown(), now);
	}

	public ServerCooldown getCooldown(CooldownSetTemplate template, long now) {
		ServerCooldown cooldown = cooldowns.get(template);
		if (cooldown == null) {
			cooldown = createCooldownSet(template, now);
			cooldowns.put(template, cooldown);
		}
		return cooldown;
	}

	private ServerCooldown createCooldownSet(CooldownSetTemplate template, long now) {
		List<ServerCooldown> collection = Lists.newArrayList();
		for (CooldownTemplate template1 : template.getCooldownTemplates()) {
			collection.add(getSimpleCooldownInstance(template1, now));
		}
		return new CooldownSet(collection);
	}


	public SimpleCooldown getSimpleCooldownInstance(CooldownTemplate cooldown, long now) {
		SimpleCooldown simpleCooldown = simpleCooldowns.get(cooldown);
		if (simpleCooldown == null) {
			simpleCooldown = cooldown.createCooldown(now);
			simpleCooldowns.put(cooldown, simpleCooldown);
		}
		return simpleCooldown;
	}

	public void warmUp(int heatContribution) {
		entityStats.getHeat().generate(heatContribution);
	}

	public Stat getHeat() {
		return entityStats.getHeat();
	}

	public EntityData createEntityData() {
		return createEntityData(null);
	}

	public EntityData createEntityData(ServerEntity target) {
		UUID pk = target != null ? target.getPk() : UUID.ZERO;

		MovementPoint<AnimationState> movementPoint = getMovementPoint();
		PositionalData position = new PositionalData(movementPoint.position, movementPoint.rotation);
		return new EntityData(getPk(), getName(),
				position,
				getAppearanceData().asSharedAppearanceData(),
				new CreatureType(getCreatureType().getPk(), getCreatureType().getName()),
				getBaseStats(), new Faction(getFaction().getPk(), getFaction().getName()), movementPoint.state,
				state, pk, getEntityInteractionCapabilities());
	}

	public EntityTemplate getTemplate() {
		return template;
	}

	public void updateLastCombatTimestamp(long now) {
		lastCombatTimestamp = Math.max(now, lastCombatTimestamp);
	}

	public long getLastCombatTimestamp() {
		return lastCombatTimestamp;
	}

	public void resetCombatTimestamp() {
		lastCombatTimestamp = 0;
	}

	protected abstract EnumSet<EntityInteractionCapability> getEntityInteractionCapabilities();

	public MovementPoint<AnimationState> getMovementPoint() {
		if (point == null) {
			point = convert(positionalData);
		}
		return point;
	}

	private MovementPoint<AnimationState> convert(PersistedPositionalData data) {
		return new MovementPoint<AnimationState>(0, AnimationState.IDLE, data.position, positionalData.rotation);
	}

	public void setMovementPoint(MovementPoint<AnimationState> point) {
		this.point = point;
		this.positionalData = new PersistedPositionalData(point.position, point.rotation);
	}

	public AnimationState getCurrentAnimation() {
		if (point == null) {
			return AnimationState.IDLE;
		}
		return point.state;
	}
}