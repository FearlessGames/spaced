package se.spaced.server.mob.brains.templates;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;
import se.fearlessgames.common.util.uuid.UUID;
import se.spaced.server.mob.MobInfoProvider;
import se.spaced.server.mob.MobOrderExecutor;
import se.spaced.server.mob.brains.AttackingBrain;
import se.spaced.server.mob.brains.MobBrain;
import se.spaced.server.model.Mob;
import se.spaced.server.model.combat.EntityTargetService;
import se.spaced.server.model.spawn.BrainParameterProvider;
import se.spaced.server.model.spawn.MobSpawnTemplate;
import se.spaced.server.model.spawn.MobTemplate;
import se.spaced.server.model.spawn.area.SpawnArea;
import se.spaced.server.tools.spawnpattern.view.InputType;

import javax.persistence.Entity;
import javax.persistence.Transient;

@Entity
public class AttackingBrainTemplate extends BrainTemplate {
	@Transient
	private final MobOrderExecutor orderExecutor;
	@Transient
	private final MobInfoProvider mobInfoProvider;
	@Transient
	private final EntityTargetService entityTargetService;

	@Inject
	protected AttackingBrainTemplate(
			MobOrderExecutor orderExecutor, MobInfoProvider mobInfoProvider, EntityTargetService entityTargetService) {
		this(orderExecutor, mobInfoProvider, entityTargetService, null, null);
	}

	public AttackingBrainTemplate(
			MobOrderExecutor orderExecutor,
			MobInfoProvider mobInfoProvider,
			EntityTargetService entityTargetService,
			UUID pk, String name) {
		super(pk, name);
		this.orderExecutor = orderExecutor;
		this.mobInfoProvider = mobInfoProvider;
		this.entityTargetService = entityTargetService;
	}

	@Override
	public MobBrain createBrain(Mob mob, SpawnArea spawnArea, BrainParameterProvider brainParameterProvider) {
		return new AttackingBrain(orderExecutor,
				mobInfoProvider,
				entityTargetService,
				mob,
				brainParameterProvider.getAttackingParameters());
	}

	@Override
	public ImmutableSet<BrainParameter> getRequiredParameters() {
		BrainParameter move = new BrainParameter() {
			@Override
			public Class<? extends BrainTemplate> getBrain() {
				return AttackingBrainTemplate.class;
			}

			@Override
			public String getName() {
				return "Move to target";
			}

			@Override
			public Object retrieveValue(MobTemplate mobTemplate, MobSpawnTemplate mobSpawnTemplate) {
				return mobTemplate.isMoveToTarget();
			}

			@Override
			public void updateValue(
					MobSpawnTemplate mobSpawnTemplate,
					Object parameter) {
				throw new UnsupportedOperationException("Can't update mobtemplate data from this view");
			}

			@Override
			public InputType getType() {
				return InputType.BOOOLEAN;
			}

			@Override
			public boolean isEditable() {
				return false;
			}
		};
		BrainParameter look = new BrainParameter() {
			@Override
			public Class<? extends BrainTemplate> getBrain() {
				return AttackingBrainTemplate.class;
			}

			@Override
			public String getName() {
				return "Look at target";
			}

			@Override
			public Object retrieveValue(MobTemplate mobTemplate, MobSpawnTemplate mobSpawnTemplate) {
				return mobTemplate.isLookAtTarget();
			}

			@Override
			public void updateValue(
					MobSpawnTemplate mobSpawnTemplate,
					Object parameter) {
				throw new UnsupportedOperationException("Can't update mobtemplate data from this view");
			}

			@Override
			public InputType getType() {
				return InputType.BOOOLEAN;
			}

			@Override
			public boolean isEditable() {
				return false;
			}
		};
		return ImmutableSet.of(move, look);
	}
}
