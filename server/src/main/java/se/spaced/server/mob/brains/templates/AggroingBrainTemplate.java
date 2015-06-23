package se.spaced.server.mob.brains.templates;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;
import se.fearless.common.time.TimeProvider;
import se.spaced.server.mob.MobOrderExecutor;
import se.spaced.server.mob.brains.AggroingBrain;
import se.spaced.server.mob.brains.MobBrain;
import se.spaced.server.model.Mob;
import se.spaced.server.model.combat.EntityCombatService;
import se.spaced.server.model.relations.RelationsService;
import se.spaced.server.model.spawn.BrainParameterProvider;
import se.spaced.server.model.spawn.MobSpawnTemplate;
import se.spaced.server.model.spawn.MobTemplate;
import se.spaced.server.model.spawn.area.SpawnArea;
import se.spaced.server.tools.spawnpattern.view.InputType;

import javax.persistence.Entity;
import javax.persistence.Transient;

@Entity
public class AggroingBrainTemplate extends BrainTemplate {
	@Transient
	private final MobOrderExecutor orderExecutor;
	@Transient
	private final TimeProvider timeProvider;
	@Transient
	private final RelationsService relationsService;
	@Transient
	private final EntityCombatService entityCombatService;
	public static final String PROXIMITY = "Aggro proximity distance";
	public static final String SOCIAL = "Social proximity distance";

	@Inject
	public AggroingBrainTemplate(
			MobOrderExecutor orderExecutor,
			TimeProvider timeProvider,
			RelationsService relationsService,
			EntityCombatService entityCombatService) {
		super(null, null);
		this.orderExecutor = orderExecutor;
		this.timeProvider = timeProvider;
		this.relationsService = relationsService;
		this.entityCombatService = entityCombatService;
	}

	@Override
	public MobBrain createBrain(Mob mob, SpawnArea spawnArea, BrainParameterProvider brainParameterProvider) {
		return new AggroingBrain(mob,
				orderExecutor,
				timeProvider,
				relationsService,
				brainParameterProvider.getProximityAggroParameters(),
				entityCombatService);
	}

	@Override
	public ImmutableSet<BrainParameter> getRequiredParameters() {
		BrainParameter proximity = new BrainParameter() {
			@Override
			public Class<? extends BrainTemplate> getBrain() {
				return AggroingBrainTemplate.class;
			}

			@Override
			public String getName() {
				return PROXIMITY;
			}

			@Override
			public Object retrieveValue(MobTemplate mobTemplate, MobSpawnTemplate mobSpawnTemplate) {
				return mobTemplate.getProximityAggroDistance();
			}

			@Override
			public void updateValue(
					MobSpawnTemplate mobSpawnTemplate,
					Object parameter) {
				throw new UnsupportedOperationException("Can't update mobtemplate data from this view");
			}

			@Override
			public InputType getType() {
				return InputType.NUMERIC;
			}

			@Override
			public boolean isEditable() {
				return false;
			}
		};

		BrainParameter social = new BrainParameter() {
			@Override
			public Class<? extends BrainTemplate> getBrain() {
				return AggroingBrainTemplate.class;
			}

			@Override
			public String getName() {
				return SOCIAL;
			}

			@Override
			public Object retrieveValue(MobTemplate mobTemplate, MobSpawnTemplate mobSpawnTemplate) {
				return mobTemplate.getSocialAggroDistance();
			}

			@Override
			public void updateValue(
					MobSpawnTemplate mobSpawnTemplate,
					Object parameter) {
				throw new UnsupportedOperationException("Can't update mobtemplate data from this view");
			}

			@Override
			public InputType getType() {
				return InputType.NUMERIC;
			}

			@Override
			public boolean isEditable() {
				return false;
			}
		};
		return ImmutableSet.of(proximity, social);
	}
}
