package se.spaced.server.mob.brains;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.spaced.messages.protocol.s2c.S2CEmptyReceiver;
import se.spaced.messages.protocol.s2c.S2CProtocol;
import se.spaced.server.mob.MobDecision;
import se.spaced.server.model.Mob;

public final class NullBrain extends AbstractMobBrain {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private final Exception exception;

	public NullBrain(Mob mob) {
		this(mob, null);

	}

	public NullBrain(Mob mob, Exception exception) {
		super(mob, null);
		this.exception = exception;
	}

	@Override
	public MobDecision act(long now) {
		if (exception != null) {
			logger.debug("Computer says no: " + exception.getMessage(), exception);
		} else {
			logger.debug("Computer says no: ");
		}
		return MobDecision.UNDECIDED;
	}

	@Override
	public S2CProtocol getSmrtReceiver() {
		return S2CEmptyReceiver.getSingleton();
	}
}
