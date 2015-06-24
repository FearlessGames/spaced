package se.spaced.client.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.ardortech.math.SpacedRotation;
import se.ardortech.math.SpacedVector3;
import se.fearless.common.publisher.Subscriber;
import se.fearless.common.util.TimeProvider;
import se.fearless.common.uuid.UUID;
import se.krka.kahlua.integration.annotations.LuaMethod;
import se.spaced.client.model.listener.ClientEntityListener;
import se.spaced.messages.protocol.Entity;
import se.spaced.shared.model.*;
import se.spaced.shared.model.stats.EntityStats;
import se.spaced.shared.model.stats.Stat;
import se.spaced.shared.network.protocol.codec.datatype.EntityData;
import se.spaced.shared.playback.BufferedMovementPlayer;
import se.spaced.shared.playback.MovementPoint;
import se.spaced.shared.playback.PlaybackUpdater;
import se.spaced.shared.playback.RecordingPoint;
import se.spaced.shared.util.ListenerDispatcher;

import java.util.EnumSet;

/**
 * The base class of every physical entity in the world, NPCs, Players, critters and objects
 */
public class ClientEntity implements Entity, Subscriber<Stat> {
	private static final Logger log = LoggerFactory.getLogger(ClientEntity.class);

	private final UUID pk;

	private ListenerDispatcher<ClientEntityListener> dispatcher;
	private String name;
	private AppearanceData appearanceData;
	private PositionalData positionalData;
	private CreatureType creatureType;
	private EntityStats entityStats;
	private ClientEntity target;
	private Faction faction;
	private boolean alive;
	private final EnumSet<EntityInteractionCapability> entityInteractionCapabilities;

	private volatile BufferedMovementPlayer<AnimationState> movementPlayer;

	private ClientEntity(
			UUID pk, String name, AppearanceData appearanceData, PositionalData positionalData, CreatureType creatureType,
			EntityStats entityStats,
			Faction faction,
			final ListenerDispatcher<ClientEntityListener> dispatcher,
			boolean alive,
			EnumSet<EntityInteractionCapability> entityInteractionCapabilities) {
		this.pk = pk;
		this.name = name;
		this.appearanceData = appearanceData;
		this.positionalData = positionalData;
		this.creatureType = creatureType;
		this.entityStats = entityStats;
		this.dispatcher = dispatcher;
		this.faction = faction;
		this.alive = alive;
		this.entityInteractionCapabilities = entityInteractionCapabilities;

		setup();
	}


	public void activateMovementPlayer(MovementPoint<AnimationState> firstPoint) {
		playAnimation(firstPoint.state);
		setPositionalData(new PositionalData(firstPoint.position, firstPoint.rotation));

		movementPlayer = new BufferedMovementPlayer<AnimationState>(new PlaybackUpdater<AnimationState>() {
			@Override
			public void updateState(long t, AnimationState state, SpacedVector3 position, SpacedRotation rotation) {
				playAnimation(state);
			}

			@Override
			public void updatePosition(long t, SpacedVector3 position, SpacedRotation rotation) {
				setPositionalData(new PositionalData(position, rotation));
			}
		}, firstPoint);
	}

	public void playAnimation(AnimationState state) {
		dispatcher.trigger().animationStateChanged(this, state);
	}

	private void setup() {
		entityStats.getCurrentHealth().subscribe(this);
		entityStats.getMaxHealth().subscribe(this);
		entityStats.getStamina().subscribe(this);
	}

	public ClientEntity(EntityData entityData, ListenerDispatcher<ClientEntityListener> dispatcher) {
		this(entityData.getId(), entityData.getName(), entityData.getAppearanceData(), entityData.getPositionalData(),
				entityData.getCreatureType(),
				entityData.getStats(),
				entityData.getFaction(),
				dispatcher,
				entityData.getEntityState() == EntityState.ALIVE,
				entityData.getEntityInteractionCapabilities());
	}

