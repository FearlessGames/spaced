package se.spaced.shared.model.stats;

import se.fearlessgames.common.publisher.Subscriber;
import se.fearlessgames.common.util.TimeProvider;
import se.spaced.shared.util.math.LinearTimeValue;

public class ShieldStrength extends SimpleStat implements Subscriber<Stat> {
	private final TimeProvider timeProvider;
	private final AbstractStat maxShieldPower;
	private final AbstractStat shieldRecoveryRate;
	private final LinearTimeValue recoveryTimer;

	public ShieldStrength(TimeProvider timeProvider, AbstractStat maxShieldPower, AbstractStat shieldRecoveryRate) {
		super("Shield Power", maxShieldPower.getValue());
		this.timeProvider = timeProvider;
		this.maxShieldPower = maxShieldPower;
		this.shieldRecoveryRate = shieldRecoveryRate;

		recoveryTimer = new LinearTimeValue(maxShieldPower.getValue());
		recoveryTimer.setValue(timeProvider.now(), maxShieldPower.getValue());
		recoveryTimer.setCurrentRate(timeProvider.now(), shieldRecoveryRate.getValue());

		maxShieldPower.subscribe(this);
		shieldRecoveryRate.subscribe(this);
	}

	@Override
	public double getValue() {
		return recoveryTimer.getValue(timeProvider.now());
	}


	@Override
	public void changeValue(double value) {
		recoveryTimer.setValue(timeProvider.now(), value);
		publisher.updateSubscribers(this);
	}

	@Override
	public void increaseValue(double amount) {
		changeValue(getValue() + amount);
	}

	@Override
	public void decreaseValue(double amount) {
		changeValue(getValue() - amount);
	}


	@Override
	public void update(Stat updated) {
		recoveryTimer.setCurrentRate(timeProvider.now(), shieldRecoveryRate.getValue());
		recoveryTimer.setMaxValue(timeProvider.now(), maxShieldPower.getValue());
		publisher.updateSubscribers(this);
	}
}
