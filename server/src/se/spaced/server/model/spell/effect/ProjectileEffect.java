package se.spaced.server.model.spell.effect;

import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import se.spaced.messages.protocol.s2c.S2CProtocol;
import se.spaced.server.model.ServerEntity;
import se.spaced.server.model.action.ActionScheduler;
import se.spaced.server.model.action.ProjectileAction;
import se.spaced.server.net.broadcast.SmrtBroadcaster;
import se.spaced.shared.model.MagicSchool;
import se.spaced.shared.util.Constants;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

@Entity
public class ProjectileEffect extends Effect {

	@Transient
	private final ActionScheduler actionScheduler;
	

	@Transient
	private final AtomicInteger projectileIdCounter;

	private double speed = Constants.SPEED_OF_LIGHT;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@Fetch(FetchMode.SUBSELECT)
	private final Set<Effect> impactEffects = Sets.newHashSet();

	@Inject
	public ProjectileEffect(
			ActionScheduler actionScheduler, SmrtBroadcaster<S2CProtocol> smrtBroadcaster,
			@Named("projectileId") AtomicInteger projectileIdCounter) {
		super(MagicSchool.PHYSICAL, smrtBroadcaster);
		this.actionScheduler = actionScheduler;
		this.projectileIdCounter = projectileIdCounter;
	}

	public void setSpeed(double speed) {
		this.speed = speed;
	}

	@Override
	public void apply(long now, ServerEntity performer, ServerEntity target, String causeName) {
		ProjectilEffectInstance projectileEffectInstance = new ProjectilEffectInstance(speed, impactEffects, performer, target, causeName, smrtBroadcaster,
				projectileIdCounter.incrementAndGet());
		ProjectileAction projectileAction = new ProjectileAction(now, performer, target, actionScheduler, projectileEffectInstance);
		smrtBroadcaster.create().toCombat(performer, target).send().projectile().
				homingProjectileCreated(projectileEffectInstance.getProjectileId(), performer, target, getResourceName(), speed);
		actionScheduler.add(projectileAction);
	}

	@Override
	public void fail(long now, ServerEntity performer, ServerEntity target, String causeName) {
		for (Effect impactEffect : impactEffects) {
			impactEffect.fail(now, performer, target, causeName);
		}
	}

	public void addImpactEffect(Effect effect) {
		impactEffects.add(effect);
	}

	public Set<Effect> getImpactEffects() {
		return impactEffects;
	}
}
