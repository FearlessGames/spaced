package se.spaced.server.model.movement;

import com.ardor3d.math.MathUtils;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.ardortech.math.SpacedRotation;
import se.ardortech.math.SpacedVector3;
import se.fearless.common.time.TimeProvider;
import se.fearless.common.uuid.UUID;
import se.spaced.messages.protocol.s2c.S2CProtocol;
import se.spaced.server.model.Player;
import se.spaced.server.model.ServerEntity;
import se.spaced.server.model.action.ActionScheduler;
import se.spaced.server.model.action.EnvironmentalDamageAction;
import se.spaced.server.model.action.OrderedAction;
import se.spaced.server.model.combat.CurrentActionService;
import se.spaced.server.model.combat.SpellCombatService;
import se.spaced.server.model.entity.VisibilityService;
import se.spaced.server.model.spell.effect.DamageSchoolEffect;
import se.spaced.server.net.broadcast.SmrtBroadcaster;
import se.spaced.server.player.PlayerService;
import se.spaced.shared.activecache.Job;
import se.spaced.shared.model.AnimationState;
import se.spaced.shared.model.MagicSchool;
import se.spaced.shared.model.PositionalData;
import se.spaced.shared.playback.MovementPoint;
import se.spaced.shared.playback.RecordingPoint;
import se.spaced.shared.util.math.interval.IntervalInt;

import java.util.Collection;
import java.util.Map;

@Singleton
public class MovementServiceImpl implements MovementService {
	private final Logger log = LoggerFactory.getLogger(getClass());

	private static final long UPDATE_PERSISTENT_INTERVAL = 10000L;

	private final PlayerService playerService;
	private final CurrentActionService currentActionService;

	private final Map<UUID, Long> lastUpdateTime = Maps.newHashMap();

	private final SmrtBroadcaster<S2CProtocol> broadcaster;
	private final TimeProvider timeProvider;
	private final ActionScheduler actionScheduler;
	private final SpellCombatService spellCombatService;
	private static final double MINIMUM_HARMFUL_FALLSPEED = 8;
	private static final double DEADLY_FALLSPEED = 25;
	private final VisibilityService visibilityService;

	@Inject
	public MovementServiceImpl(
			SmrtBroadcaster<S2CProtocol> broadcaster, TimeProvider timeProvider, ActionScheduler actionScheduler,
			SpellCombatService spellCombatService,
			VisibilityService visibilityService,
			PlayerService playerService,
			CurrentActionService currentActionService) {
		this.broadcaster = broadcaster;
		this.actionScheduler = actionScheduler;
		this.timeProvider = timeProvider;
		this.spellCombatService = spellCombatService;
		this.visibilityService = visibilityService;
		this.playerService = playerService;
		this.currentActionService = currentActionService;
	}

	@Override
	public void hitGround(ServerEntity entity, float impactSpeed) {
		if (impactSpeed > MINIMUM_HARMFUL_FALLSPEED) {
			double maxHealth = entity.getBaseStats().getMaxHealth().getValue();
			int damage = (int) MathUtils.lerp((impactSpeed - MINIMUM_HARMFUL_FALLSPEED) / (DEADLY_FALLSPEED - MINIMUM_HARMFUL_FALLSPEED),
					0.05 * maxHealth, maxHealth);
			DamageSchoolEffect fallDamageEffect = new DamageSchoolEffect(spellCombatService, broadcaster,
					new IntervalInt(damage, damage), MagicSchool.PHYSICAL);
			actionScheduler.add(new EnvironmentalDamageAction(timeProvider.now(), entity, spellCombatService, "Fall damage", fallDamageEffect));
		}
	}

	@Override
	public void sendPlaybackData(final ServerEntity entity, final RecordingPoint<AnimationState> playbackData) {
		// TODO: fix tricky race condition when people enter and leave visibility area
		// might miss some playback messages
		visibilityService.invokeForNearby(entity, new Job<Collection<ServerEntity>>() {
			@Override
			public void run(Collection<ServerEntity> value) {
				broadcaster.create().to(value).exclude(entity).send().movement().sendPlayback(entity, playbackData);
			}
		});
	}


	private void updatePosition(ServerEntity entity, MovementPoint<AnimationState> point) {
		// This should probably not be here
		if (!isMovementAllowed(entity)) {
			return;
		}
		boolean standingStill = entity.getPosition().equals(point.position) &&
				entity.getRotation().equals(point.rotation);
		boolean actuallyMoved = !standingStill;
		entity.setMovementPoint(point);
		if (actuallyMoved) {
			visibilityService.updateEntityPosition(entity);
		}

		if (point.state.isMoving()) {
			OrderedAction action = currentActionService.getCurrentAction(entity);
			if (action != null) {
				action.performerMoved();
			}
		}

		if (actuallyMoved) {
			updatePersistentPosition(entity, point.timestamp);
		}
	}

	@Override
	public void moveAndRotateEntity(ServerEntity entity, MovementPoint<AnimationState> point) {
		updatePosition(entity, point);
	}

	@Override
	public void teleportEntity(ServerEntity entity, SpacedVector3 newPos, SpacedRotation rotation, long now) {
		if (isTeleportAllowed(entity)) {
			broadcaster.create().to(entity).send().movement().teleportTo(new PositionalData(newPos, rotation));
		}
	}

	private boolean isTeleportAllowed(ServerEntity entity) {
		return true;
	}

	private void updatePersistentPosition(ServerEntity entity, long now) {
		if (entity.getTemplate().isPersistent()) {
			Long lastUpdate = lastUpdateTime.remove(entity.getPk());
			if (lastUpdate == null || lastUpdate - now > UPDATE_PERSISTENT_INTERVAL) {
				Player player = (Player) entity;
				playerService.updatePlayer(player);
				lastUpdateTime.put(player.getPk(), now);
			}
		}
	}

	@Override
	public boolean isMovementAllowed(ServerEntity entity) {
		return entity != null && entity.isAlive();
	}
	
}
