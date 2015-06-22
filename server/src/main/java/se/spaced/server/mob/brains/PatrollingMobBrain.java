package se.spaced.server.mob.brains;

import com.google.common.collect.Iterables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.ardortech.math.SpacedVector3;
import se.spaced.messages.protocol.s2c.S2CEmptyReceiver;
import se.spaced.messages.protocol.s2c.S2CProtocol;
import se.spaced.server.mob.MobDecision;
import se.spaced.server.mob.MobOrderExecutor;
import se.spaced.server.model.Mob;
import se.spaced.server.model.movement.TransportationMode;

import java.util.Iterator;

public class PatrollingMobBrain extends AbstractMobBrain {
	private static final Logger log = LoggerFactory.getLogger(PatrollingMobBrain.class);
	private static final int MAX_LOOP_TIMES = 10;
	private static final double EPSILON_SQ = 0.5 * 0.5;

	private SpacedVector3 currentGoal;
	private final Iterator<? extends SpacedVector3> patrolPathIterator;


	public PatrollingMobBrain(
			Mob mob,
			Iterable<? extends SpacedVector3> patrolPath,
			MobOrderExecutor orderExecutor) {
		super(mob, orderExecutor);
		patrolPathIterator = Iterables.cycle(Iterables.unmodifiableIterable(patrolPath)).iterator();
	}

	@Override
	public MobDecision act(long now) {

		if (!patrolPathIterator.hasNext()) {
			log.error("Patrolling brain have an empty path for mob {} ", mob.getName());
		}

		for (int i = 0; i < MAX_LOOP_TIMES && needsNewGoal() && patrolPathIterator.hasNext(); i++) {
			currentGoal = patrolPathIterator.next();
		}
		if (currentGoal != null) {
			TransportationMode mode = mob.getSlowTransportationMode();
			orderExecutor.moveTo(mob, currentGoal, mode);
		}
		return MobDecision.DECIDED;
	}

	@Override
	public S2CProtocol getSmrtReceiver() {
		return S2CEmptyReceiver.getSingleton();
	}

	private boolean needsNewGoal() {
		if (currentGoal == null) {
			return true;
		} else {
			SpacedVector3 position = mob.getPosition();
			return SpacedVector3.distanceSq(position, currentGoal) < EPSILON_SQ;
		}
	}

	public Iterator<? extends SpacedVector3> getPatrolPathIterator() {
		return patrolPathIterator;
	}
}