	public ClientEntity(UUID uuid, TimeProvider timeProvider) {
		this(uuid,
				null,
				new AppearanceData(),
				new PositionalData(),
				CreatureType.NULL_TYPE,
				new EntityStats(timeProvider),
				null,
				ListenerDispatcher.create(ClientEntityListener.class),
				false,
				EnumSet.noneOf(EntityInteractionCapability.class));
	}

	@LuaMethod(name = "GetName")
	public String getName() {
		return name;
	}

	public EntityStats getBaseStats() {
		return entityStats;
	}

	public PositionalData getPositionalData() {
		return positionalData;
	}

	public SpacedVector3 getPosition() {
		return positionalData.getPosition();
	}

	@LuaMethod(name = "GetRotation")
	public SpacedRotation getRotation() {
		return positionalData.getRotation();
	}

	public void setPositionalData(PositionalData positionalData) {
		this.positionalData.setPosition(positionalData.getPosition());
		this.positionalData.setRotation(positionalData.getRotation());
		this.dispatcher.trigger().positionalDataChanged(this);
	}

	public void setAppearanceData(AppearanceData appearanceData) {
		this.appearanceData = appearanceData;
		dispatcher.trigger().appearanceDataUpdated(this);
	}

	public AppearanceData getAppearanceData() {
		return appearanceData;
	}

	public CreatureType getCreatureType() {
		return creatureType;
	}

	public Faction getFaction() {
		return faction;
	}

	@LuaMethod(name = "IsAlive")
	public boolean isAlive() {
		return alive;
	}

	public void setAlive(final boolean alive) {
		this.alive = alive;
		if (!alive) {
			getBaseStats().getOutOfCombatHealthRegen().disable();
			getBaseStats().getCurrentHealth().changeValue(0);
			dispatcher.trigger().died(this);
		} else {
			dispatcher.trigger().respawned(this);
		}
	}

	@LuaMethod(name = "GetTarget")
	public ClientEntity getTarget() {
		return target;
	}

	public void setTarget(ClientEntity target) {
		this.target = target;
	}

	@LuaMethod(name = "GetXmoPath")
	public String getXmoPath() {
		return appearanceData.getModelName();
	}

	@LuaMethod(name = "GetUUID")
	public String getUUID() {
		return pk.toString();
	}

	@Override
	public UUID getPk() {
		return pk;
	}

	@Override
	public void update(Stat arg) {
		dispatcher.trigger().statsUpdated(this);
	}

	public void update(EntityData entityData, ListenerDispatcher<ClientEntityListener> entityDispatcher) {
		this.appearanceData = entityData.getAppearanceData();
		this.creatureType = entityData.getCreatureType();
		this.dispatcher = entityDispatcher;
		this.entityStats = entityData.getStats();
		this.name = entityData.getName();
		this.positionalData = entityData.getPositionalData();
		this.alive = entityData.getEntityState() == EntityState.ALIVE;
		setup();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof Entity)) {
			return false;
		}
		Entity e = (Entity) o;
		UUID pk1 = getPk();
		UUID pk2 = e.getPk();
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
		StringBuilder builder = new StringBuilder("Entity ");
		builder.append(name);
		builder.append(' ');
		builder.append(getPk());
		builder.append(" @ ");
		builder.append(positionalData.getPosition());
		return builder.toString();
	}

	public void addRecordingPoint(RecordingPoint<AnimationState> recordingPoint) {
		if (movementPlayer != null) {
			movementPlayer.addData(recordingPoint);
		} else {
			log.error("Got playback data for " + this + " which has no movement player!");
		}
	}

	public BufferedMovementPlayer<AnimationState> getMovementPlayer() {
		return movementPlayer;
	}

	@LuaMethod(name = "GetInteractionCapabilities")
	public EnumSet<EntityInteractionCapability> getEntityInteractionCapabilities() {
		return entityInteractionCapabilities;
	}
}
