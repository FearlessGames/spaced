package se.spaced.server.mob.brains;

import com.google.common.base.Preconditions;
import se.ardortech.math.SpacedVector3;
import se.spaced.messages.protocol.s2c.S2CEmptyReceiver;
import se.spaced.messages.protocol.s2c.S2CProtocol;
import se.spaced.server.mob.MobDecision;
import se.spaced.server.mob.MobOrderExecutor;
import se.spaced.server.model.Mob;
import se.spaced.server.model.movement.TransportationMode;
import se.spaced.server.model.spawn.area.SpawnArea;
import se.spaced.server.model.spawn.area.SpawnPoint;
import se.spaced.shared.util.math.interval.IntervalInt;
import se.spaced.shared.util.random.RandomProvider;

public class RoamingBrain extends AbstractMobBrain {
	private static final double EPSILON_SQ = 0.5 * 0.5;

	private SpacedVector3 currentGoal;

	private final SpawnArea area;
	private final IntervalInt timePauseAtPoints;
	private final RandomProvider randomProvider;

	private long nextPointTime;
	private State state;

	public RoamingBrain(
			Mob mob,
			MobOrderExecutor orderExecutor,
			SpawnArea area,
			IntervalInt timePauseAtPoints,
			RandomProvider randomProvider) {
		super(mob, orderExecutor);
		this.area = Preconditions.checkNotNull(area, "area must not be null");
		this.timePauseAtPoints = Preconditions.checkNotNull(timePauseAtPoints, "timePauseAtPoints must not be null");
		this.randomProvider = randomProvider;
		state = State.PAUSING_AT_POINT;
		nextPointTime = 0L;
	}

	@Override
	public MobDecision act(long now) {
		return state.act(this, now);
	}

	@Override
	public S2CProtocol getSmrtReceiver() {
		return S2CEmptyReceiver.getSingleton();
	}

	public SpawnArea getArea() {
		return area;
	}

	public IntervalInt getTimePauseAtPoints() {
		return timePauseAtPoints;
	}

	private MobDecision pauseAtPoint(long now) {
		if (now >= nextPointTime) {
			SpawnPoint newPoint = area.getNextSpawnPoint();
			currentGoal = newPoint.getPosition();
			state = State.MOVING_TOWARDS_POINT;
		}
		return MobDecision.DECIDED;
	}

	private MobDecision moveTowards(long now) {
		if (SpacedVector3.distanceSq(mob.getPosition(), currentGoal) <= MobOrderExecutor.CLOSE_ENOUGH_SQ) {
			state = State.PAUSING_AT_POINT;
			nextPointTime = now + randomProvider.getInteger(timePauseAtPoints);
		} else {
			TransportationMode mode = mob.getSlowTransportationMode();
			orderExecutor.moveTo(mob, currentGoal, mode);
		}
		return MobDecision.DECIDED;
	}


	private enum State {
		MOVING_TOWARDS_POINT {
			@Override
			MobDecision act(RoamingBrain brain, long now) {
				return brain.moveTowards(now);
			}
		},
		PAUSING_AT_POINT {
			@Override
			MobDecision act(RoamingBrain brain, long now) {
				return brain.pauseAtPoint(now);
			}
		};

		abstract MobDecision act(RoamingBrain brain, long now);

	}
}
