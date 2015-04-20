package se.spaced.server.model.action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.spaced.server.model.ServerEntity;
import se.spaced.server.model.spell.effect.ProjectilEffectInstance;

public class ProjectileAction extends TargetedAction {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	private static final double IMPACT_RANGE = 0.001;
	private static final double SHORT_RANGE = 0.3;

	private final ActionScheduler actionScheduler;
	private long previousTime;
	private final ProjectilEffectInstance projectileEffectInstance;

	public ProjectileAction(long executionTime, ServerEntity performer, ServerEntity target, ActionScheduler actionScheduler,
										  ProjectilEffectInstance projectileEffectInstance) {
		super(executionTime, performer, target);
		this.projectileEffectInstance = projectileEffectInstance;

		this.actionScheduler = actionScheduler;
		previousTime = executionTime;
	}

	@Override
	public void perform() {
		long delta = executionTime - previousTime;
		previousTime = executionTime;

		double remainingTravelTime = projectileEffectInstance.move(delta / 1000.0);

		if (remainingTravelTime < IMPACT_RANGE) {
			logger.debug("Projectile is at target - impact");
			projectileEffectInstance.onImpact(executionTime);
		} else if (remainingTravelTime < SHORT_RANGE) {
			logger.debug("Projectile within short range, still {} to go", projectileEffectInstance.getTargetDistance());
			reschedule(remainingTravelTime, 1);
		} else {
			logger.debug("Projectile not there yet, still {} to go. delta {}", projectileEffectInstance.getTargetDistance(), delta);
			reschedule(remainingTravelTime, 2);
		}
	}


	private void reschedule(double time, int halfLifeComponent) {
		double travelTime = time / halfLifeComponent;
		long newExecutionTime = (long) (executionTime + travelTime * 1000);
		actionScheduler.reschedule(this, newExecutionTime);
	}
}
